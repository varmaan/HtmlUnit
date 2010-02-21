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

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebServerTestCase;
import com.gargoylesoftware.htmlunit.BrowserRunner.Alerts;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * Tests for {@link HtmlPage}.
 *
 * @version $Revision: 5301 $
 * @author Ahmed Ashour
 */
@RunWith(BrowserRunner.class)
public class HtmlPage2Test extends WebServerTestCase {

    /**
     * @exception Exception If the test fails
     */
    @Test
    public void constructor() throws Exception {
        final String html = "<html>\n"
            + "<head><title>foo</title></head>\n"
            + "<body>\n"
            + "<p>hello world</p>\n"
            + "<form id='form1' action='/formSubmit' method='post'>\n"
            + "<input type='text' NAME='textInput1' value='textInput1'/>\n"
            + "<input type='text' name='textInput2' value='textInput2'/>\n"
            + "<input type='hidden' name='hidden1' value='hidden1'/>\n"
            + "<input type='submit' name='submitInput1' value='push me'/>\n"
            + "</form>\n"
            + "</body></html>";

        final HtmlPage page = loadPageWithAlerts(html);
        assertEquals("foo", page.getTitleText());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void getInputByName() throws Exception {
        final String html = "<html>\n"
            + "<head><title>foo</title></head>\n"
            + "<body>\n"
            + "<p>hello world</p>\n"
            + "<form id='form1' action='/formSubmit' method='post'>\n"
            + "<input type='text' NAME='textInput1' value='textInput1'/>\n"
            + "<input type='text' name='textInput2' value='textInput2'/>\n"
            + "<input type='hidden' name='hidden1' value='hidden1'/>\n"
            + "<input type='submit' name='submitInput1' value='push me'/>\n"
            + "</form>\n"
            + "</body></html>";
        final HtmlPage page = loadPageWithAlerts(html);

        final HtmlForm form = page.getHtmlElementById("form1");
        final HtmlInput input = form.getInputByName("textInput1");
        Assert.assertEquals("name", "textInput1", input.getNameAttribute());

        Assert.assertEquals("value", "textInput1", input.getValueAttribute());
        Assert.assertEquals("type", "text", input.getTypeAttribute());
    }

    /**
     * Different versions of IE behave differently here.
     * Distinction is made with
     * {@link com.gargoylesoftware.htmlunit.BrowserVersionFeatures#JS_FRAME_RESOLVE_URL_WITH_PARENT_WINDOW}.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = "25", IE6 = "25", IE7 = "error", IE8 = "25")
    public void loadExternalJavaScript() throws Exception {
        final String html =
            "<html><head>\n"
            + "<script>\n"
            + "function makeIframe() {\n"
            + "  var iframesrc = '<html><head>';\n"
            + "  iframesrc += '<script src=\"" + "js.js" + "\"></' + 'script>';\n"
            + "  iframesrc += '<script>';\n"
            + "  iframesrc += 'function doSquared(){';\n"
            + "  iframesrc += '    try {';\n"
            + "  iframesrc += '      var y = squared(5);';\n"
            + "  iframesrc += '      alert(y);';\n"
            + "  iframesrc += '    } catch (e) {';\n"
            + "  iframesrc += '      alert(\"error\");';\n"
            + "  iframesrc += '    }'\n"
            + "  iframesrc += '};';\n"
            + "  iframesrc += '</' + 'script>';\n"
            + "  iframesrc += '</head>';\n"
            + "  iframesrc += '<body onLoad=\"doSquared()\" >';\n"
            + "  iframesrc += '</body>';\n"
            + "  iframesrc += '</html>';\n"
            + "  var iframe = document.createElement('IFRAME');\n"
            + "  iframe.id = 'iMessage';\n"
            + "  iframe.name = 'iMessage';\n"
            + "  iframe.src = \"javascript:'\" + iframesrc + \"'\";\n"
            + "  document.body.appendChild(iframe);\n"
            + "}\n"
            + "</script></head>\n"
            + "<body onload='makeIframe()'>\n"
            + "</body></html>";

        final String js = "function squared(n) {return n * n}";

        final WebClient webClient = getWebClient();
        final MockWebConnection webConnection = new MockWebConnection();

        webConnection.setResponse(URL_FIRST, html);
        webConnection.setResponse(new URL(URL_FIRST, "js.js"), js);
        webClient.setWebConnection(webConnection);

        final List<String> collectedAlerts = new ArrayList<String>();
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        webClient.getPage(URL_FIRST);
        assertEquals(getExpectedAlerts(), collectedAlerts);
    }

    /**
     * Differs from {@link #loadExternalJavaScript()} by the absolute reference of the javascript source.
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("25")
    public void loadExternalJavaScript_absolute() throws Exception {
        final String html =
            "<html><head>\n"
            + "<script>\n"
            + "function makeIframe() {\n"
            + "  var iframesrc = '<html><head>';\n"
            + "  iframesrc += '<script src=\"" + URL_SECOND + "\"></' + 'script>';\n"
            + "  iframesrc += '<script>';\n"
            + "  iframesrc += 'function doSquared(){';\n"
            + "  iframesrc += '    try {';\n"
            + "  iframesrc += '      var y = squared(5);';\n"
            + "  iframesrc += '      alert(y);';\n"
            + "  iframesrc += '    } catch (e) {';\n"
            + "  iframesrc += '      alert(\"error\");';\n"
            + "  iframesrc += '    }'\n"
            + "  iframesrc += '};';\n"
            + "  iframesrc += '</' + 'script>';\n"
            + "  iframesrc += '</head>';\n"
            + "  iframesrc += '<body onLoad=\"doSquared()\" >';\n"
            + "  iframesrc += '</body>';\n"
            + "  iframesrc += '</html>';\n"
            + "  var iframe = document.createElement('IFRAME');\n"
            + "  iframe.id = 'iMessage';\n"
            + "  iframe.name = 'iMessage';\n"
            + "  iframe.src = \"javascript:'\" + iframesrc + \"'\";\n"
            + "  document.body.appendChild(iframe);\n"
            + "}\n"
            + "</script></head>\n"
            + "<body onload='makeIframe()'>\n"
            + "</body></html>";

        final String js = "function squared(n) {return n * n}";

        final WebClient webClient = getWebClient();
        final MockWebConnection webConnection = new MockWebConnection();

        webConnection.setResponse(URL_FIRST, html);
        webConnection.setResponse(URL_SECOND, js);
        webClient.setWebConnection(webConnection);

        final List<String> collectedAlerts = new ArrayList<String>();
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        webClient.getPage(URL_FIRST);
        assertEquals(getExpectedAlerts(), collectedAlerts);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void getFullQualifiedUrl_topWindow() throws Exception {
        final String firstHtml = "<html><head><title>first</title>\n"
            + "<script>\n"
            + "  function init() {\n"
            + "    var iframe = window.frames['f'];\n"
            + "    iframe.document.write(\"<form name='form' action='" + URL_SECOND + "'>"
            + "<input name='submit' type='submit'></form>\");\n"
            + "    iframe.document.close();\n"
            + "  }\n"
            + "</script></head>\n"
            + "<body onload='init()'>\n"
            + "  <iframe name='f'></iframe>\n"
            + "</body></html>";
        final String secondHtml = "<html><head><title>second</title></head>"
            + "<body><p>Form submitted successfully.</p></body></html>";

        final WebClient client = getWebClient();

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setResponse(URL_FIRST, firstHtml);
        webConnection.setDefaultResponse(secondHtml);
        client.setWebConnection(webConnection);

        final HtmlPage page = client.getPage(URL_FIRST);

        HtmlPage framePage = (HtmlPage) page.getFrameByName("f").getEnclosedPage();
        final HtmlForm form = framePage.getFormByName("form");
        final HtmlInput submit = form.getInputByName("submit");
        framePage = submit.click();
        assertEquals("Form submitted successfully.", framePage.getBody().asText());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts("Hello there")
    public void save() throws Exception {
        final String html = "<html><head><script src='" + URL_SECOND + "'>\n</script></head></html>";

        final String js = "alert('Hello there')";

        final WebClient webClient = getWebClient();
        final MockWebConnection webConnection = new MockWebConnection();

        webConnection.setResponse(URL_FIRST, html);
        webConnection.setResponse(URL_SECOND, js);
        webClient.setWebConnection(webConnection);

        final List<String> collectedAlerts = new ArrayList<String>();
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        final HtmlPage page = webClient.getPage(URL_FIRST);
        assertEquals(getExpectedAlerts(), collectedAlerts);
        final File file = new File(System.getProperty("java.io.tmpdir"), "hu_HtmlPageTest_save.html");
        page.save(file);
        assertTrue(file.exists());
        assertTrue(file.isFile());
        final String content = FileUtils.readFileToString(file);
        assertFalse(content.contains("<script"));
        file.delete();
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void save_image() throws Exception {
        final String html = "<html><body><img src='" + URL_SECOND + "'></body></html>";

        final URL url = getClass().getClassLoader().getResource("testfiles/tiny-jpg.img");
        final FileInputStream fis = new FileInputStream(new File(new URI(url.toString())));
        final byte[] directBytes = IOUtils.toByteArray(fis);
        fis.close();

        final WebClient webClient = getWebClient();
        final MockWebConnection webConnection = new MockWebConnection();

        webConnection.setResponse(URL_FIRST, html);
        final List< ? extends NameValuePair> emptyList = Collections.emptyList();
        webConnection.setResponse(URL_SECOND, directBytes, 200, "ok", "image/jpg", emptyList);
        webClient.setWebConnection(webConnection);

        final List<String> collectedAlerts = new ArrayList<String>();
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        final HtmlPage page = webClient.getPage(URL_FIRST);
        final File file = new File(System.getProperty("java.io.tmpdir"), "hu_HtmlPageTest_save2.html");
        final File imgFile = new File(System.getProperty("java.io.tmpdir"), "hu_HtmlPageTest_save2/second.JPEG");
        try {
            page.save(file);
            assertTrue(file.exists());
            assertTrue(file.isFile());
            final FileInputStream fos = new FileInputStream(imgFile);
            final byte[] loadedBytes = IOUtils.toByteArray(fos);
            fos.close();
            assertTrue(loadedBytes.length > 0);
        }
        finally {
            file.delete();
            FileUtils.deleteDirectory(imgFile.getParentFile());
        }
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void save_css() throws Exception {
        final String html = "<html><head>"
            + "<link rel='stylesheet' type='text/css' href='" + URL_SECOND + "'/></head></html>";

        final String css = "body {color: blue}";

        final WebClient webClient = getWebClient();
        final MockWebConnection webConnection = new MockWebConnection();

        webConnection.setResponse(URL_FIRST, html);
        webConnection.setResponse(URL_SECOND, css);
        webClient.setWebConnection(webConnection);

        final List<String> collectedAlerts = new ArrayList<String>();
        webClient.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        final HtmlPage page = webClient.getPage(URL_FIRST);
        final File file = new File(System.getProperty("java.io.tmpdir"), "hu_HtmlPageTest_save3.html");
        final File cssFile = new File(System.getProperty("java.io.tmpdir"), "hu_HtmlPageTest_save3/second.css");
        try {
            page.save(file);
            assertTrue(file.exists());
            assertTrue(file.isFile());
            assertEquals(css, FileUtils.readFileToString(cssFile));
        }
        finally {
            file.delete();
            FileUtils.deleteDirectory(cssFile.getParentFile());
        }
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    @Alerts(FF = "Hello")
    public void application_javascript_type() throws Exception {
        final String html = "<html>\n"
            + "<body>\n"
            + "  <script type='application/javascript'>\n"
            + "    alert('Hello');\n"
            + "  </script>\n"
            + "</body></html>";

        loadPageWithAlerts(html);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void serialization_attributeListenerLock() throws Exception {
        final String html = "<html><head><script>"
            + "function foo() {"
            + "  document.getElementById('aframe').src = '" + URL_FIRST + "';"
            + "  return false;"
            + "}</script>"
            + "<body><iframe src='about:blank' id='aframe'></iframe>"
            + "<a href='#' onclick='foo()' id='link'>load iframe</a></body></html>";
        final HtmlPage page = loadPageWithAlerts(html);
        final WebClient copy = clone(page.getWebClient());
        final HtmlPage copyPage = (HtmlPage) copy.getCurrentWindow().getTopWindow().getEnclosedPage();
        copyPage.getElementById("link").click();
        assertEquals(URL_FIRST.toExternalForm(), copyPage.getElementById("aframe").getAttribute("src"));
    }

    /**
     * @exception Exception if the test fails
     */
    @Test
    @Alerts(IE = { "[object]", "1" }, FF = { "null", "0" })
    public void write_getElementById_afterParsing() throws Exception {
        final String html = "<html>\n"
            + "<head><title>foo</title><script>\n"
            + "  function test() {\n"
            + "    document.write(\"<input id='sendemail'>\");\n"
            + "    alert(document.getElementById('sendemail'));\n"
            + "    document.write(\"<input name='sendemail2'>\");\n"
            + "    alert(document.getElementsByName('sendemail2').length);\n"
            + "  }\n"
            + "</script></head>\n"
            + "<body onload='test()'></body></html>";
        loadPageWithAlerts(html);
    }

    /**
     * @exception Exception if the test fails
     */
    @Test
    @Alerts(IE = { "[object]", "1" }, FF = { "[object HTMLInputElement]", "1" })
    public void write_getElementById_duringParsing() throws Exception {
        final String html = "<html>\n"
            + "<head><title>foo</title></head>\n"
            + "<body><script>\n"
            + "    document.write(\"<input id='sendemail'>\");\n"
            + "    alert(document.getElementById('sendemail'));\n"
            + "    document.write(\"<input name='sendemail2'>\");\n"
            + "    alert(document.getElementsByName('sendemail2').length);\n"
            + "</script></body></html>";
        loadPageWithAlerts(html);
    }

}
