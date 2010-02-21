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

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.WebDriverTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browser;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browsers;

/**
 * Tests for {@link CSSImportRule}.
 *
 * @version $Revision: 5301 $
 * @author Daniel Gredler
 * @author Marc Guillemot
 */
@RunWith(BrowserRunner.class)
public class CSSImportRuleTest extends WebDriverTestCase {

    /**
     * Regression test for bug 2658249.
     * @throws Exception if an error occurs
     */
    @Test
    @Browsers(Browser.FF)
    public void testGetImportFromCssRulesCollection() throws Exception {
        // with absolute URL
        testGetImportFromCssRulesCollection(getDefaultUrl(), URL_SECOND.toExternalForm(), URL_SECOND);

        // with relative URL
        final URL urlPage = new URL(URL_FIRST, "/dir1/dir2/foo.html");
        final URL urlCss = new URL(URL_FIRST, "/dir1/dir2/foo.css");
        testGetImportFromCssRulesCollection(urlPage, "foo.css", urlCss);
    }

    private void testGetImportFromCssRulesCollection(final URL pageUrl, final String cssRef, final URL cssUrl)
        throws Exception {
        final String html
            = "<html><body>\n"
            + "<style>@import url('" + cssRef + "');</style><div id='d'>foo</div>\n"
            + "<script>\n"
            + "var r = document.styleSheets.item(0).cssRules[0];\n"
            + "alert(r);\n"
            + "alert(r.href);\n"
            + "alert(r.media);\n"
            + "alert(r.media.length);\n"
            + "alert(r.styleSheet);\n"
            + "</script>\n"
            + "</body></html>";
        final String css = "#d { color: green }";

        getMockWebConnection().setResponse(cssUrl, css, "text/css");

        setExpectedAlerts("[object CSSImportRule]", cssRef,
            "[object MediaList]", "0", "[object CSSStyleSheet]");
        loadPageWithAlerts2(html, pageUrl);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("true")
    public void testImportedStylesheetsLoaded() throws Exception {
        final String html
            = "<html><body>\n"
            + "<style>@import url('" + URL_SECOND + "');</style>\n"
            + "<div id='d'>foo</div>\n"
            + "<script>\n"
            + "var d = document.getElementById('d');\n"
            + "var s = window.getComputedStyle ? window.getComputedStyle(d,null) : d.currentStyle;\n"
            + "alert(s.color.indexOf('128') > 0);\n"
            + "</script>\n"
            + "</body></html>";
        final String css = "#d { color: rgb(0, 128, 0); }";

        getMockWebConnection().setResponse(URL_SECOND, css, "text/css");

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts("true")
    public void testImportedStylesheetsURLResolution() throws Exception {
        final String html = "<html><head>\n"
            + "<link rel='stylesheet' type='text/css' href='dir1/dir2/file1.css'></link>\n"
            + "<body>\n"
            + "<div id='d'>foo</div>\n"
            + "<script>\n"
            + "var d = document.getElementById('d');\n"
            + "var s = window.getComputedStyle ? window.getComputedStyle(d, null) : d.currentStyle;\n"
            + "alert(s.color.indexOf('128') > 0);\n"
            + "</script>\n"
            + "</body></html>";
        final String css1 = "@import url('file2.css');";
        final String css2 = "#d { color: rgb(0, 128, 0); }";

        final URL urlPage = URL_FIRST;
        final URL urlCss1 = new URL(urlPage, "dir1/dir2/file1.css");
        final URL urlCss2 = new URL(urlPage, "dir1/dir2/file2.css");
        getMockWebConnection().setResponse(urlCss1, css1, "text/css");
        getMockWebConnection().setResponse(urlCss2, css2, "text/css");

        loadPageWithAlerts2(html, urlPage);
    }
}
