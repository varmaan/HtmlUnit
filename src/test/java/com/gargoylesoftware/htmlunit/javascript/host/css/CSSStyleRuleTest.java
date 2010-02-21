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
package com.gargoylesoftware.htmlunit.javascript.host.css;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.WebDriverTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browser;
import com.gargoylesoftware.htmlunit.BrowserRunner.NotYetImplemented;

/**
 * Tests for {@link CSSStyleRule}.
 *
 * @version $Revision: 5301 $
 * @author Ahmed Ashour
 */
@RunWith(BrowserRunner.class)
public class CSSStyleRuleTest extends WebDriverTestCase {

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts(FF = { "[object CSSStyleRule]", "1", "[object CSSStyleSheet]", "null", "h1", "", "red" },
            IE = { "[object]", "H1", "", "red" })
    public void test() throws Exception {
        final String html = "<html><head><title>First</title>\n"
                + "<style>\n"
                + "  BODY { background-color: white; color: black; }\n"
                + "  H1 { font: 8pt Arial bold; }\n"
                + "  P  { font: 10pt Arial; text-indent: 0.5in; }\n"
                + "  A  { text-decoration: none; color: blue; }\n"
                + "</style>\n"
                + "<script>\n"
                + "  function test(){\n"
                + "    var rules;\n"
                + "    if (document.styleSheets[0].cssRules)\n"
                + "      rules = document.styleSheets[0].cssRules;\n"
                + "    else\n"
                + "      rules = document.styleSheets[0].rules;\n"
                + "    var r = rules[1];\n"
                + "    alert(r);\n"
                + "    if (r.type) {\n"
                + "      alert(r.type);\n"
                + "      alert(r.parentStyleSheet);\n"
                + "      alert(r.parentRule);\n"
                + "      alert(r.selectorText);\n"
                + "    } else {\n"
                + "      alert(r.selectorText);\n"
                + "    }\n"
                + "    alert(r.style.backgroundColor);\n"
                + "    r.style.backgroundColor = 'red';\n"
                + "    alert(r.style.backgroundColor);\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts(IE = "false")
    @NotYetImplemented(Browser.IE)
    public void testReadOnly() throws Exception {
        final String html = "<html><head><title>First</title>\n"
                + "<style>\n"
                + "  BODY { background-color: white; color: black; }\n"
                + "  H1 { font: 8pt Arial bold; }\n"
                + "  P  { font: 10pt Arial; text-indent: 0.5in; }\n"
                + "  A  { text-decoration: none; color: blue; }\n"
                + "</style>\n"
                + "<script>\n"
                + "  function test(){\n"
                + "    var rules;\n"
                + "    if (document.styleSheets[0].cssRules)\n"
                + "      rules = document.styleSheets[0].cssRules;\n"
                + "    else\n"
                + "      rules = document.styleSheets[0].rules;\n"
                + "    var r = rules[1];\n"
                + "    if (!r.type) {\n"
                + "      alert(r.readOnly);\n"
                + "    }\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts(FF = { "body", "h1", "a.foo", ".foo", ".foo .foo2", "#byId" },
            IE = { "BODY", "H1", "A.foo", ".foo", ".foo .foo2", "#byId" })
    public void selectorText() throws Exception {
        final String html = "<html><head><title>First</title>\n"
                + "<style>\n"
                + "  BoDY { background-color: white; color: black; }\n"
                + "  H1 { font: 8pt Arial bold; }\n"
                + "  A.foo  { color: blue; }\n"
                + "  .foo  { color: blue; }\n"
                + "  .foo .foo2 { font: 8pt; }\n"
                + "  #byId { font: 8pt; }\n"
                + "</style>\n"
                + "<script>\n"
                + "  function test(){\n"
                + "    var sheet = document.styleSheets[0];\n"
                + "    var rules = sheet.cssRules || sheet.rules;\n"
                + "    for (var i=0; i<rules.length; ++i)\n"
                + "      alert(rules[i].selectorText);\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception on test failure
     */
    @Test
    @Alerts(IE = { "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='rightCorner.gif',sizingMethod='crop')" },
            FF = { "undefined" })
    @NotYetImplemented(Browser.IE)
    public void colon() throws Exception {
        final String html = "<html><head><title>First</title>\n"
                + "<style>\n"
                + "  BODY { filter: progid:DXImageTransform.Microsoft.AlphaImageLoader"
                + "(src='rightCorner.gif',sizingMethod='crop'); }\n"
                + "</style>\n"
                + "<script>\n"
                + "  function test(){\n"
                + "    var sheet = document.styleSheets[0];\n"
                + "    var rules = sheet.cssRules || sheet.rules;\n"
                + "    alert(rules[0].style.filter);\n"
                + "  }\n"
                + "</script>\n"
                + "</head><body onload='test()'>\n"
                + "</body></html>";
        loadPageWithAlerts2(html);
    }
}
