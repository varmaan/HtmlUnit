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
package com.gargoylesoftware.htmlunit.html;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebTestCase;

/**
 * Tests for {@link HtmlDivision}.
 *
 * @version $Revision: 5347 $
 * @author Ahmed Ashour
 * @author Marc Guillemot
 */
public class HtmlDivisionTest extends WebTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void simpleScriptable() throws Exception {
        final String html = "<html><head>\n"
            + "<script>\n"
            + "  function test() {\n"
            + "    alert(document.getElementById('myId'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <div id='myId'/>\n"
            + "</body></html>";

        final String[] expectedAlerts = {"[object HTMLDivElement]"};
        final List<String> collectedAlerts = new ArrayList<String>();
        final HtmlPage page = loadPage(BrowserVersion.FIREFOX_3, html, collectedAlerts);
        assertTrue(HtmlDivision.class.isInstance(page.getHtmlElementById("myId")));
        assertEquals(expectedAlerts, collectedAlerts);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void asText() throws Exception {
        String expected = "hello" + LINE_SEPARATOR + "world";
        testAsText(expected, "<div>hello</div>world");
        testAsText(expected, "<div>hello<br/></div>world");

        expected = "hello" + LINE_SEPARATOR + LINE_SEPARATOR + "world";
        testAsText(expected, "<div>hello<br/><br/></div>world");
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void asText_contiguousBlocks() throws Exception {
        final String expected = "hello" + LINE_SEPARATOR + "world";
        testAsText(expected, "<div><table><tr><td>hello</td></tr><tr><td>world<br/></td></tr></table></div>");
        testAsText(expected, "<div>hello</div><div>world</div>");
        testAsText(expected, "<div>hello</div><div><div>world</div></div>");
        testAsText(expected, "<div><table><tr><td>hello</td></tr><tr><td>world<br/></td></tr></table></div>");
    }

    private void testAsText(final String expected, final String htmlSnippet) throws Exception {
        final String html = "<html><head></head><body>\n"
            + htmlSnippet
            + "</body></html>";

        final HtmlPage page = loadPage(html);
        assertEquals(expected, page.asText());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void asTextDiv() throws Exception {
        final String html = "<html><head></head><body>\n"
            + "<div id='foo'>\n \n hello </div>"
            + "</body></html>";

        final HtmlPage page = loadPage(html);
        assertEquals("hello", page.asText());
        final HtmlDivision div = page.getHtmlElementById("foo");
        assertEquals("hello", div.asText());
    }
}
