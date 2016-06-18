/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 KuiGang Wang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lt.compiler.library;

import lt.repl.Compiler;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.*;

/**
 * test libraries
 */
public class TestLibraries {
        public static ClassLoader load(Map<String, Object> map) throws Exception {
                Compiler compiler = new Compiler();
                return compiler.compile(map);
        }

        @Test
        public void testHtml() throws Exception {
                ClassLoader loader = load(new HashMap<String, Object>() {{
                        try {
                                ClassLoader.getSystemClassLoader().loadClass("lt.dsl.html.HTMLElement");
                        } catch (ClassNotFoundException ignore) {
                                put("html.lt", TestLibraries.class.getResourceAsStream("/lt/html.lt"));
                        }
                        put("test_html.lt", TestLibraries.class.getResourceAsStream("/test_libraries/test_html.lt"));
                }});
                Class<?> TestHtml = loader.loadClass("lt.dsl.html.test.TestHtml");
                Class<?> Html = loader.loadClass("lt.dsl.html.html");
                Object html = Html.newInstance(); // html=html()

                Method testConstructHtml = TestHtml.getMethod("testConstructHtml");

                assertEquals(html, testConstructHtml.invoke(null));

                Method testHtmlFormat = TestHtml.getMethod("testHtmlFormat");
                assertEquals("" +
                        "<html>" +
                        "<head>" +
                        "</head>" +
                        "<body>" +
                        "<form>" +
                        "<input value='hello&nbsp;world' type='text'>" +
                        "</form>" +
                        "</body>" +
                        "</html>", testHtmlFormat.invoke(null).toString());

                Method testHtmlEscape = TestHtml.getMethod("testHtmlEscape");
                assertEquals("" +
                                "<html onload='window.location.href=&quot;page&quot;'>" +
                                "&nbsp;&gt;&lt;&amp;&quot;" +
                                "</html>"
                        , testHtmlEscape.invoke(null).toString());
        }

        @Test
        public void testSQL() throws Exception {
                ClassLoader loader = load(new HashMap<String, Object>() {{
                        try {
                                ClassLoader.getSystemClassLoader().loadClass("lt.dsl.sql.SQL");
                        } catch (ClassNotFoundException ignore) {
                                put("sql.lt", TestLibraries.class.getResourceAsStream("/lt/sql.lt"));
                        }
                        put("test_sql.lt", TestLibraries.class.getResourceAsStream("/test_libraries/test_sql.lt"));
                }});
                Class<?> TestSQL = loader.loadClass("lt.dsl.sql.test.TestSQL");

                Method testSelectString = TestSQL.getMethod("testSelectString");
                assertEquals("select `User`.`id`, `User`.`name` from `User` where `User`.`id` > ? order by `User`.`id` desc limit ?,?",
                        testSelectString.invoke(null));

                Method testSelectAlias = TestSQL.getMethod("testSelectAlias");
                assertEquals("select `User`.`id` as theId, `User`.`name` as theName from `User`",
                        testSelectAlias.invoke(null));

                Method testSelectWithSubQuery = TestSQL.getMethod("testSelectWithSubQuery");
                assertEquals("select `User`.`id`, `User`.`name` from `User` where `User`.`id` <= " +
                                "(select `User`.`id` from `User` limit ?) order by `User`.`id` desc",
                        testSelectWithSubQuery.invoke(null));

                Method testInsertString = TestSQL.getMethod("testInsertString");
                assertEquals("insert into `User` (`id`, `name`) values (?, ?)",
                        testInsertString.invoke(null));

                Method testUpdateString = TestSQL.getMethod("testUpdateString");
                assertEquals("update `User` set `User`.`id` = ? where `User`.`name` = ?",
                        testUpdateString.invoke(null));

                Method testDeleteString = TestSQL.getMethod("testDeleteString");
                assertEquals("delete from `User` where `User`.`id` = ?",
                        testDeleteString.invoke(null));

                Method testAndOr = TestSQL.getMethod("testAndOr");
                assertEquals("select User.name from User where (User.id > ? and User.name <> ?) or User.id < ?", testAndOr.invoke(null));

                Method testAllQueries = TestSQL.getMethod("testAllQueries");
                assertEquals(Arrays.asList(
                        "select distinct User.name from User",
                        "select count(User.name) from User",
                        "select mid(User.name, 1) from User",
                        "select mid(User.name, 1) as a from User",
                        "select User.name from User union select User.name from User union select User.name from User",
                        "select User.id into Tbl from User where User.name = ?"
                ), testAllQueries.invoke(null));
        }
}
