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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.WebDriverTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browser;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browsers;
import com.gargoylesoftware.htmlunit.html.HtmlImageInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

/**
 * Tests for {@link HTMLInputElement} and buttons.
 *
 * @version $Revision: 5301 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author Marc Guillemot
 * @author Chris Erskine
 * @author Ahmed Ashour
 */
@RunWith(BrowserRunner.class)
public class HTMLInputElementTest extends WebDriverTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"foo", "text", "textfield1", "form1", "cat" })
    public void standardProperties_Text() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function doTest(){\n"
            + "    alert(document.form1.textfield1.value);\n"
            + "    alert(document.form1.textfield1.type);\n"
            + "    alert(document.form1.textfield1.name);\n"
            + "    alert(document.form1.textfield1.form.name);\n"
            + "    document.form1.textfield1.value='cat';\n"
            + "    alert(document.form1.textfield1.value);\n"
            + "}\n"
            + "</script></head><body onload='doTest()'>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1'>\n"
            + "    <input type='text' name='textfield1' value='foo' />\n"
            + "</form>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({
            "button", "button", "checkbox", "file", "hidden", "select-one",
            "select-multiple", "password", "reset", "reset", "submit",
            "submit", "text", "textarea" })
    public void textProperties() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function doTest(){\n"
            + "    alert(document.form1.button1.type);\n"
            + "    alert(document.form1.button2.type);\n"
            + "    alert(document.form1.checkbox1.type);\n"
            + "    alert(document.form1.fileupload1.type);\n"
            + "    alert(document.form1.hidden1.type);\n"
            + "    alert(document.form1.select1.type);\n"
            + "    alert(document.form1.select2.type);\n"
            + "    alert(document.form1.password1.type);\n"
            + "    alert(document.form1.reset1.type);\n"
            + "    alert(document.form1.reset2.type);\n"
            + "    alert(document.form1.submit1.type);\n"
            + "    alert(document.form1.submit2.type);\n"
            + "    alert(document.form1.textInput1.type);\n"
            + "    alert(document.form1.textarea1.type);\n"
            + "}\n"
            + "</script></head><body onload='doTest()'>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1'>\n"
            + "    <input type='button' name='button1'></button>\n"
            + "    <button type='button' name='button2'></button>\n"
            + "    <input type='checkbox' name='checkbox1' />\n"
            + "    <input type='file' name='fileupload1' />\n"
            + "    <input type='hidden' name='hidden1' />\n"
            + "    <select name='select1'>\n"
            + "        <option>foo</option>\n"
            + "    </select>\n"
            + "    <select multiple='multiple' name='select2'>\n"
            + "        <option>foo</option>\n"
            + "    </select>\n"
            + "    <input type='password' name='password1' />\n"
            + "    <input type='radio' name='radio1' />\n"
            + "    <input type='reset' name='reset1' />\n"
            + "    <button type='reset' name='reset2'></button>\n"
            + "    <input type='submit' name='submit1' />\n"
            + "    <button type='submit' name='submit2'></button>\n"
            + "    <input type='text' name='textInput1' />\n"
            + "    <textarea name='textarea1'>foo</textarea>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"false", "true" })
    public void checkedAttribute_Checkbox() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function test() {\n"
            + "    alert(document.form1.checkbox1.checked);\n"
            + "    document.form1.checkbox1.checked = true;\n"
            + "    alert(document.form1.checkbox1.checked);\n"
            + "}\n"
            + "</script></head><body>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1'>\n"
            + "    <input type='cheCKbox' name='checkbox1' id='checkbox1' value='foo' />\n"
            + "</form>\n"
            + "<a href='javascript:test()' id='clickme'>click me</a>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);
        final WebElement checkBox = driver.findElement(By.id("checkbox1"));
        assertFalse(checkBox.isSelected());
        driver.findElement(By.id("clickme")).click();
        assertTrue(checkBox.isSelected());

        assertEquals(getExpectedAlerts(), getCollectedAlerts(driver));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "true", "false", "false", "false", "true", "false" })
    public void checkedAttribute_Radio() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function test() {\n"
            + "    alert(document.form1.radio1[0].checked);\n"
            + "    alert(document.form1.radio1[1].checked);\n"
            + "    alert(document.form1.radio1[2].checked);\n"
            + "    document.form1.radio1[1].checked = true;\n"
            + "    alert(document.form1.radio1[0].checked);\n"
            + "    alert(document.form1.radio1[1].checked);\n"
            + "    alert(document.form1.radio1[2].checked);\n"
            + "}\n"
            + "</script></head><body>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1'>\n"
            + "    <input type='radio' name='radio1' id='radioA' value='a' checked='checked'/>\n"
            + "    <input type='RADIO' name='radio1' id='radioB' value='b' />\n"
            + "    <input type='radio' name='radio1' id='radioC' value='c' />\n"
            + "</form>\n"
            + "<a href='javascript:test()' id='clickme'>click me</a>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);
        final WebElement radioA = driver.findElement(By.id("radioA"));
        final WebElement radioB = driver.findElement(By.id("radioB"));
        final WebElement radioC = driver.findElement(By.id("radioC"));
        assertTrue(radioA.isSelected());
        assertFalse(radioB.isSelected());
        assertFalse(radioC.isSelected());

        driver.findElement(By.id("clickme")).click();
        assertFalse(radioA.isSelected());
        assertTrue(radioB.isSelected());
        assertFalse(radioC.isSelected());

        assertEquals(getExpectedAlerts(), getCollectedAlerts(driver));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({ "false", "true", "false", "true", "false", "true" })
    public void disabledAttribute() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function test() {\n"
            + "    alert(document.form1.button1.disabled);\n"
            + "    alert(document.form1.button2.disabled);\n"
            + "    alert(document.form1.button3.disabled);\n"
            + "    document.form1.button1.disabled = true;\n"
            + "    document.form1.button2.disabled = false;\n"
            + "    document.form1.button3.disabled = true;\n"
            + "    alert(document.form1.button1.disabled);\n"
            + "    alert(document.form1.button2.disabled);\n"
            + "    alert(document.form1.button3.disabled);\n"
            + "}\n"
            + "</script></head><body>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1'>\n"
            + "    <input type='submit' name='button1' value='1'/>\n"
            + "    <input type='submit' name='button2' value='2' disabled/>\n"
            + "    <input type='submit' name='button3' value='3'/>\n"
            + "</form>\n"
            + "<a href='javascript:test()' id='clickme'>click me</a>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);

        final WebElement button1 = driver.findElement(By.name("button1"));
        final WebElement button2 = driver.findElement(By.name("button2"));
        final WebElement button3 = driver.findElement(By.name("button3"));
        assertTrue(button1.isEnabled());
        assertFalse(button2.isEnabled());
        assertTrue(button3.isEnabled());
        driver.findElement(By.id("clickme")).click();
        assertFalse(button1.isEnabled());
        assertTrue(button2.isEnabled());
        assertFalse(button3.isEnabled());

        assertEquals(getExpectedAlerts(), getCollectedAlerts(driver));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void inputValue() throws Exception {
        final String html =
            "<html><head><title>foo</title><script>\n"
            + "function doTest(){\n"
            + " document.form1.textfield1.value = 'blue';\n"
            + "}\n"
            + "</script></head><body>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1' onsubmit='doTest()'>\n"
            + " <input type='text' name='textfield1' id='textfield1' value='foo' />\n"
            + " <input type='submit' id='clickMe'/>\n"
            + "</form>\n"
            + "</body></html>";

        getMockWebConnection().setDefaultResponse("");
        final WebDriver driver = loadPage2(html);
        driver.findElement(By.id("clickMe")).click();
        assertEquals(getDefaultUrl() + "?textfield1=blue", driver.getCurrentUrl());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void inputSelect_NotDefinedAsPropertyAndFunction() throws Exception {
        final String html =
            "<html><head><title>foo</title><script>\n"
            + "function doTest(){\n"
            + " document.form1.textfield1.select();\n"
            + "}\n"
            + "</script></head><body>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1' onsubmit='doTest()'>\n"
            + " <input type='text' name='textfield1' id='textfield1' value='foo' />\n"
            + " <input type='submit' id='clickMe'/>\n"
            + "</form>\n"
            + "</body></html>";

        getMockWebConnection().setDefaultResponse("");
        final WebDriver driver = loadPage2(html);
        driver.findElement(By.id("clickMe")).click();
        assertEquals(getDefaultUrl() + "?textfield1=foo", driver.getCurrentUrl());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void thisDotFormInOnClick() throws Exception {
        final String html = "<html>\n"
            + "<head><title>First</title></head>\n"
            + "<body>\n"
            + "<form name='form1'>\n"
            + "<input type='submit' id='clickMe' onClick=\"this.form.target='_blank'; return false;\">\n"
            + "</form>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);
        assertEquals("First", driver.getTitle());

        assertEquals(null, driver.findElement(By.name("form1")).getAttribute("target"));

        driver.findElement(By.id("clickMe")).click();

        assertEquals("_blank", driver.findElement(By.name("form1")).getAttribute("target"));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"true", "true", "true", "true", "true" })
    public void fieldDotForm() throws Exception {
        final String html = "<html>\n"
            + "<head><title>foo</title><script>\n"
            + "function test(){\n"
            + "  var f = document.form1;\n"
            + "  alert(f == f.mySubmit.form);\n"
            + "  alert(f == f.myText.form);\n"
            + "  alert(f == f.myPassword.form);\n"
            + "  alert(f == document.getElementById('myImage').form);\n"
            + "  alert(f == f.myButton.form);\n"
            + "}\n"
            + "</script></head>\n"
            + "<body onload='test()'>\n"
            + "<form name='form1'>\n"
            + "<input type='submit' name='mySubmit'>\n"
            + "<input type='text' name='myText'>\n"
            + "<input type='password' name='myPassword'>\n"
            + "<input type='button' name='myButton'>\n"
            + "<input type='image' src='foo' name='myImage' id='myImage'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void inputNameChange() throws Exception {
        final String html = "<html><head><title>foo</title><script>\n"
            + "function doTest(){\n"
            + " document.form1.textfield1.name = 'changed';\n"
            + "}\n"
            + "</script></head><body>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1' onsubmit='doTest()'>\n"
            + " <input type='text' name='textfield1' id='textfield1' value='foo' />\n"
            + " <input type='submit' name='button1' id='clickMe' value='pushme' />\n"
            + "</form>\n"
            + "</body></html>";

        getMockWebConnection().setDefaultResponse("");
        final WebDriver driver = loadPage2(html);
        driver.findElement(By.id("clickMe")).click();

        assertEquals(getDefaultUrl() + "?changed=foo&button1=pushme", driver.getCurrentUrl());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("foo")
    public void onChange() throws Exception {
        final String html = "<html><head><title>foo</title>\n"
            + "</head><body>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1'>\n"
            + " <input type='text' name='text1' onchange='alert(this.value)'>\n"
            + "<input name='myButton' type='button' onclick='document.form1.text1.value=\"from button\"'>\n"
            + "</form>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);

        final WebElement textinput = driver.findElement(By.name("text1"));
        textinput.sendKeys("foo");
        final WebElement button = driver.findElement(By.name("myButton"));
        button.click();
        assertEquals("from button", textinput.getValue());

        assertEquals(getExpectedAlerts(), getCollectedAlerts(driver));
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("foo")
    public void onChangeSetByJavaScript() throws Exception {
        final String html = "<html><head><title>foo</title>\n"
            + "</head><body>\n"
            + "<p>hello world</p>\n"
            + "<form name='form1'>\n"
            + " <input type='text' name='text1' id='text1'>\n"
            + "<input name='myButton' type='button' onclick='document.form1.text1.value=\"from button\"'>\n"
            + "</form>\n"
            + "<script>\n"
            + "document.getElementById('text1').onchange= function(event) { alert(this.value) };\n"
            + "</script>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);

        final WebElement textinput = driver.findElement(By.name("text1"));
        textinput.sendKeys("foo");
        final WebElement button = driver.findElement(By.name("myButton"));
        button.click();
        assertEquals("from button", textinput.getValue());

        assertEquals(getExpectedAlerts(), getCollectedAlerts(driver));
    }

    /**
     * Test the default value of a radio and checkbox buttons.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"on", "on" })
    public void defautValue() throws Exception {
        final String html
            = "<html><head><title>First</title><script>\n"
            + "function doTest() {\n"
            + "    alert(document.myForm.myRadio.value);\n"
            + "    alert(document.myForm.myCheckbox.value);\n"
            + "}\n</script></head>\n"
            + "<body onload='doTest()'>\n"
            + "<form name='myForm' action='foo'>\n"
            + "<input type='radio' name='myRadio'/>\n"
            + "<input type='checkbox' name='myCheckbox'/>\n"
            + "</form></body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * Test that changing type doesn't throw.
     * Test must be extended when setting type really does something.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"radio", "hidden", "image" })
    public void changeType() throws Exception {
        final String html
            = "<html><head><title>First</title><script>\n"
            + "function doTest() {\n"
            + "    var input = document.myForm.myRadio;\n"
            + "    alert(input.type);\n"
            + "    input.type = 'hidden';\n"
            + "    alert(input.type);\n"
            + "    input.setAttribute('type', 'image');\n"
            + "    alert(input.type);\n"
            + "}\n</script></head>\n"
            + "<body onload='doTest()'>\n"
            + "<form name='myForm' action='foo'>\n"
            + "<input type='radio' name='myRadio'/>\n"
            + "</form></body></html>";

        final WebDriver driver = loadPageWithAlerts2(html);

        if (driver instanceof HtmlUnitDriver) {
            final WebElement myRadio = driver.findElement(By.name("myRadio"));
            assertTrue(toHtmlElement(myRadio) instanceof HtmlImageInput);
        }
    }

    /**
     * Inputs have properties not only from there own type.
     * Works with Mozilla, Firefox and IE... but not with HtmlUnit now.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({
            "button: false, false, function, function, , ",
            "submit: false, false, function, function, submit it!, submit it!",
            "file: false, false, function, function, , ",
            "checkbox: true, true, function, function, , on",
            "radio: true, true, function, function, , on",
            "text: false, false, function, function, , ",
            "password: false, false, function, function, , "
    })
    public void defaultValues() throws Exception {
        final String html
            = "<html><head></head><body>\n"
                + "<form name='myForm'>\n"
                + "<input type='button' name='myButton'/>\n"
                + "<input type='submit' name='mySubmit' value='submit it!'/>\n"
                + "<input type='file' name='myFile'/>\n"
                + "<input type='checkbox' name='myCheckbox' checked='true'/>\n"
                + "<input type='radio' name='myRadio' checked='true'/>\n"
                + "<input type='text' name='myText'/>\n"
                + "<input type='password' name='myPwd'/>\n"
                + "</form>\n"
                + "<script>\n"
                + "function details(_oInput) {\n"
                + "  alert(_oInput.type + ': '\n"
                + "  + _oInput.checked + ', ' \n"
                + "  + _oInput.defaultChecked + ', '\n"
                + "  + ((String(_oInput.click).indexOf('function') != -1) ? 'function' : 'unknown') + ', '\n"
                + "  + ((String(_oInput.select).indexOf('function') != -1) ? 'function' : 'unknown') + ', '\n"
                + "  + _oInput.defaultValue + ', '\n"
                + "  + _oInput.value\n"
                + " );\n"
                + "}\n"
                + "var oForm = document.myForm;\n"
                + "details(oForm.myButton);\n"
                + "details(oForm.mySubmit);\n"
                + "details(oForm.myFile);\n"
                + "details(oForm.myCheckbox);\n"
                + "details(oForm.myRadio);\n"
                + "details(oForm.myText);\n"
                + "details(oForm.myPwd);\n"
                + "</script>\n"
                + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"text", "hidden" })
    public void createInputAndChangeType() throws Exception {
        final String html
            = "<html><head><title>First</title><script>\n"
            + "function doTest() {\n"
            + "    var input = document.createElement('INPUT');\n"
            + "    alert(input.type);\n"
            + "    input.type = 'hidden';\n"
            + "    alert(input.type);\n"
            + "    myForm.appendChild(input);\n"
            + "}\n</script></head>\n"
            + "<body onload='doTest()'>\n"
            + "<form name='myForm' action='foo'>\n"
            + "</form></body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("123")
    public void buttonOutsideForm() throws Exception {
        final String html
            = "<html><head><title>foo</title></head><body>\n"
            + "<button id='clickme' onclick='alert(123)'>click me</button>\n"
            + "</body></html>";

        final WebDriver driver = loadPage2(html);
        driver.findElement(By.id("clickme")).click();

        assertEquals(getExpectedAlerts(), getCollectedAlerts(driver));
    }

    /**
     * Test that field delegates submit to the containing form.
     * @throws Exception if the test fails
     */
    @Test
    public void onChangeCallsFormSubmit() throws Exception {
        final String html
            = "<html><head>\n"
            + "</head>\n"
            + "<body>\n"
            + "<form name='test' action='foo'>\n"
            + "<input name='field1' onchange='submit()'>\n"
            + "</form>\n"
            + "</body></html>";

        getMockWebConnection().setDefaultResponse("<html><title>page 2</title><body></body></html>");

        final WebDriver driver = loadPage2(html);
        driver.findElement(By.name("field1")).sendKeys("bla");
        driver.findElement(By.tagName("body")).click();
        assertEquals("page 2", driver.getTitle());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"undefined", "30", "undefined", "30", "30", "30", "40", "50", "string", "number" })
    public void maxLength() throws Exception {
        final String html
            = "<html><head><title>First</title><script>\n"
            + "function doTest() {\n"
            + "    var input = document.getElementById('text1');\n"
            + "    alert(input.maxlength);\n"
            + "    alert(input.maxLength);\n"
            + "    alert(input.MaxLength);\n"
            + "    alert(input.getAttribute('maxlength'));\n"
            + "    alert(input.getAttribute('maxLength'));\n"
            + "    alert(input.getAttribute('MaxLength'));\n"
            + "    input.setAttribute('MaXlenGth', 40);\n"
            + "    alert(input.maxLength);\n"
            + "    input.maxLength = 50;\n"
            + "    alert(input.getAttribute('maxlength'));\n"
            + "    alert(typeof input.getAttribute('maxLength'));\n"
            + "    alert(typeof input.maxLength);\n"
            + "}\n</script></head>\n"
            + "<body onload='doTest()'>\n"
            + "<form name='myForm' action='foo'>\n"
            + "<input type='text' id='text1' maxlength='30'/>\n"
            + "</form></body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Browsers(Browser.FF)
    @Alerts("hello")
    public void selectionRange() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function test() {\n"
            + "    var input = document.getElementById('myInput');\n"
            + "    input.setSelectionRange(2, 7);\n"
            + "    alert('hello');"
            + "}\n"
            + "</script></head><body onload='test()'>\n"
            + "<input id='myInput' value='some test'>\n"
            + "</body></html>";

        final HtmlPage page = loadPageWithAlerts(html);
        final HtmlTextInput input = page.getHtmlElementById("myInput");
        assertEquals("me te", input.getSelectedText());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts({"text text", "password password", "hidden hidden",
            "checkbox checkbox", "radio radio", "file file", "checkbox checkbox" })
    public void typeCase() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function test() {\n"
            + "  var t = document.getElementById('aText');\n"
            + "  alert(t.type + ' ' + t.getAttribute('type'));\n"
            + "  var p = document.getElementById('aPassword');\n"
            + "  alert(p.type + ' ' + p.getAttribute('type'));\n"
            + "  var h = document.getElementById('aHidden');\n"
            + "  alert(h.type + ' ' + h.getAttribute('type'));\n"
            + "  var cb = document.getElementById('aCb');\n"
            + "  alert(cb.type + ' ' + cb.getAttribute('type'));\n"
            + "  var r = document.getElementById('aRadio');\n"
            + "  alert(r.type + ' ' + r.getAttribute('type'));\n"
            + "  var f = document.getElementById('aFile');\n"
            + "  alert(f.type + ' ' + f.getAttribute('type'));\n"
            + "  f.type = 'CHECKBOX';\n"
            + "  alert(f.type + ' ' + f.getAttribute('type'));\n"
            + "}\n"
            + "</script></head><body onload='test()'>\n"
            + "<form action='foo'>\n"
            + "<input Type='TeXt' id='aText' value='some test'>\n"
            + "<input tYpe='PassWord' id='aPassword' value='some test'>\n"
            + "<input tyPe='Hidden' id='aHidden' value='some test'>\n"
            + "<input typE='CheckBox' id='aCb'>\n"
            + "<input TYPE='rAdiO' id='aRadio'>\n"
            + "<input type='FILE' id='aFile'>\n"
            + "</form>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("true")
    public void readOnly() throws Exception {
        final String html
            = "<html><head><title>foo</title><script>\n"
            + "function test() {\n"
            + "    var input = document.getElementById('myInput');\n"
            + "    alert(input.readOnly);\n"
            + "}\n"
            + "</script></head><body onload='test()'>\n"
            + "<input id='myInput' value='some test' readonly='false'>\n"
            + "</body></html>";

        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts(FF = {"", "hello", "left", "hi", "right" },
            IE = {"", "error", "", "", "error", "", "" })
    public void align() throws Exception {
        final String html =
            "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + "      function test() {\n"
            + "        var i = document.getElementById('i');\n"
            + "        alert(i.align);\n"
            + "        set(i, 'hello');\n"
            + "        alert(i.align);\n"
            + "        set(i, 'left');\n"
            + "        alert(i.align);\n"
            + "        set(i, 'hi');\n"
            + "        alert(i.align);\n"
            + "        set(i, 'right');\n"
            + "        alert(i.align);\n"
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
            + "  <body onload='test()'><input type='text' id='i' /></body>\n"
            + "</html>";
        loadPageWithAlerts2(html);
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @Alerts({ "", "A", "a", "A", "a8", "8Afoo", "8", "@" })
    public void accessKey() throws Exception {
        final String html
            = "<html><body>\n"
            + "<input id='a1'>\n"
            + "<input id='a2' accesskey='A'>\n"
            + "<script>\n"
            + "var a1 = document.getElementById('a1');\n"
            + "var a2 = document.getElementById('a2');\n"
            + "alert(a1.accessKey);\n"
            + "alert(a2.accessKey);\n"
            + "a1.accessKey = 'a';\n"
            + "a2.accessKey = 'A';\n"
            + "alert(a1.accessKey);\n"
            + "alert(a2.accessKey);\n"
            + "a1.accessKey = 'a8';\n"
            + "a2.accessKey = '8Afoo';\n"
            + "alert(a1.accessKey);\n"
            + "alert(a2.accessKey);\n"
            + "a1.accessKey = '8';\n"
            + "a2.accessKey = '@';\n"
            + "alert(a1.accessKey);\n"
            + "alert(a2.accessKey);\n"
            + "</script></body></html>";
        loadPageWithAlerts2(html);
    }
}
