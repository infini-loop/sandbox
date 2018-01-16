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

public class TestCsvPrinter
{
    @Test
    public void testCsvPrinting()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new CsvPrinter(fieldNames, writer, true);

        printer.printRows(rows(
                row("hello", "world", 123),
                row("a", null, 4.5),
                row("some long${symbol_escape}ntext that${symbol_escape}ndoes not${symbol_escape}nfit on${symbol_escape}none line", "more${symbol_escape}ntext", 4567),
                row("bye", "done", -15)),
                true);
        printer.finish();

        String expected = "" +
                "${symbol_escape}"first${symbol_escape}",${symbol_escape}"last${symbol_escape}",${symbol_escape}"quantity${symbol_escape}"${symbol_escape}n" +
                "${symbol_escape}"hello${symbol_escape}",${symbol_escape}"world${symbol_escape}",${symbol_escape}"123${symbol_escape}"${symbol_escape}n" +
                "${symbol_escape}"a${symbol_escape}",${symbol_escape}"${symbol_escape}",${symbol_escape}"4.5${symbol_escape}"${symbol_escape}n" +
                "${symbol_escape}"some long${symbol_escape}n" +
                "text that${symbol_escape}n" +
                "does not${symbol_escape}n" +
                "fit on${symbol_escape}n" +
                "one line${symbol_escape}",${symbol_escape}"more${symbol_escape}n" +
                "text${symbol_escape}",${symbol_escape}"4567${symbol_escape}"${symbol_escape}n" +
                "${symbol_escape}"bye${symbol_escape}",${symbol_escape}"done${symbol_escape}",${symbol_escape}"-15${symbol_escape}"${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testCsvPrintingNoRows()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last");
        OutputPrinter printer = new CsvPrinter(fieldNames, writer, true);

        printer.finish();

        assertEquals(writer.getBuffer().toString(), "${symbol_escape}"first${symbol_escape}",${symbol_escape}"last${symbol_escape}"${symbol_escape}n");
    }

    @Test
    public void testCsvPrintingNoHeader()
            throws Exception
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new CsvPrinter(fieldNames, writer, false);

        printer.printRows(rows(
                row("hello", "world", 123),
                row("a", null, 4.5)),
                true);
        printer.finish();

        String expected = "" +
                "${symbol_escape}"hello${symbol_escape}",${symbol_escape}"world${symbol_escape}",${symbol_escape}"123${symbol_escape}"${symbol_escape}n" +
                "${symbol_escape}"a${symbol_escape}",${symbol_escape}"${symbol_escape}",${symbol_escape}"4.5${symbol_escape}"${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }

    @Test
    public void testCsvVarbinaryPrinting()
            throws IOException
    {
        StringWriter writer = new StringWriter();
        List<String> fieldNames = ImmutableList.of("first", "last", "quantity");
        OutputPrinter printer = new CsvPrinter(fieldNames, writer, false);

        printer.printRows(rows(row("hello".getBytes(), null, 123)), true);
        printer.finish();

        String expected = "${symbol_escape}"68 65 6c 6c 6f${symbol_escape}",${symbol_escape}"${symbol_escape}",${symbol_escape}"123${symbol_escape}"${symbol_escape}n";

        assertEquals(writer.getBuffer().toString(), expected);
    }
}
