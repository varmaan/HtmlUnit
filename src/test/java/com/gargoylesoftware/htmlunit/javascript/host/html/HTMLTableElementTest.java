/*
 * Copyright (c) 2002-2010 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gargoylesoftware.htmlunit.javascript.host.html;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.WebDriverTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Tests for {@link HTMLTableElement}.
 *
 * @version $Revision: 5301 $
 * @author David D. Kilzer
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Daniel Gredler
 * @author Marc Guillemot
 * @author Ahmed Ashour
 */
@RunWith(BrowserRunner.class)
public class HTMLTableElementTest extends WebDriverTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = { "caption1", "caption2", "null", "caption3", "exception", "caption3", "caption4" },
            IE = { "caption1", "caption2", "null", "caption3", "exception", "caption3", "exception", "caption3" })
    public void tableCaptions() throws Exception {
        final String html
            = "<html><head><title>foo</title></head><body>\n"
            + "  <table id='table_1'><caption>caption1</caption><caption>caption2</caption>\n"
            + "    <tr><td>cell1</td><td>cell2</td><td rowspan='2'>cell4</td></tr>\n"
            + "    <tr><td colspan='2'>cell3</td></tr>\n"
            + "  </table>\n"
            + "  <script type='text/javascript' language='JavaScript'>\n"
            + "    var table = document.getElementById('table_1');\n"
            + "    alert(table.caption.innerHTML);\n"
            + "    table.deleteCaption();\n"
            + "    alert(table.caption.innerHTML);\n"
            + "    table.deleteCaption();\n"
            + "    alert(table.caption);\n"
            + "    var newCaption = table.createCaption();\n"
            + "    newCaption.innerHTML = 'caption3';\n"
            + "    alert(table.caption.innerHTML);\n"
            + "    try { table.caption = 123; } catch(e) { alert('exception') }\n"
            + "    alert(table.caption.innerHTML);\n"
            + "    var caption4 = document.createElement('caption');\n"
            + "    caption4.innerHTML = 'caption4';\n"
            + "    try { table.caption = caption4; } catch(e) { alert('exception') }\n"
            + "    alert(table.caption.innerHTML);\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = { "thead1", "thead2", "null", "thead3", "exception", "thead3", "thead4" },
            IE = { "thead1", "thead2", "null", "thead3", "exception", "thead3", "exception", "thead3" })
    public void tableHeaders() throws Exception {
        final String html
            = "<html><head><title>foo</title></head><body>\n"
            + "  <table id='table_1'>\n"
            + "    <thead id='thead1'><tr><td>cell1</td><td>cell2</td><td>cell3</td></tr></thead>\n"
            + "    <thead id='thead2'><tr><td>cell7</td><td>cell8</td><td>cell9</td></tr></thead>\n"
            + "    <tr><td>cell1</td><td>cell2</td><td rowspan='2'>cell4</td></tr>\n"
            + "    <tr><td colspan='2'>cell3</td></tr>\n"
            + "  </table>\n"
            + "  <script type='text/javascript' language='JavaScript'>\n"
            + "    var table = document.getElementById('table_1');\n"
            + "    alert(table.tHead.id);\n"
            + "    table.deleteTHead();\n"
            + "    alert(table.tHead.id);\n"
            + "    table.deleteTHead();\n"
            + "    alert(table.tHead);\n"
            + "    var newTHead = table.createTHead();\n"
            + "    newTHead.id = 'thead3';\n"
            + "    alert(table.tHead.id);\n"
            + "    try { table.tHead = 123; } catch(e) { alert('exception') }\n"
            + "    alert(table.tHead.id);\n"
            + "    var tHead4 = document.createElement('tHead');\n"
            + "    tHead4.id = 'thead4';\n"
            + "    try { table.tHead = tHead4; } catch(e) { alert('exception') }\n"
            + "    alert(table.tHead.id);\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "2", "true", "4 2 2", "6 3 3" })
    public void tableBodies() throws Exception {
        final String html
            = "<html><head><title>foo</title></head><body>\n"
            + "  <table id='table_1'>\n"
            + "    <tbody id='tbody1'>\n"
            + "      <tr><td>cell1</td><td>cell2</td></tr>\n"
            + "      <tr><td>cell3</td><td>cell4</td></tr>\n"
            + "    </tbody>\n"
            + "    <tbody id='tbody2'>\n"
            + "      <tr><td>cell1</td><td>cell2</td></tr>\n"
            + "      <tr><td>cell3</td><td>cell4</td></tr>\n"
            + "    </tbody>\n"
            + "  </table>\n"
            + "  <script type='text/javascript' language='JavaScript'>\n"
            + "    var table = document.getElementById('table_1');\n"
            + "    var bodies = table.tBodies;\n"
            + "    alert(bodies.length);\n"
            + "    alert(bodies == table.tBodies);\n"
            + "    var body1 = table.tBodies[0];\n"
            + "    var body2 = table.tBodies[1];\n"
            + "    alert(table.rows.length + ' ' + body1.rows.length + ' ' + body2.rows.length);\n"
            + "    table.insertRow(-1); // Should add at end, to body2.\n"
            + "    body1.insertRow(-1); // Add one to body1, as well.\n"
            + "    alert(table.rows.length + ' ' + body1.rows.length + ' ' + body2.rows.length);\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "2", "true", "3", "2", "3", "2" })
    public void tableRows() throws Exception {
        final String html
            = "<html><head><title>foo</title></head><body>\n"
            + "  <table id='table_1'>\n"
            + "    <tr><td>cell1</td><td>cell2</td><td rowspan='2'>cell4</td></tr>\n"
            + "    <tr><td colspan='2'>cell3</td></tr>\n"
            + "  </table>\n"
            + "  <script type='text/javascript' language='JavaScript'>\n"
            + "    var table = document.getElementById('table_1');\n"
            + "    var rows = table.rows;\n"
            + "    alert(rows.length);\n"
            + "    alert(rows == table.rows);\n"
            + "    table.insertRow(1);\n"
            + "    alert(rows.length);\n"
            + "    table.deleteRow(1);\n"
            + "    alert(rows.length);\n"
            + "    table.insertRow(rows.length);\n"
            + "    alert(rows.length);\n"
            + "    table.deleteRow(-1);\n"
            + "    alert(rows.length);\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * Regression test for bug 1528024.
     * @see <a href="https://sourceforge.net/tracker/?func=detail&atid=448266&aid=1528024&group_id=47038">bug</a>
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "1", "1" })
    public void tableHeadRows() throws Exception {
        final String html = "<html><head>\n"
            + "<script>\n"
            + "function test() {\n"
            + "  var t = document.getElementById('myTable');\n"
            + "  alert(t.rows[0].cells.length);\n"
            + "  alert(t.rows[1].cells.length);\n"
            + "}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "<table id='myTable'>\n"
            + "<tr><th>Some Heading</th></tr>\n"
            + "<tr><td>some desc</td></tr>\n"
            + "</table>\n"
            + "</body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "2", "true", "8 2 2 2 2",
        "9 2 2 2 3", "8 2 2 1 3", "9 2 3 1 3", "8 1 3 1 3" })
    public void tableRowsWithManySections() throws Exception {
        final String html = "<html><head><title>foo</title></head><body>\n"
            + "  <table id='table_1'>\n"
            + "    <thead>\n"
            + "      <tr><td>cell1</td><td>cell2</td></tr>\n"
            + "      <tr><td>cell3</td><td>cell4</td></tr>\n"
            + "    </thead>\n"
            + "    <tbody id='tbody1'>\n"
            + "      <tr><td>cell1</td><td>cell2</td></tr>\n"
            + "      <tr><td>cell3</td><td>cell4</td></tr>\n"
            + "    </tbody>\n"
            + "    <tbody id='tbody2'>\n"
            + "      <tr><td>cell1</td><td>cell2</td></tr>\n"
            + "      <tr><td>cell3</td><td>cell4</td></tr>\n"
            + "    </tbody>\n"
            + "    <tfoot>\n"
            + "      <tr><td>cell1</td><td>cell2</td></tr>\n"
            + "      <tr><td>cell3</td><td>cell4</td></tr>\n"
            + "    </tfoot>\n"
            + "  </table>\n"
            + "  <script type='text/javascript' language='JavaScript'>\n"
            + "  <!--\n"
            + "    var table = document.getElementById('table_1');\n"
            + "    var bodies = table.tBodies;\n"
            + "    alert(bodies.length);\n"
            + "    alert(bodies == table.tBodies);\n"
            + "    var head = table.tHead;\n"
            + "    var body1 = table.tBodies.item(0);\n"
            + "    var body2 = table.tBodies.item(1);\n"
            + "    var foot = table.tFoot;\n"
            + "    alert(table.rows.length + ' ' + head.rows.length + ' ' + body1.rows.length "
            + "        + ' ' + body2.rows.length + ' ' + foot.rows.length);\n"
            + "    table.insertRow(6); // Insert a row in the footer.\n"
            + "    alert(table.rows.length + ' ' + head.rows.length + ' ' + body1.rows.length "
            + "        + ' ' + body2.rows.length + ' ' + foot.rows.length);\n"
            + "    table.deleteRow(5); // Delete a row from the second body.\n"
            + "    alert(table.rows.length + ' ' + head.rows.length + ' ' + body1.rows.length "
            + "        + ' ' + body2.rows.length + ' ' + foot.rows.length);\n"
            + "    table.insertRow(2); // Insert a row in the first body.\n"
            + "    alert(table.rows.length + ' ' + head.rows.length + ' ' + body1.rows.length "
            + "        + ' ' + body2.rows.length + ' ' + foot.rows.length);\n"
            + "    table.deleteRow(1); // Delete a row from the header.\n"
            + "    alert(table.rows.length + ' ' + head.rows.length + ' ' + body1.rows.length "
            + "        + ' ' + body2.rows.length + ' ' + foot.rows.length);\n"
            + "  // -->\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = { "tfoot1", "tfoot2", "null", "tfoot3", "exception", "tfoot3", "tfoot4" },
            IE = { "tfoot1", "tfoot2", "null", "tfoot3", "exception", "tfoot3", "exception", "tfoot3" })
    public void tableFooters() throws Exception {
        final String html
            = "<html><head><title>foo</title></head><body>\n"
            + "  <table id='table_1'>\n"
            + "    <tr><td>cell1</td><td>cell2</td><td rowspan='2'>cell4</td></tr>\n"
            + "    <tr><td colspan='2'>cell3</td></tr>\n"
            + "    <tfoot id='tfoot1'><tr><td>cell1</td><td>cell2</td><td>cell3</td></tr></tfoot>\n"
            + "    <tfoot id='tfoot2'><tr><td>cell7</td><td>cell8</td><td>cell9</td></tr></tfoot>\n"
            + "  </table>\n"
            + "  <script>\n"
            + "    var table = document.getElementById('table_1');\n"
            + "    alert(table.tFoot.id);\n"
            + "    table.deleteTFoot();\n"
            + "    alert(table.tFoot.id);\n"
            + "    table.deleteTFoot();\n"
            + "    alert(table.tFoot);\n"
            + "    var newTFoot = table.createTFoot();\n"
            + "    newTFoot.id = 'tfoot3';\n"
            + "    alert(table.tFoot.id);\n"
            + "    try { table.tFoot = 123; } catch(e) { alert('exception') }\n"
            + "    alert(table.tFoot.id);\n"
            + "    var tFoot4 = document.createElement('tFoot');\n"
            + "    tFoot4.id = 'tfoot4';\n"
            + "    try { table.tFoot = tFoot4; } catch(e) { alert('exception') }\n"
            + "    alert(table.tFoot.id);\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "0", "1", "0", "1", "-1" })
    public void cellIndex() throws Exception {
        final String html
            = "<html><head><title>Test</title>\n"
            + "<script>\n"
            + "  function test() {\n"
            + "    alert(document.getElementById('th1').cellIndex);\n"
            + "    alert(document.getElementById('th2').cellIndex);\n"
            + "    alert(document.getElementById('td1').cellIndex);\n"
            + "    alert(document.getElementById('td2').cellIndex);\n"
            + "    alert(document.createElement('td').cellIndex);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'><table>\n"
            + "<tr><th id='th1'>a</th><th id='th2'>b</th></tr>\n"
            + "<tr><td id='td1'>c</td><td id='td2'>d</td></tr>\n"
            + "</table></body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "1", "2", "1", "0", "TD", "1", "2" })
    public void insertRow() throws Exception {
        final String html
            = "<html><head><title>foo</title></head><body>\n"
            + "  <table id='table_1'>\n"
            + "  <tr><td>foo</td></tr>\n"
            + "  </table>\n"
            + "  <script type='text/javascript' language='JavaScript'>\n"
            + "    var table = document.getElementById('table_1');\n"
            + "    alert(table.rows.length);\n"
            + "    var newRow = table.insertRow(-1);\n"
            + "    alert(table.rows.length);\n"
            + "    alert(newRow.rowIndex);\n"
            + "    alert(newRow.cells.length);\n"
            + "    var newCell = newRow.insertCell(-1);\n"
            + "    alert(newCell.tagName);\n"
            + "    alert(newRow.cells.length);\n"
            + "    newRow.insertCell(newRow.cells.length);\n"
            + "    alert(newRow.cells.length);\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * Regression test for bug 1244839.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "mytable", "mytable" })
    public void insertRowNested() throws Exception {
        final String html =
            "<html><head>\n"
            + "<script>\n"
            + "function test() {\n"
            + "  var container = document.getElementById('mytable');\n"
            + "  alert(container.id);\n"
            + "  var tableRow = container.insertRow(1);\n"
            + "  alert(tableRow.parentNode.parentNode.id);\n"
            + "}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "<table id='mytable'>\n"
            + "<tr>\n"
            + "<td>\n"
            + "  <table id='nested'><tr><td></td></tr></table>\n"
            + "</td></tr>\n"
            + "</table>\n"
            + "</body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * Tests that a <tt>tbody</tt> is automatically created.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "TBODY", "TABLE" })
    public void insertRowInEmptyTable() throws Exception {
        final String html =
            "<html><head>\n"
            + "<script>\n"
            + "function test() {\n"
            + "  var oTable = document.getElementById('mytable');\n"
            + "  var tableRow = oTable.insertRow(0);\n"
            + "  alert(tableRow.parentNode.tagName);\n"
            + "  alert(tableRow.parentNode.parentNode.tagName);\n"
            + "}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "<table id='mytable'>\n"
            + "</table>\n"
            + "</body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * Tests that a <tt>tbody</tt> is automatically created.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "TBODY", "TBODY", "TBODY" })
    public void insertRowInTableWithEmptyTbody() throws Exception {
        final String html =
            "<html><head>\n"
            + "<script>\n"
            + "function test() {\n"
            + "  var oTable = document.getElementById('mytable');\n"
            + "  alert(oTable.lastChild.tagName);\n"
            + "  var tableRow = oTable.insertRow(0);\n"
            + "  alert(oTable.lastChild.tagName);\n"
            + "  alert(tableRow.parentNode.tagName);\n"
            + "}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "<table id='mytable'><tbody></tbody></table>\n"
            + "</body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * Tests length, tBodies on nested rows.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "1", "1" })
    public void nestedTables() throws Exception {
        final String html =
            "<html><head>\n"
            + "<script>\n"
            + "function test() {\n"
            + "  var myTable = document.getElementById('mytable');\n"
            + "  alert(myTable.rows.length);\n"
            + "  alert(myTable.tBodies.length);\n"
            + "}\n"
            + "</script>\n"
            + "</head>\n"
            + "<body onload='test()'>\n"
            + "<table id='mytable'>\n"
            + "<tr>\n"
            + "<td>\n"
            + "  <table id='nested'><tr><td></td></tr></table>\n"
            + "</td></tr>\n"
            + "</table>\n"
            + "</body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * Tests string default values.
     * See https://sourceforge.net/tracker/?func=detail&atid=448266&aid=1538136&group_id=47038.
     * Currently not working for FF as HtmlUnit's object names don't map to FF ones.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(IE = { "table: [object]", "row: [object]", "cell: [object]" },
            FF = { "table: [object HTMLTableElement]",
                    "row: [object HTMLTableRowElement]", "cell: [object HTMLTableCellElement]" })
    public void stringValues() throws Exception {
        final String html =
            "<html><head>\n"
            + "  <script>\n"
            + "    function test()\n"
            + "    {\n"
            + "      alert('table: ' + document.getElementById('myTable'));\n"
            + "      alert('row: ' + document.getElementById('myRow'));\n"
            + "      alert('cell: ' + document.getElementById('myCell'));\n"
            + "    }\n"
            + "  </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "    <table id='myTable'>\n"
            + "      <tr id='myRow'>\n"
            + "        <th id='myCell'>Foo</th>\n"
            + "      </tr>\n"
            + "    </table>\n"
            + "  </body>\n"
            + "</html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void width() throws Exception {
        final String content
            = "<html><head></head><body>\n"
                + "<table id='tableID' style='background:blue'><tr><td></td></tr></table>\n"
                + "<script language='javascript'>\n"
                + "    var table = document.getElementById('tableID');\n"
                + "    table.width = '200';\n"
                + "</script></body></html>";

        final HtmlPage page = loadPage(getBrowserVersion(), content, null);
        final String xml = page.asXml();
        assertTrue(xml.contains("width=\"200\""));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("21")
    public void cellSpacing() throws Exception {
        final String html
            = "<html><head></head><body>\n"
                + "<table id='tableID' cellspacing='2'><tr><td></td></tr></table>\n"
                + "<script language='javascript'>\n"
                + "    var table = document.getElementById('tableID');\n"
                + "    table.cellSpacing += 1;\n"
                + "    alert(table.cellSpacing);\n"
                + "</script></body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("21")
    public void cellPadding() throws Exception {
        final String html
            = "<html><head></head><body>\n"
                + "<table id='tableID' cellpadding='2'><tr><td></td></tr></table>\n"
                + "<script language='javascript'>\n"
                + "    var table = document.getElementById('tableID');\n"
                + "    table.cellPadding += 1;\n"
                + "    alert(table.cellPadding);\n"
                + "</script></body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void refresh() throws Exception {
        final String html
            = "<html><head><script>\n"
            + "  function test() {\n"
            + "    document.getElementById('myTable').refresh();\n"
            + "  }\n"
            + "</script></head><body onload='test()'>\n"
            + "<table id='myTable'></table>\n"
            + "</body></html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(FF = {"", "hello", "left", "hi", "right" },
            IE = {"", "error", "", "left", "error", "left", "right" })
    public void align() throws Exception {
        final String html =
            "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + "      function test() {\n"
            + "        var t = document.getElementById('t');\n"
            + "        alert(t.align);\n"
            + "        set(t, 'hello');\n"
            + "        alert(t.align);\n"
            + "        set(t, 'left');\n"
            + "        alert(t.align);\n"
            + "        set(t, 'hi');\n"
            + "        alert(t.align);\n"
            + "        set(t, 'right');\n"
            + "        alert(t.align);\n"
            + "      }\n"
            + "      function set(e, value) {\n"
            + "        try {\n"
            + "          e.align = value;\n"
            + "        } catch (e) {\n"
            + "          alert('error');\n"
            + "        }\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "    <table id='t'>\n"
            + "      <thead id='th'/>\n"
            + "      <tbody id='tb'/>\n"
            + "      <tfoot id='tf'/>\n"
            + "    </table>\n"
            + "  </body>\n"
            + "</html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({"", "#0000aa", "#000000" })
    public void bgColor() throws Exception {
        final String html =
            "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + "      function test() {\n"
            + "        var table = document.getElementById('table');\n"
            + "        alert(table.bgColor);\n"
            + "        table.bgColor = '#0000aa';\n"
            + "        alert(table.bgColor);\n"
            + "        table.bgColor = 'x';\n"
            + "        alert(table.bgColor);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "  <table id='table'><tr><td>a</td></tr></table>\n"
            + "  </body>\n"
            + "</html>";
        loadPageWithAlerts2(html);
    }

}
