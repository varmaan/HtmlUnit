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
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browser;
import com.gargoylesoftware.htmlunit.BrowserRunner.NotYetImplemented;

/**
 * Tests for {@link HtmlForm}, with BrowserRunner.
 *
 * @version $Revision: 5524 $
 * @author Ahmed Ashour
 */
@RunWith(BrowserRunner.class)
public class HtmlForm2Test extends WebTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    @NotYetImplemented(Browser.FF)
    public void encoding() throws Exception {
        final String html = "<html>\n"
            + "<head><title>foo</title>\n"
            + "  <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>\n"
            + "</head>\n"
            + "<body>\n"
            + "  <form>\n"
            + "    <input name='par\u00F6m' value='Hello G\u00FCnter'>\n"
            + "    <input id='mySubmit' type='submit' value='Submit'>\n"
            + "  </form>\n"
            + "   <a href='bug.html?h\u00F6=G\u00FCnter' id='myLink'>Click me</a>\n"
            + "</body></html>";

        final WebClient client = getWebClientWithMockWebConnection();
        final List<String> collectedAlerts = new ArrayList<String>();
        client.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        final URL url = getDefaultUrl();
        final MockWebConnection webConnection = getMockWebConnection();
        webConnection.setDefaultResponse(html, "text/html", "UTF-8");

        final HtmlPage page = client.getPage(url);
        assertEquals(url.toExternalForm(), page.getWebResponse().getRequestSettings().getUrl());
        final HtmlPage submitPage = page.<HtmlElement>getHtmlElementById("mySubmit").click();
        final HtmlPage linkPage = page.<HtmlElement>getHtmlElementById("myLink").click();
        final String submitSuffix;
        final String linkSuffix;
        if (getBrowserVersion().isIE()) {
            submitSuffix = "?par%C3%B6m=Hello+G%C3%BCnter";
            linkSuffix = "bug.html?h\u00F6=G\u00FCnter";
        }
        else if (getBrowserVersion().getBrowserVersionNumeric() == 2) {
            submitSuffix = "?par%C3%B6m=Hello+G%C3%BCnter";
            linkSuffix = "bug.html?h%C3%B6=G%C3%BCnter";
        }
        else {
            submitSuffix = "?par\u00F6m=Hello+G\u00FCnter";
            linkSuffix = "bug.html?h\u00F6=G\u00FCnter";
        }
        assertEquals(url.toExternalForm() + submitSuffix, submitPage.getWebResponse().getRequestSettings().getUrl());
        assertEquals(url.toExternalForm() + linkSuffix, linkPage.getWebResponse().getRequestSettings().getUrl());
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(IE = { "textfieldid", "textfieldname", "textfieldid", "textfieldid", "textfieldname", "textfieldid" },
            FF = { "error", "error", "error", "error", "error", "error" })
    public void asFunction() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function test(){\n"
            + "  var f1 = document.forms[0];\n"
            + "  var f2 = document.forms(0);\n"
            + "  try { alert(f1('textfieldid').id) } catch (e) { alert('error') }\n"
            + "  try { alert(f1('textfieldname').name) } catch (e) { alert('error') }\n"
            + "  try { alert(f1(0).id) } catch (e) { alert('error') }\n"
            + "  try { alert(f2('textfieldid').id) } catch (e) { alert('error') }\n"
            + "  try { alert(f2('textfieldname').name) } catch (e) { alert('error') }\n"
            + "  try { alert(f2(0).id) } catch (e) { alert('error') }\n"
            + "}\n"
            + "</script></head><body onload='test()'>\n"
            + "<p>hello world</p>\n"
            + "<form id='firstid' name='firstname'>\n"
            + "  <input type='text' id='textfieldid' value='foo' />\n"
            + "  <input type='text' name='textfieldname' value='foo' />\n"
            + "</form>\n"
            + "</body></html>";
        loadPageWithAlerts(html);
    }

}
