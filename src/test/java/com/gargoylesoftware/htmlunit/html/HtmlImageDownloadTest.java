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

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebServerTestCase;

/**
 * Tests for {@link HtmlImage}.
 *
 * @version $Revision: 5301 $
 * @author Knut Johannes Dahle
 * @author Ahmed Ashour
 * @author Marc Guillemot
 */
public class HtmlImageDownloadTest extends WebServerTestCase {
    private static final String base_file_path_ = "src/test/resources/com/gargoylesoftware/htmlunit/html";

    /**
     * Constructor.
     * @throws Exception if an exception occurs
     */
    public HtmlImageDownloadTest() throws Exception {
        startWebServer(base_file_path_);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testImageHeight() throws Exception {
        final HtmlImage htmlimage = getHtmlElementToTest("image1");
        Assert.assertEquals("Image height", 612, htmlimage.getHeight());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testImageWidth() throws Exception {
        final HtmlImage htmlimage = getHtmlElementToTest("image1");
        Assert.assertEquals("Image width", 879, htmlimage.getWidth());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testImageFileSize() throws Exception {
        final HtmlImage htmlimage = getHtmlElementToTest("image1");
        Assert.assertEquals("Image filesize", 140144, htmlimage.getWebResponse(true).getContentAsBytes().length);
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testGetImageReader() throws Exception {
        final HtmlImage htmlimage = getHtmlElementToTest("image1");
        Assert.assertNotNull("ImageReader should not be null", htmlimage.getImageReader());
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testGetImageReaderNoneSupportedImage() throws Exception {
        final HtmlImage htmlimage = getHtmlElementToTest("image1");
        final String url = "/HtmlImageDownloadTest.html";
        htmlimage.setAttribute("src", url);
        try {
            htmlimage.getImageReader();
            Assert.fail("it was not an image!");
        }
        catch (final IOException ioe) {
            // Correct behaviour
        }
    }

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void testGetWebResponse() throws Exception {
        final HtmlImage htmlimage = getHtmlElementToTest("image1");
        final URL url = htmlimage.getPage().getWebResponse().getRequestSettings().getUrl();
        Assert.assertNull(htmlimage.getWebResponse(false));
        final WebResponse resp = htmlimage.getWebResponse(true);
        Assert.assertNotNull(resp);
        assertEquals(url.toExternalForm(), resp.getRequestSettings().getAdditionalHeaders().get("Referer"));
    }

    /**
     * Common code for the tests to load the testpage and fetch the HtmlImage object.
     * @param id value of image id attribute
     * @return the found HtmlImage
     * @throws Exception if an error occurs
     */
    private HtmlImage getHtmlElementToTest(final String id) throws Exception {
        final String url = "http://localhost:" + PORT + "/HtmlImageDownloadTest.html";
        final HtmlPage page = loadUrl(url);
        return (HtmlImage) page.getElementById(id);
    }
}
