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

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebTestCase;

/**
 * Tests for {@link HtmlScript}.
 *
 * @version $Revision: 5503 $
 * @author Marc Guillemot
 * @author Daniel Gredler
 * @author Ahmed Ashour
 */
public class HtmlScriptTest extends WebTestCase {

    /**
     * Verifies that a failing HTTP status code for a JavaScript file request (like a 404 response)
     * results in a {@link FailingHttpStatusCodeException}, depending on how the client has been
     * configured.
     *
     * @see WebClient#isThrowExceptionOnFailingStatusCode()
     * @throws Exception if an error occurs
     */
    @Test
    public void testBadExternalScriptReference() throws Exception {
        final String html = "<html><head><title>foo</title>\n"
                + "<script src='inexistent.js'></script>\n"
                + "</head><body></body></html>";

        final WebClient client = new WebClient();

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setDefaultResponse("inexistent", 404, "Not Found", "text/html");
        webConnection.setResponse(URL_FIRST, html);
        client.setWebConnection(webConnection);

        try {
            client.getPage(URL_FIRST);
            fail("Should throw.");
        }
        catch (final FailingHttpStatusCodeException e) {
            final String url = URL_FIRST.toExternalForm();
            assertTrue("exception contains URL of failing script", e.getMessage().indexOf(url) > -1);
            assertEquals(404, e.getStatusCode());
            assertEquals("Not Found", e.getStatusMessage());
        }

        client.setThrowExceptionOnFailingStatusCode(false);

        try {
            client.getPage(URL_FIRST);
        }
        catch (final FailingHttpStatusCodeException e) {
            fail("Should not throw.");
        }
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void testAsText() throws Exception {
        final String html = "<html><body><script id='s'>var foo = 132;</script></body></html>";
        final HtmlPage page = loadPage(html);
        final HtmlScript script = page.getHtmlElementById("s");
        assertEquals("", script.asText());
    }

    /**
     * Verifies that the weird script src attribute used by the jQuery JavaScript library is
     * ignored silently (bug 1695279).
     * @throws Exception if the test fails
     */
    @Test
    public void testInvalidJQuerySrcAttribute() throws Exception {
        loadPage("<html><body><script src='//:'></script></body></html>");
    }

    /**
     * Verifies that if a script element executes "window.location.href=someotherpage", then subsequent
     * script tags, and any onload handler for the original page do not run.
     * @throws Exception if the test fails
     */
    @Test
    public void testChangingLocationSkipsFurtherScriptsOnPage() throws Exception {
        final String firstPage
            = "<html><head></head>\n"
            + "<body onload='alert(\"body onload executing but should be skipped\")'>\n"
            + "<script>alert('First script executes')</script>\n"
            + "<script>window.location.href='" + URL_SECOND + "'</script>\n"
            + "<script>alert('Third script executing but should be skipped')</script>\n"
            + "</body></html>";

        final String secondPage
            = "<html><head></head><body>\n"
            + "<script>alert('Second page loading')</script>\n"
            + "</body></html>";

        final WebClient client = new WebClient();

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setResponse(URL_FIRST, firstPage);
        webConnection.setResponse(URL_SECOND, secondPage);
        client.setWebConnection(webConnection);

        final List<String> collectedAlerts = new ArrayList<String>();
        client.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        client.getPage(URL_FIRST);
        final String[] expectedAlerts = {"First script executes", "Second page loading"};
        assertEquals(expectedAlerts, collectedAlerts);
    }

    /**
     * Verifies that a script element is not run when it is cloned.
     * See bug 1707788.
     * @throws Exception if an error occurs
     */
    @Test
    public void testScriptIsNotRunWhenCloned() throws Exception {
        final String html = "<html><body onload='document.body.cloneNode(true)'>\n"
            + "<script>alert('a')</script></body></html>";
        final List<String> collectedAlerts = new ArrayList<String>();
        loadPage(html, collectedAlerts);

        final String[] expectedAlerts = {"a"};
        assertEquals(expectedAlerts, collectedAlerts);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void testDefer() throws Exception {
        final String html = "<html><head>\n"
            + "<script defer>alert('deferred')</script>\n"
            + "<script>alert('normal')</script>\n"
            + "</head>\n"
            + "<body onload='alert(\"onload\")'>test</body>\n"
            + "</html>";

        final List<String> actualFF = new ArrayList<String>();
        loadPage(BrowserVersion.FIREFOX_3, html, actualFF);
        final String[] expectedFF = new String[] {"deferred", "normal", "onload"};
        assertEquals(expectedFF, actualFF);

        final List<String> actualIE = new ArrayList<String>();
        loadPage(BrowserVersion.INTERNET_EXPLORER_7, html, actualIE);
        final String[] expectedIE = new String[] {"normal", "deferred", "onload"};
        assertEquals(expectedIE, actualIE);
    }

}
