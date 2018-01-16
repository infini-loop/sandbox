#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ${package};

import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.io.StringWriter;
import java.util.List;

import static ${package}.TestAlignedTablePrinter.bytes;
import static ${package}.TestAlignedTablePrinter.row;
import static ${package}.TestAlignedTablePrinter.rows;
import static org.testng.Assert.assertEquals;

@SuppressWarnings("Duplicates")
public class TestVerticalRecordPrinter
{
    @Test
    public void testVerticalPrinting()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new VerticalRecordPrinter(fieldNames, writer);

        printer.printRows(rows(
                row("hello", "world", 123),
                row("a", null, 4.5),
                row("some long${symbol_escape}ntext that${symbol_escape}ndoes not${symbol_escape}nfit on${symbol_escape}none line", "more${symbol_escape}ntext", 4567),
                row("bye", "done", -15)),
                true);
        printer.finish();

        String expected = "" +
                "-[ RECORD 1 ]-------${symbol_escape}n" +
                "first    | hello${symbol_escape}n" +
                "last     | world${symbol_escape}n" +
                "quantity | 123${symbol_escape}n" +
                "-[ RECORD 2 ]-------${symbol_escape}n" +
                "first    | a${symbol_escape}n" +
                "last     | NULL${symbol_escape}n" +
                "quantity | 4.5${symbol_escape}n" +
                "-[ RECORD 3 ]-------${symbol_escape}n" +
                "first    | some long${symbol_escape}n" +
                "         | text that${symbol_escape}n" +
                "         | does not${symbol_escape}n" +
                "         | fit on${symbol_escape}n" +
                "         | one line${symbol_escape}n" +
                "last     | more${symbol_escape}n" +
                "         | text${symbol_escape}n" +
                "quantity | 4567${symbol_escape}n" +
                "-[ RECORD 4 ]-------${symbol_escape}n" +
                "first    | bye${symbol_escape}n" +
                "last     | done${symbol_escape}n" +
                "quantity | -15${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testVerticalShortName()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("a");
        OutputPrinter printer = new VerticalRecordPrinter(fieldNames, writer);

        printer.printRows(rows(row("x")), true);
        printer.finish();

        String expected = "" +
                "-[ RECORD 1 ]${symbol_escape}n" +
                "a | x${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testVerticalLongName()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("shippriority");
        OutputPrinter printer = new VerticalRecordPrinter(fieldNames, writer);

        printer.printRows(rows(row("hello")), true);
        printer.finish();

        String expected = "" +
                "-[ RECORD 1 ]+------${symbol_escape}n" +
                "shippriority | hello${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testVerticalLongerName()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("order_priority");
        OutputPrinter printer = new VerticalRecordPrinter(fieldNames, writer);

        printer.printRows(rows(row("hello")), true);
        printer.finish();

        String expected = "" +
                "-[ RECORD 1 ]--+------${symbol_escape}n" +
                "order_priority | hello${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testVerticalWideCharacterName()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("order_priority${symbol_escape}u7f51");
        OutputPrinter printer = new VerticalRecordPrinter(fieldNames, writer);

        printer.printRows(rows(row("hello")), true);
        printer.finish();

        String expected = "" +
                "-[ RECORD 1 ]----+------${symbol_escape}n" +
                "order_priority${symbol_escape}u7f51 | hello${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testVerticalWideCharacterValue()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("name");
        OutputPrinter printer = new VerticalRecordPrinter(fieldNames, writer);

        printer.printRows(rows(row("hello${symbol_escape}u7f51 bye")), true);
        printer.finish();

        String expected = "" +
                "-[ RECORD 1 ]-----${symbol_escape}n" +
                "name | hello${symbol_escape}u7f51 bye${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testVerticalPrintingNoRows()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("none");
        OutputPrinter printer = new VerticalRecordPrinter(fieldNames, writer);

        printer.finish();

        assertEquals(writer.getBuffer().toString(), "(no rows)${symbol_escape}n");
    }

    @Test
    public void testVerticalPrintingHex()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "binary", "last");
        OutputPrinter printer = new VerticalRecordPrinter(fieldNames, writer);

        printer.printRows(rows(
                row("hello", bytes("hello"), "world"),
                row("a", bytes("some long text that is more than 16 bytes"), "b"),
                row("cat", bytes(""), "dog")),
                true);
        printer.finish();

        String expected = "" +
                "-[ RECORD 1 ]-------------------------------------------${symbol_escape}n" +
                "first  | hello${symbol_escape}n" +
                "binary | 68 65 6c 6c 6f${symbol_escape}n" +
                "last   | world${symbol_escape}n" +
                "-[ RECORD 2 ]-------------------------------------------${symbol_escape}n" +
                "first  | a${symbol_escape}n" +
                "binary | 73 6f 6d 65 20 6c 6f 6e 67 20 74 65 78 74 20 74${symbol_escape}n" +
                "       | 68 61 74 20 69 73 20 6d 6f 72 65 20 74 68 61 6e${symbol_escape}n" +
                "       | 20 31 36 20 62 79 74 65 73${symbol_escape}n" +
                "last   | b${symbol_escape}n" +
                "-[ RECORD 3 ]-------------------------------------------${symbol_escape}n" +
                "first  | cat${symbol_escape}n" +
                "binary | ${symbol_escape}n" +
                "last   | dog${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }
}
