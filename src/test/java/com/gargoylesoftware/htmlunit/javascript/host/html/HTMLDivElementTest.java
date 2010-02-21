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

/**
 * Unit tests for {@link HTMLDivElement}.
 *
 * @version $Revision: 5301 $
 * @author Daniel Gredler
 * @author Marc Guillemot
 * @author Ahmed Ashour
 */
@RunWith(BrowserRunner.class)
public class HTMLDivElementTest extends WebDriverTestCase {

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(FF = "no", IE = "yes")
    public void doScroll() throws Exception {
        final String html =
            "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + "      function test() {\n"
            + "        var div = document.getElementById('d');\n"
            + "        if(div.doScroll) {\n"
            + "          alert('yes');\n"
            + "          div.doScroll();\n"
            + "          div.doScroll('down');\n"
            + "        } else {\n"
            + "          alert('no');\n"
            + "        }\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'><div id='d'>abc</div></body>\n"
            + "</html>";
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
            + "        var div = document.getElementById('d');\n"
            + "        alert(div.align);\n"
            + "        set(div, 'hello');\n"
            + "        alert(div.align);\n"
            + "        set(div, 'left');\n"
            + "        alert(div.align);\n"
            + "        set(div, 'hi');\n"
            + "        alert(div.align);\n"
            + "        set(div, 'right');\n"
            + "        alert(div.align);\n"
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
            + "  <body onload='test()'><div id='d'>abc</div></body>\n"
            + "</html>";
        loadPageWithAlerts2(html);
    }
}
