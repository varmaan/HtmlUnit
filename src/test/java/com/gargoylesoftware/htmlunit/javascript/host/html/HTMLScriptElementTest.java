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
import com.gargoylesoftware.htmlunit.WebTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browser;
import com.gargoylesoftware.htmlunit.BrowserRunner.NotYetImplemented;

/**
 * Unit tests for {@link HTMLScriptElement}.
 * TODO: check event order with defer in real browser WITHOUT using alert(...) as it impacts ordering.
 * Some expectations seems to be incorrect.
 * @version $Revision: 5349 $
 * @author Daniel Gredler
 * @author Ahmed Ashour
 * @author Marc Guillemot
 */
@RunWith(BrowserRunner.class)
public class HTMLScriptElementTest extends WebTestCase {

    /**
     * Verifies that the <tt>onreadystatechange</tt> handler is invoked correctly.
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(FF = "1 2 3 4 onload ", IE = "1 2 3 b=loading 4 b=loaded ")
    public void onReadyStateChangeHandler() throws Exception {
        final String html = "<html>\n"
            + "  <head>\n"
            + "    <title>test</title>\n"
            + "    <script>\n"
            + "      function test() {\n"
            + "        var script = document.createElement('script');\n"
            + "        script.id = 'b';\n"
            + "        script.type = 'text/javascript';\n"
            + "        script.onreadystatechange = null;\n"
            + "        script.onreadystatechange = function() {\n"
            + "          document.getElementById('myTextarea').value += script.id + '=' + script.readyState + ' ';\n"
            + "          if (this.readyState == 'loaded') {\n"
            + "            alert(document.getElementById('myTextarea').value);\n"
            + "          }\n"
            + "        }\n"
            + "        script.onload = function () {\n"
            + "          document.getElementById('myTextarea').value += 'onload ';\n"
            + "          alert(document.getElementById('myTextarea').value);\n"
            + "        }\n"
            + "        document.getElementById('myTextarea').value += '1 ';\n"
            + "        script.src = 'script.js';\n"
            + "        document.getElementById('myTextarea').value += '2 ';\n"
            + "        document.getElementsByTagName('head')[0].appendChild(script);\n"
            + "        document.getElementById('myTextarea').value += '3 ';\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "    <textarea id='myTextarea' cols='40'></textarea>\n"
            + "  </body></html>";

        final String js = "document.getElementById('myTextarea').value += '4 ';";

        getMockWebConnection().setDefaultResponse(js, JAVASCRIPT_MIME_TYPE);
        loadPageWithAlerts(html);
    }

    /**
     * Test for bug https://sourceforge.net/tracker/?func=detail&atid=448266&aid=1782719&group_id=47038.
     * TODO: check if IE8 really behaves like IE6 and not like IE7.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = "1", IE6 = "1", IE8 = "1")
    public void srcWithJavaScriptProtocol_Static() throws Exception {
        final String html = "<html><head><script src='javascript:\"alert(1)\"'></script></head><body></body></html>";
        loadPageWithAlerts(html);
    }

    /**
     * Test for bug https://sourceforge.net/tracker/?func=detail&atid=448266&aid=1782719&group_id=47038.
     * TODO: check if IE8 really behaves like IE6 and not like IE7.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = "1", IE6 = "1", IE8 = "1")
    public void srcWithJavaScriptProtocol_Dynamic() throws Exception {
        final String html =
              "<html><head><title>foo</title><script>\n"
            + "  function test() {\n"
            + "    var script=document.createElement('script');\n"
            + "    script.src=\"javascript: 'alert(1)'\";\n"
            + "    document.getElementsByTagName('head')[0].appendChild(script);\n"
            + "  }\n"
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        loadPageWithAlerts(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = "exception", IE = "hello")
    public void scriptForEvent() throws Exception {
        // IE accepts it with () or without
        scriptForEvent("onload");
        scriptForEvent("onload()");
    }

    private void scriptForEvent(final String eventName) throws Exception {
        final String html = "<html><head><title>foo</title>\n"
            + "<script FOR='window' EVENT='" + eventName + "' LANGUAGE='javascript'>\n"
            + "try {\n"
            + " document.form1.txt.value='hello';\n"
            + " alert(document.form1.txt.value);\n"
            + "} catch(e) {alert('exception'); }\n"
            + "</script></head><body>\n"
            + "<form name='form1'><input type=text name='txt'></form></body></html>";

        loadPageWithAlerts(html);
    }

    /**
     * Verifies the correct the ordering of script element execution, deferred script element
     * execution, script ready state changes, deferred script ready state changes, and onload
     * handlers.
     *
     * @throws Exception if an error occurs
     */
    @Test
    @NotYetImplemented(Browser.IE)
    @Alerts(FF = { "3", "4", "2", "5" }, IE = { "1", "2", "3", "4", "5", "6", "7" })
    public void onReadyStateChange_Order() throws Exception {
        final String html =
              "<html>\n"
            + "  <head>\n"
            + "    <title>test</title>\n"
            + "    <script defer=''>alert('3');</script>\n"
            + "    <script defer='' onreadystatechange='if(this.readyState==\"complete\") alert(\"6\");'>alert('4');</script>\n"
            + "    <script src='//:' onreadystatechange='if(this.readyState==\"complete\") alert(\"1\");'></script>\n"
            + "    <script defer='' src='//:' onreadystatechange='if(this.readyState==\"complete\") alert(\"7\");'></script>\n"
            + "    <script>alert('2')</script>\n"
            + "  </head>\n"
            + "  <body onload='alert(5)'></body>\n"
            + "</html>";
        loadPageWithAlerts(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(IE = "[object]")
    public void onReadyStateChange_EventAvailable() throws Exception {
        final String html =
              "<html><body><script>\n"
            + "var s = document.createElement('script');\n"
            + "s.src = '//:';\n"
            + "s.onreadystatechange = function() {alert(window.event);};\n"
            + "document.body.appendChild(s);\n"
            + "</script></body></html>";
        loadPageWithAlerts(html);
    }

    /**
     * Verifies the correct the ordering of script element execution, deferred script element
     * execution, script ready state changes, deferred script ready state changes, and onload
     * handlers when the document doesn't have an explicit <tt>body</tt> element.
     *
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(FF = { "3", "4", "2" }, IE = { "1", "2", "3", "4", "5", "6" })
    public void onReadyStateChange_Order_NoBody() throws Exception {
        final String html =
              "<html>\n"
            + "  <head>\n"
            + "    <title>test</title>\n"
            + "    <script defer=''>alert('3');</script>\n"
            + "    <script defer='' onreadystatechange='if(this.readyState==\"complete\") alert(\"5\");'>alert('4');</script>\n"
            + "    <script src='//:' onreadystatechange='if(this.readyState==\"complete\") alert(\"1\");'></script>\n"
            + "    <script defer='' src='//:' onreadystatechange='if(this.readyState==\"complete\") alert(\"6\");'></script>\n"
            + "    <script>alert('2')</script>\n"
            + "  </head>\n"
            + "</html>";
        loadPageWithAlerts(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("1")
    public void text() throws Exception {
        final String html =
            "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + "      function test() {\n"
            + "        execMe('alert(1)');\n"
            + "      }\n"
            + "      function execMe(text) {\n"
            + "        document.head = document.getElementsByTagName('head')[0];\n"
            + "        var script = document.createElement('script');\n"
            + "        script.text = text;\n"
            + "        document.head.appendChild(script);\n"
            + "        document.head.removeChild(script);\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'>\n"
            + "  </body>\n"
            + "</html>";
        loadPageWithAlerts(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = "onload", IE = { "defer", "onload" })
    public void onload_after_deferReadStateComplete() throws Exception {
        final String html =
              "<html>\n"
            + "  <head>\n"
            + "    <title>test</title>\n"
            + "    <script onreadystatechange='if(this.readyState==\"complete\") alert(\"defer\");' defer></script>\n"
            + "  </head>\n"
            + "  <body onload='alert(\"onload\")'>\n"
            + "  </body>\n"
            + "</html>";

        loadPageWithAlerts(html);
    }
}
