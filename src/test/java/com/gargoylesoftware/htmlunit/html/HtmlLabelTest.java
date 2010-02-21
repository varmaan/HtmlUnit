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
 * Tests for {@link HtmlLabel}.
 *
 * @version $Revision: 5347 $
 * @author Marc Guillemot
 * @author Ahmed Ashour
 */
public class HtmlLabelTest extends WebTestCase {

    /**
     * Verifies that a checkbox is toggled when the related label is clicked.
     * @throws Exception if the test fails
     */
    @Test
    public void test_click() throws Exception {
        final String htmlContent
            = "<html><head><title>foo</title></head><body>\n"
            + "<form id='form1'>\n"
            + " <input type='checkbox' name='checkbox' id='testCheckbox' onclick='alert(\"checkbox\")'/>\n"
            + " <label for='testCheckbox' id='testLabel' onclick='alert(\"label\")'>Check me</label>\n"
            + "</form></body></html>";
        final List<String> collectedAlerts = new ArrayList<String>();
        final HtmlPage page = loadPage(htmlContent, collectedAlerts);
        final HtmlCheckBoxInput checkBox = page.getHtmlElementById("testCheckbox");

        assertFalse(checkBox.isChecked());
        final HtmlLabel label = page.getHtmlElementById("testLabel");
        label.click();
        assertTrue(checkBox.isChecked());
        final String[] expectedAlerts = {"label", "checkbox"};
        assertEquals(expectedAlerts, collectedAlerts);
        label.click();
        assertFalse(checkBox.isChecked());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void test_getReferencedElement() throws Exception {
        final String htmlContent
            = "<html><head><title>foo</title></head><body>\n"
            + "<form id='form1'>\n"
            + " <input type='checkbox' name='checkbox' id='testCheckbox'/>\n"
            + " <label for='testCheckbox' id='testLabel1'>Check me</label>\n"
            + " <label for='notExisting' id='testLabel2'>Check me too</label>\n"
            + "</form></body></html>";
        final List<String> collectedAlerts = new ArrayList<String>();
        final HtmlPage page = loadPage(htmlContent, collectedAlerts);
        final HtmlCheckBoxInput checkBox = page.getHtmlElementById("testCheckbox");

        final HtmlLabel label = page.getHtmlElementById("testLabel1");
        assertTrue(checkBox == label.getReferencedElement());
        final HtmlLabel label2 = page.getHtmlElementById("testLabel2");
        assertNull(label2.getReferencedElement());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testSimpleScriptable() throws Exception {
        final String html = "<html><head>\n"
            + "<script>\n"
            + "  function test() {\n"
            + "    alert(document.getElementById('myId'));\n"
            + "  }\n"
            + "</script>\n"
            + "</head><body onload='test()'>\n"
            + "<label id='myId'>Item</label>\n"
            + "</body></html>";

        final String[] expectedAlerts = {"[object HTMLLabelElement]"};
        final List<String> collectedAlerts = new ArrayList<String>();
        final HtmlPage page = loadPage(BrowserVersion.FIREFOX_3, html, collectedAlerts);
        assertTrue(HtmlLabel.class.isInstance(page.getHtmlElementById("myId")));
        assertEquals(expectedAlerts, collectedAlerts);
    }
}
