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

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebServerTestCase;
import com.gargoylesoftware.htmlunit.util.KeyDataPair;

/**
 * Tests for {@link HtmlFileInput}.
 *
 * @version $Revision: 5375 $
 * @author Marc Guillemot
 * @author Ahmed Ashour
 */
public class HtmlFileInputTest extends WebServerTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testFileInput() throws Exception {
        String path = getClass().getClassLoader().getResource("testfiles/" + "tiny-png.img").toExternalForm();
        testFileInput(path);
        final File file = new File(new URI(path));
        testFileInput(file.getCanonicalPath());

        if (path.startsWith("file:")) {
            path = path.substring("file:".length());
        }
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            testFileInput(URLDecoder.decode(path.replace('/', '\\'), "UTF-8"));
        }
        testFileInput("file:/" + path);
        testFileInput("file://" + path);
        testFileInput("file:///" + path);
    }

    /**
     * Tests setData method.
     * @throws Exception if the test fails
     */
    @Test
    public void testSetData() throws Exception {
        final String firstContent = "<html><head></head><body>\n"
            + "<form enctype='multipart/form-data' action='" + URL_SECOND + "' method='POST'>\n"
            + "  <input type='file' name='image' />\n"
            + "</form>\n"
            + "</body>\n"
            + "</html>";
        final String secondContent = "<html><head><title>second</title></head></html>";
        final WebClient client = new WebClient();

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setResponse(URL_FIRST, firstContent);
        webConnection.setResponse(URL_SECOND, secondContent);

        client.setWebConnection(webConnection);

        final HtmlPage firstPage = client.getPage(URL_FIRST);
        final HtmlForm f = firstPage.getForms().get(0);
        final HtmlFileInput fileInput = f.getInputByName("image");
        fileInput.setValueAttribute("dummy.txt");
        fileInput.setContentType("text/csv");
        fileInput.setData("My file data".getBytes());
        f.submit((SubmittableElement) null);
        final KeyDataPair pair = (KeyDataPair) webConnection.getLastParameters().get(0);
        assertNotNull(pair.getData());
        assertTrue(pair.getData().length != 0);
    }

    private void testFileInput(final String fileURL) throws Exception {
        final String firstContent = "<html><head></head><body>\n"
            + "<form enctype='multipart/form-data' action='" + URL_SECOND + "' method='POST'>\n"
            + "  <input type='file' name='image' />\n"
            + "</form>\n"
            + "</body>\n"
            + "</html>";
        final String secondContent = "<html><head><title>second</title></head></html>";
        final WebClient client = new WebClient();

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setResponse(URL_FIRST, firstContent);
        webConnection.setResponse(URL_SECOND, secondContent);

        client.setWebConnection(webConnection);

        final HtmlPage firstPage = client.getPage(URL_FIRST);
        final HtmlForm f = firstPage.getForms().get(0);
        final HtmlFileInput fileInput = f.getInputByName("image");
        fileInput.setValueAttribute(fileURL);
        f.submit((SubmittableElement) null);
        final KeyDataPair pair = (KeyDataPair) webConnection.getLastParameters().get(0);
        assertNotNull(pair.getFile());
        assertTrue(pair.getFile().length() != 0);
    }

    /**
     * Verifies that content is provided for a not filled file input.
     * @throws Exception if the test fails
     */
    @Test
    public void testEmptyField() throws Exception {
        final String firstContent = "<html><head></head><body>\n"
            + "<form enctype='multipart/form-data' action='" + URL_SECOND + "' method='POST'>\n"
            + "  <input type='file' name='image' />\n"
            + "</form>\n"
            + "</body>\n"
            + "</html>";
        final String secondContent = "<html><head><title>second</title></head></html>";
        final WebClient client = new WebClient();

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setResponse(URL_FIRST, firstContent);
        webConnection.setResponse(URL_SECOND, secondContent);

        client.setWebConnection(webConnection);

        final HtmlPage firstPage = client.getPage(URL_FIRST);
        final HtmlForm f = firstPage.getForms().get(0);
        f.submit(null);
        final KeyDataPair pair = (KeyDataPair) webConnection.getLastParameters().get(0);
        assertEquals("image", pair.getName());
        assertNull(pair.getFile());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testContentType() throws Exception {
        final String firstContent = "<html><head></head><body>\n"
            + "<form enctype='multipart/form-data' action='" + URL_SECOND + "' method='POST'>\n"
            + "  <input type='file' name='image' />\n"
            + "  <input type='submit' name='mysubmit'/>\n"
            + "</form>\n"
            + "</body>\n"
            + "</html>";
        final String secondContent = "<html><head><title>second</title></head></html>";
        final WebClient client = new WebClient();

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setResponse(URL_FIRST, firstContent);
        webConnection.setResponse(URL_SECOND, secondContent);

        client.setWebConnection(webConnection);

        final HtmlPage firstPage = client.getPage(URL_FIRST);
        final HtmlForm f = firstPage.getForms().get(0);
        final HtmlFileInput fileInput = f.getInputByName("image");

        final URL fileURL = getClass().getClassLoader().getResource("testfiles/empty.png");

        fileInput.setValueAttribute(fileURL.toExternalForm());
        f.<HtmlInput>getInputByName("mysubmit").click();
        final KeyDataPair pair = (KeyDataPair) webConnection.getLastParameters().get(0);
        assertNotNull(pair.getFile());
        Assert.assertFalse("Content type: " + pair.getContentType(), "text/webtest".equals(pair.getContentType()));

        fileInput.setContentType("text/webtest");
        f.<HtmlInput>getInputByName("mysubmit").click();
        final KeyDataPair pair2 = (KeyDataPair) webConnection.getLastParameters().get(0);
        assertNotNull(pair2.getFile());
        assertEquals("text/webtest", pair2.getContentType());
    }

    /**
     * Test HttpClient for uploading a file with non-ASCII name, if it works it means HttpClient has fixed its bug.
     *
     * Test for http://issues.apache.org/jira/browse/HTTPCLIENT-293,
     * which is related to http://sourceforge.net/tracker/index.php?func=detail&aid=1818569&group_id=47038&atid=448266
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUploadFileWithNonASCIIName_HttpClient() throws Exception {
        if (notYetImplemented()) {
            return;
        }

        final String filename = "\u6A94\u6848\uD30C\uC77C\u30D5\u30A1\u30A4\u30EB\u0645\u0644\u0641.txt";
        final String path = getClass().getClassLoader().getResource(filename).toExternalForm();
        final File file = new File(new URI(path));
        assertTrue(file.exists());

        final Map<String, Class< ? extends Servlet>> servlets = new HashMap<String, Class< ? extends Servlet>>();
        servlets.put("/upload2", Upload2Servlet.class);

        startWebServer("./", null, servlets);
        final PostMethod filePost = new PostMethod("http://localhost:" + PORT + "/upload2");

        final FilePart part = new FilePart("myInput", file);
        part.setCharSet("UTF-8");

        filePost.setRequestEntity(new MultipartRequestEntity(new Part[] {part}, filePost.getParams()));
        final HttpClient client = new HttpClient();
        client.executeMethod(filePost);

        final String response = filePost.getResponseBodyAsString();
        //this is the value with ASCII encoding
        assertFalse("3F 3F 3F 3F 3F 3F 3F 3F 3F 3F 3F 2E 74 78 74 <br>myInput".equals(response));
    }

    /**
     * Test uploading a file with non-ASCII name.
     *
     * Test for http://sourceforge.net/tracker/index.php?func=detail&aid=1818569&group_id=47038&atid=448266
     *
     * @throws Exception if the test fails
     */
    @Test
    public void testUploadFileWithNonASCIIName() throws Exception {
        final Map<String, Class< ? extends Servlet>> servlets = new HashMap<String, Class< ? extends Servlet>>();
        servlets.put("/upload1", Upload1Servlet.class);
        servlets.put("/upload2", Upload2Servlet.class);
        startWebServer("./", null, servlets);

        testUploadFileWithNonASCIIName(BrowserVersion.FIREFOX_3);
        testUploadFileWithNonASCIIName(BrowserVersion.INTERNET_EXPLORER_7);
    }

    private void testUploadFileWithNonASCIIName(final BrowserVersion browserVersion) throws Exception {
        final String filename = "\u6A94\u6848\uD30C\uC77C\u30D5\u30A1\u30A4\u30EB\u0645\u0644\u0641.txt";
        final String path = getClass().getClassLoader().getResource(filename).toExternalForm();
        final File file = new File(new URI(path));
        assertTrue(file.exists());

        final WebClient client = new WebClient(browserVersion);
        final HtmlPage firstPage = client.getPage("http://localhost:" + PORT + "/upload1");

        final HtmlForm form = firstPage.getForms().get(0);
        final HtmlFileInput fileInput = form.getInputByName("myInput");
        fileInput.setValueAttribute(path);

        final HtmlSubmitInput submitInput = form.getInputByValue("Upload");
        final HtmlPage secondPage = submitInput.click();

        final String response = secondPage.getWebResponse().getContentAsString();

        //this is the value with UTF-8 encoding
        final String expectedResponse = "6A94 6848 D30C C77C 30D5 30A1 30A4 30EB 645 644 641 2E 74 78 74 <br>myInput";

        assertTrue("Invalid Response: " + response, response.contains(expectedResponse));

        if (browserVersion.isIE()) {
            assertTrue(expectedResponse.length() < response.length());
        }
        else {
            assertEquals(expectedResponse.length(), response.length());
        }
    }

    /**
     * Servlet for '/upload1'.
     */
    public static class Upload1Servlet extends HttpServlet {

        private static final long serialVersionUID = 6693252829875297263L;

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            response.getWriter().write("<html>"
                + "<body><form action='upload2' method='post' enctype='multipart/form-data'>\n"
                + "Name: <input name='myInput' type='file'><br>\n"
                + "Name 2 (should stay empty): <input name='myInput2' type='file'><br>\n"
                + "<input type='submit' value='Upload' id='mySubmit'>\n"
                + "</form></body></html>\n");
        }
    }

    /**
     * Servlet for '/upload2'.
     */
    public static class Upload2Servlet extends HttpServlet {

        private static final long serialVersionUID = -1350878755076138012L;

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            final Writer writer = response.getWriter();
            if (ServletFileUpload.isMultipartContent(request)) {
                try {
                    final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                    for (final FileItem item : (List<FileItem>) upload.parseRequest(request)) {
                        if ("myInput".equals(item.getFieldName())) {
                            final String path = item.getName();
                            for (final char ch : path.toCharArray()) {
                                writer.write(Integer.toHexString(ch).toUpperCase() + " ");
                            }
                            writer.write("<br>");
                            writer.write(item.getFieldName());
                        }
                    }
                }
                catch (final FileUploadBase.SizeLimitExceededException e) {
                    writer.write("SizeLimitExceeded");
                }
                catch (final Exception e) {
                    writer.write("error");
                }
            }
            writer.close();
        }
    }
}
