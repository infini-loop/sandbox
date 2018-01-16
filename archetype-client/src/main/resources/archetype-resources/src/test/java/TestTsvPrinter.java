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

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import static ${package}.TestAlignedTablePrinter.row;
import static ${package}.TestAlignedTablePrinter.rows;
import static org.testng.Assert.assertEquals;

public class TestTsvPrinter
{
    @Test
    public void testTsvPrinting()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new TsvPrinter(fieldNames, writer, true);

        printer.printRows(rows(
                row("hello", "world", 123),
                row("a", null, 4.5),
                row("some long${symbol_escape}ntext${symbol_escape}tdone", "more${symbol_escape}ntext", 4567),
                row("bye", "done", -15),
                row("oops${symbol_escape}0a${symbol_escape}nb${symbol_escape}rc${symbol_escape}bd${symbol_escape}fe${symbol_escape}tf${symbol_escape}${symbol_escape}g${symbol_escape}1done", "escape", 9)),
                true);
        printer.finish();

        String expected = "" +
                "first${symbol_escape}tlast${symbol_escape}tquantity${symbol_escape}n" +
                "hello${symbol_escape}tworld${symbol_escape}t123${symbol_escape}n" +
                "a${symbol_escape}t${symbol_escape}t4.5${symbol_escape}n" +
                "some long${symbol_escape}${symbol_escape}ntext${symbol_escape}${symbol_escape}tdone${symbol_escape}tmore${symbol_escape}${symbol_escape}ntext${symbol_escape}t4567${symbol_escape}n" +
                "bye${symbol_escape}tdone${symbol_escape}t-15${symbol_escape}n" +
                "oops${symbol_escape}${symbol_escape}0a${symbol_escape}${symbol_escape}nb${symbol_escape}${symbol_escape}rc${symbol_escape}${symbol_escape}bd${symbol_escape}${symbol_escape}fe${symbol_escape}${symbol_escape}tf${symbol_escape}${symbol_escape}${symbol_escape}${symbol_escape}g${symbol_escape}1done${symbol_escape}tescape${symbol_escape}t9${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testTsvPrintingNoRows()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last");
        OutputPrinter printer = new TsvPrinter(fieldNames, writer, true);

        printer.finish();

        assertEquals(writer.getBuffer().toString(), "first${symbol_escape}tlast${symbol_escape}n");
    }

    @Test
    public void testTsvPrintingNoHeader()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new TsvPrinter(fieldNames, writer, false);

        printer.printRows(rows(
                row("hello", "world", 123),
                row("a", null, 4.5)),
                true);
        printer.finish();

        String expected = "" +
                "hello${symbol_escape}tworld${symbol_escape}t123${symbol_escape}n" +
                "a${symbol_escape}t${symbol_escape}t4.5${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testCsvVarbinaryPrinting()
            throws IOException
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new TsvPrinter(fieldNames, writer, false);

        printer.printRows(rows(row("hello".getBytes(), null, 123)), true);
        printer.finish();

        String expected = "68 65 6c 6c 6f${symbol_escape}t${symbol_escape}t123${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }
}
