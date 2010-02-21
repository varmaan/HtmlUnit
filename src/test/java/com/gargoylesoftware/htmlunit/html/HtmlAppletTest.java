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

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;

/**
 * Tests for {@link HtmlApplet}.
 *
 * @version $Revision: 5301 $
 * @author Ahmed Ashour
 * @author Marc Guillemot
 */
@RunWith(BrowserRunner.class)
public class HtmlAppletTest extends WebTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = { "[object HTMLAppletElement]", "[object HTMLAppletElement]" },
            IE = { "[object]", "[object]" })
    public void simpleScriptable() throws Exception {
        final String html = "<html><head>\n"
            + "<script>\n"
            + "  function test() {\n"
            + "    alert(document.getElementById('myId'));\n"
            + "    alert(document.applets[0]);\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "  <applet id='myId'></applet>\n"
            + "</body></html>";

        final HtmlPage page = loadPageWithAlerts(html);
        assertTrue(HtmlApplet.class.isInstance(page.getHtmlElementById("myId")));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void asText_appletDisabled() throws Exception {
        final String html = "<html><head>\n"
            + "</head><body>\n"
            + "  <applet id='myId'>Your browser doesn't support applets</object>\n"
            + "</body></html>";

        final HtmlPage page = loadPageWithAlerts(html);
        final HtmlApplet appletNode = page.getHtmlElementById("myId");
        assertEquals("Your browser doesn't support applets", appletNode.asText());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void asText_appletEnabled() throws Exception {
        final String html = "<html><head>\n"
            + "</head><body>\n"
            + "  <applet id='myId'>Your browser doesn't support applets</object>\n"
            + "</body></html>";

        final WebClient webClient = getWebClient();
        final MockWebConnection connection = getMockWebConnection();
        webClient.setWebConnection(connection);
        connection.setDefaultResponse(html);
        webClient.setAppletEnabled(true);
        final HtmlPage page = webClient.getPage(URL_FIRST);
        final HtmlApplet appletNode = page.getHtmlElementById("myId");
        assertEquals("", appletNode.asText()); // should we display something else?
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void simpleInstantiation() throws Exception {
        final URL url = getClass().getResource("/applets/emptyApplet.html");

        final HtmlPage page = getWebClient().getPage(url);
        final HtmlApplet appletNode = page.getHtmlElementById("myApp");

        assertEquals("net.sourceforge.htmlunit.testapplets.EmptyApplet", appletNode.getApplet().getClass().getName());
    }
}
