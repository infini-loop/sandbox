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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

public class TestAlignedTablePrinter
{
    @Test
    public void testAlignedPrinting()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new AlignedTablePrinter(fieldNames, writer);

        printer.printRows(rows(
                row("hello", "world", 123),
                row("a", null, 4.5),
                row("some long${symbol_escape}ntext that${symbol_escape}ndoes not${symbol_escape}nfit on${symbol_escape}none line", "more${symbol_escape}ntext", 4567),
                row("bye", "done", -15)),
                true);
        printer.finish();

        String expected = "" +
                "   first   | last  | quantity ${symbol_escape}n" +
                "-----------+-------+----------${symbol_escape}n" +
                " hello     | world |      123 ${symbol_escape}n" +
                " a         | NULL  |      4.5 ${symbol_escape}n" +
                " some long+| more +|     4567 ${symbol_escape}n" +
                " text that+| text  |          ${symbol_escape}n" +
                " does not +|       |          ${symbol_escape}n" +
                " fit on   +|       |          ${symbol_escape}n" +
                " one line  |       |          ${symbol_escape}n" +
                " bye       | done  |      -15 ${symbol_escape}n" +
                "(4 rows)${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testAlignedPrintingOneRow()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last");
        OutputPrinter printer = new AlignedTablePrinter(fieldNames, writer);

        printer.printRows(rows(row("a long line${symbol_escape}nwithout wrapping", "text")), true);
        printer.finish();

        String expected = "" +
                "      first       | last ${symbol_escape}n" +
                "------------------+------${symbol_escape}n" +
                " a long line      | text ${symbol_escape}n" +
                " without wrapping |      ${symbol_escape}n" +
                "(1 row)${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testAlignedPrintingNoRows()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last");
        OutputPrinter printer = new AlignedTablePrinter(fieldNames, writer);

        printer.finish();

        String expected = "" +
                " first | last ${symbol_escape}n" +
                "-------+------${symbol_escape}n" +
                "(0 rows)${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testAlignedPrintingHex()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "binary", "last");
        OutputPrinter printer = new AlignedTablePrinter(fieldNames, writer);

        printer.printRows(rows(
                row("hello", bytes("hello"), "world"),
                row("a", bytes("some long text that is more than 16 bytes"), "b"),
                row("cat", bytes(""), "dog")),
                true);
        printer.finish();

        String expected = "" +
                " first |                     binary                      | last  ${symbol_escape}n" +
                "-------+-------------------------------------------------+-------${symbol_escape}n" +
                " hello | 68 65 6c 6c 6f                                  | world ${symbol_escape}n" +
                " a     | 73 6f 6d 65 20 6c 6f 6e 67 20 74 65 78 74 20 74+| b     ${symbol_escape}n" +
                "       | 68 61 74 20 69 73 20 6d 6f 72 65 20 74 68 61 6e+|       ${symbol_escape}n" +
                "       | 20 31 36 20 62 79 74 65 73                      |       ${symbol_escape}n" +
                " cat   |                                                 | dog   ${symbol_escape}n" +
                "(3 rows)${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testAlignedPrintingWideCharacters()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("go${symbol_escape}u7f51", "last", "quantity${symbol_escape}u7f51");
        OutputPrinter printer = new AlignedTablePrinter(fieldNames, writer);

        printer.printRows(rows(
                row("hello", "wide${symbol_escape}u7f51", 123),
                row("some long${symbol_escape}ntext ${symbol_escape}u7f51${symbol_escape}ndoes not${symbol_escape}u7f51${symbol_escape}nfit", "more${symbol_escape}ntext", 4567),
                row("bye", "done", -15)),
                true);
        printer.finish();

        String expected = "" +
                "    go${symbol_escape}u7f51    |  last  | quantity${symbol_escape}u7f51 ${symbol_escape}n" +
                "------------+--------+------------${symbol_escape}n" +
                " hello      | wide${symbol_escape}u7f51 |        123 ${symbol_escape}n" +
                " some long +| more  +|       4567 ${symbol_escape}n" +
                " text ${symbol_escape}u7f51   +| text   |            ${symbol_escape}n" +
                " does not${symbol_escape}u7f51+|        |            ${symbol_escape}n" +
                " fit        |        |            ${symbol_escape}n" +
                " bye        | done   |        -15 ${symbol_escape}n" +
                "(3 rows)${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    static List<?> row(Object... values)
    {
        return asList(values);
    }

    static List<List<?>> rows(List<?>... rows)
    {
        return asList(rows);
    }

    static byte[] bytes(String s)
    {
        return s.getBytes(UTF_8);
    }
}
