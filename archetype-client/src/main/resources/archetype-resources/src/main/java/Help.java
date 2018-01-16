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

public final class Help
{
    private Help() {}

    public static String getHelpText()
    {
        return "" +
                "Supported commands:${symbol_escape}n" +
                "QUIT${symbol_escape}n" +
                "EXPLAIN [ ( option [, ...] ) ] <query>${symbol_escape}n" +
                "    options: FORMAT { TEXT | GRAPHVIZ }${symbol_escape}n" +
                "             TYPE { LOGICAL | DISTRIBUTED }${symbol_escape}n" +
                "DESCRIBE <table>${symbol_escape}n" +
                "SHOW COLUMNS FROM <table>${symbol_escape}n" +
                "SHOW FUNCTIONS${symbol_escape}n" +
                "SHOW CATALOGS [LIKE <pattern>]${symbol_escape}n" +
                "SHOW SCHEMAS [FROM <catalog>] [LIKE <pattern>]${symbol_escape}n" +
                "SHOW TABLES [FROM <schema>] [LIKE <pattern>]${symbol_escape}n" +
                "SHOW PARTITIONS FROM <table> [WHERE ...] [ORDER BY ...] [LIMIT n]${symbol_escape}n" +
                "USE [<catalog>.]<schema>${symbol_escape}n" +
                "";
    }
}
