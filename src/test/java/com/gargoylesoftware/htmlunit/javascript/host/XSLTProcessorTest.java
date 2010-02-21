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
package com.gargoylesoftware.htmlunit.javascript.host;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebTestCase;

/**
 * Tests for {@link XSLTProcessor}.
 *
 * @version $Revision: 5347 $
 * @author Ahmed Ashour
 */
public class XSLTProcessorTest extends WebTestCase {

    /**
     * @throws Exception if the test fails
     */
    @Test
    public void test() throws Exception {
        final String[] expectedAlertsIE = {"97"};
        test(BrowserVersion.INTERNET_EXPLORER_7, expectedAlertsIE);
        final String[] expectedAlertsFF = {"97", "null"};
        test(BrowserVersion.FIREFOX_3, expectedAlertsFF);
    }

    private void test(final BrowserVersion browserVersion, final String[] expectedAlerts) throws Exception {
        final String html = "<html><head><title>foo</title><script>\n"
            + "  function test() {\n"
            + "    var xmlDoc = createXmlDocument();\n"
            + "    xmlDoc.async = false;\n"
            + "    xmlDoc.load('" + URL_SECOND + "');\n"
            + "    \n"
            + "    var xslDoc;\n"
            + "    if (window.ActiveXObject)\n"
            + "      xslDoc = new ActiveXObject('Msxml2.FreeThreadedDOMDocument.3.0');\n"
            + "    else\n"
            + "      xslDoc = createXmlDocument();\n"
            + "    xslDoc.async = false;\n"
            + "    xslDoc.load('" + URL_THIRD + "');\n"
            + "    \n"
            + "    if (window.ActiveXObject) {\n"
            + "      var xslt = new ActiveXObject('Msxml2.XSLTemplate.3.0');\n"
            + "      xslt.stylesheet = xslDoc;\n"
            + "      var xslProc = xslt.createProcessor();\n"
            + "      xslProc.input = xmlDoc;\n"
            + "      xslProc.transform();\n"
            + "      var s = xslProc.output.replace(/\\r?\\n/g, '');\n"
            + "      alert(s.length);\n"
            + "      xslProc.input = xmlDoc.documentElement;\n"
            + "      xslProc.transform();\n"
            + "    } else {\n"
            + "      var processor = new XSLTProcessor();\n"
            + "      processor.importStylesheet(xslDoc);\n"
            + "      var newDocument = processor.transformToDocument(xmlDoc);\n"
            + "      alert(new XMLSerializer().serializeToString(newDocument.documentElement).length);\n"
            + "      newDocument = processor.transformToDocument(xmlDoc.documentElement);\n"
            + "      alert(newDocument.documentElement);\n"
            + "    }\n"
            + "  }\n"
            + "  function createXmlDocument() {\n"
            + "    if (document.implementation && document.implementation.createDocument)\n"
            + "      return document.implementation.createDocument('', '', null);\n"
            + "    else if (window.ActiveXObject)\n"
            + "      return new ActiveXObject('Microsoft.XMLDOM');\n"
            + "  }\n"
            + "</script></head><body onload='test()'>\n"
            + "</body></html>";

        final String xml
            = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
            + "<catalog>\n"
            + "  <cd>\n"
            + "    <title>Empire Burlesque</title>\n"
            + "    <artist>Bob Dylan</artist>\n"
            + "    <country>USA</country>\n"
            + "    <company>Columbia</company>\n"
            + "    <price>10.90</price>\n"
            + "    <year>1985</year>\n"
            + "  </cd>\n"
            + "</catalog>";

        final String xsl
            = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n"
            + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
            + "  <xsl:template match=\"/\">\n"
            + "  <html>\n"
            + "    <body>\n"
            + "      <h2>My CD Collection</h2>\n"
            + "      <ul>\n"
            + "      <xsl:for-each select=\"catalog/cd\">\n"
            + "        <li><xsl:value-of select='title'/> (<xsl:value-of select='artist'/>)</li>\n"
            + "      </xsl:for-each>\n"
            + "      </ul>\n"
            + "    </body>\n"
            + "  </html>\n"
            + "  </xsl:template>\n"
            + "</xsl:stylesheet>";

        final List<String> collectedAlerts = new ArrayList<String>();
        final WebClient client = new WebClient(browserVersion);
        client.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
        final MockWebConnection conn = new MockWebConnection();
        conn.setResponse(URL_FIRST, html);
        conn.setResponse(URL_SECOND, xml, "text/xml");
        conn.setResponse(URL_THIRD, xsl, "text/xml");
        client.setWebConnection(conn);

        client.getPage(URL_FIRST);
        assertEquals(expectedAlerts, collectedAlerts);
    }
}
