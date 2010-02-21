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
package com.gargoylesoftware.htmlunit.javascript;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.WebDriverTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browser;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browsers;

/**
 * Tests for {@link StringScriptPreProcessor}.
 *
 * @version $Revision: 5468 $
 * @author Ahmed Ashour
 */
@RunWith(BrowserRunner.class)
public class StringScriptPreProcessorTest extends WebDriverTestCase {

    /**
     * Test for single quote inside comment.
     */
    @Test
    @Browsers(Browser.NONE)
    public void singleQuoteInComment() {
        final String input = "/* Let's test it */ var x = /[\\x00-\\x1f]/;";
        test(input, input);
    }

    private void test(final String input, final String expectedOutput) {
        final String output = new StringScriptPreProcessor(null).preProcess(null, input, null, 0, null);
        assertEquals(expectedOutput, output);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "/'/", "/[\\x5f]/" })
    public void regularExpressions() throws Exception {
        final String html = "<html><head><script>\n"
            + "function test() {\n"
            + "  alert(/'/);\n"
            + "  alert(/[\\x5f]/);\n"
            + "}\n"
            + "</script>\n"
            + "</head><body onload='test()'></body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void divisionOperator() throws Exception {
        final String html = "<html><body><script>\n"
            + "x=function(a){a/2};\n"
            + "y='/';\n"
            + "z=function(b){b(/\\xAD/)};\n"
            + "p=function(c){c(/d/)};\n"
            + "</script></body></html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void divisionOperatorAfterParentheses() throws Exception {
        final String html = "<html><body><script>\n"
            + "x=function(a){b()/2};\n"
            + "y='/';\n"
            + "z=function(b){b(/\\xAD/)};\n"
            + "p=function(c){c(/d/)};\n"
            + "</script></body></html>";
        loadPageWithAlerts2(html);
    }
}
