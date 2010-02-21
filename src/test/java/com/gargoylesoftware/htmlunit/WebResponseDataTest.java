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
package com.gargoylesoftware.htmlunit;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;

/**
 * Tests for {@link WebResponseData}.
 *
 * @version $Revision: 5301 $
 * @author Daniel Gredler
 * @author Ahmed Ashour
 */
public class WebResponseDataTest extends WebServerTestCase {

    private static final String GZIPPED_FILE = "testfiles/test.html.gz";

    /**
     * Tests that gzipped content is handled correctly.
     * @throws Exception if the test fails
     */
    @Test
    public void testGZippedContent() throws Exception {
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(GZIPPED_FILE);
        final byte[] zippedContent = IOUtils.toByteArray(stream);

        final List<NameValuePair> headers = new ArrayList<NameValuePair>();
        headers.add(new NameValuePair("Content-Encoding", "gzip"));

        final WebResponseData data = new WebResponseData(zippedContent, HttpStatus.SC_OK, "OK", headers);
        final String body = new String(data.getBody(), "UTF-8");
        assertTrue(StringUtils.contains(body, "Test"));
    }

    /**
     * Verifies that a null body input stream is handled correctly. A null body may be sent, for
     * example, when a 304 (Not Modified) response is sent to the client. See bug 1706505.
     * @throws Exception if the test fails
     */
    @Test
    public void testNullBody() throws Exception {
        final InputStream body = null;
        final List<NameValuePair> headers = new ArrayList<NameValuePair>();
        final WebResponseData data = new WebResponseData(body, 304, "NOT_MODIFIED", headers);
        assertNull(data.getBody());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void deflateCompression() throws Exception {
        startWebServer("src/test/resources/pjl-comp-filter", null);
        final  WebRequestSettings settings = new WebRequestSettings(new URL("http://localhost:"
            + PORT + "/index.html"));
        settings.setAdditionalHeader("Accept-Encoding", "deflate");
        final WebClient webClient = new WebClient();
        final HtmlPage page = webClient.getPage(settings);
        assertEquals("Hello Compressed World!", page.asText());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void redirection() throws Exception {
        final Map<String, Class< ? extends Servlet>> servlets = new HashMap<String, Class< ? extends Servlet>>();
        servlets.put("/folder1/page1", RedirectionServlet.class);
        servlets.put("/folder2/page2", RedirectionServlet.class);
        startWebServer("./", null, servlets);

        final WebClient client = new WebClient();

        final HtmlPage page = client.getPage("http://localhost:" + PORT + "/folder1/page1");
        assertEquals("Hello Redirected!", page.asText());
    }

    /**
     * Servlet for {@link #redirection()}.
     */
    public static class RedirectionServlet extends HttpServlet {

        private static final long serialVersionUID = 2865392611660286450L;

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            if (request.getRequestURI().equals("/folder1/page1")) {
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
                final String location = request.getRequestURL().toString().replace("page1", "../folder2/page2");
                response.setHeader("Location", location);
                return;
            }
            response.setContentType("text/html");
            final Writer writer = response.getWriter();
            writer.write("Hello Redirected!");
            writer.close();
        }
    }
}
