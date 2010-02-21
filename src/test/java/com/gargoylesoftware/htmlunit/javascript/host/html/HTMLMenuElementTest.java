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

/**
 * Unit tests for {@link HTMLMenuElement}.
 *
 * @version $Revision: 5301 $
 * @author Daniel Gredler
 */
@RunWith(BrowserRunner.class)
public class HTMLMenuElementTest extends WebTestCase {

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(FF = { "false", "true", "true", "true", "null", "", "blah", "2",
                   "true", "false", "true", "false", "", "null", "", "null" },
        IE = { "false", "true", "true", "true", "false", "true", "true", "true",
               "true", "false", "true", "false", "true", "false", "true", "false" })
    public void compact() throws Exception {
        final String html = "<html><body>\n"
            + "<menu id='menu1'><li>a</li><li>b</li></menu>\n"
            + "<menu compact='' id='menu2'><li>a</li><li>b</li></menu>\n"
            + "<menu compact='blah' id='menu3'><li>a</li><li>b</li></menu>\n"
            + "<menu compact='2' id='menu4'><li>a</li><li>b</li></menu>\n"
            + "<script>\n"
            + "alert(document.getElementById('menu1').compact);\n"
            + "alert(document.getElementById('menu2').compact);\n"
            + "alert(document.getElementById('menu3').compact);\n"
            + "alert(document.getElementById('menu4').compact);\n"
            + "alert(document.getElementById('menu1').getAttribute('compact'));\n"
            + "alert(document.getElementById('menu2').getAttribute('compact'));\n"
            + "alert(document.getElementById('menu3').getAttribute('compact'));\n"
            + "alert(document.getElementById('menu4').getAttribute('compact'));\n"
            + "document.getElementById('menu1').compact = true;\n"
            + "document.getElementById('menu2').compact = false;\n"
            + "document.getElementById('menu3').compact = 'xyz';\n"
            + "document.getElementById('menu4').compact = null;\n"
            + "alert(document.getElementById('menu1').compact);\n"
            + "alert(document.getElementById('menu2').compact);\n"
            + "alert(document.getElementById('menu3').compact);\n"
            + "alert(document.getElementById('menu4').compact);\n"
            + "alert(document.getElementById('menu1').getAttribute('compact'));\n"
            + "alert(document.getElementById('menu2').getAttribute('compact'));\n"
            + "alert(document.getElementById('menu3').getAttribute('compact'));\n"
            + "alert(document.getElementById('menu4').getAttribute('compact'));\n"
            + "</script>\n"
            + "</body></html>";
        loadPageWithAlerts(html);
    }

}
