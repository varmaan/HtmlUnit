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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Common superclass for HtmlUnit tests.
 *
 * @version $Revision: 5349 $
 * @author <a href="mailto:mbowler@GargoyleSoftware.com">Mike Bowler</a>
 * @author David D. Kilzer
 * @author Marc Guillemot
 * @author Chris Erskine
 * @author Michael Ottati
 * @author Daniel Gredler
 * @author Ahmed Ashour
 */
public abstract class WebTestCase {

    /** Logging support. */
    private static final Log LOG = LogFactory.getLog(WebTestCase.class);

    /** The listener port for the web server. */
    public static final int PORT = Integer.valueOf(System.getProperty("htmlunit.test.port", "12345"));

    /** Constant for the URL which is used in the tests. */
    public static final URL URL_FIRST;

    /** Constant for the URL which is used in the tests. */
    public static final URL URL_SECOND;

    /**
     * Constant for the URL which is used in the tests.
     * This URL doesn't use the same host name as {@link #URL_FIRST} and {@link #URL_SECOND}.
     **/
    public static final URL URL_THIRD;

    /**
     * The content type for JavaScript.
     */
    public static final String JAVASCRIPT_MIME_TYPE = "application/javascript";

    /**
     * The name of the system property used to determine if files should be generated
     * or not in {@link #createTestPageForRealBrowserIfNeeded(String,List)}.
     */
    public static final String PROPERTY_GENERATE_TESTPAGES
        = "com.gargoylesoftware.htmlunit.WebTestCase.GenerateTestpages";

    /** System-specific line separator. */
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private BrowserVersion browserVersion_;
    private WebClient webClient_;
    private MockWebConnection mockWebConnection_;

    private String[] expectedAlerts_;

    private static final BrowserVersion FLAG_ALL_BROWSERS = new BrowserVersion("", "", "", 0);
    private static final ThreadLocal<BrowserVersion> generateTest_browserVersion_ = new ThreadLocal<BrowserVersion>();
    private String generateTest_content_;
    private List<String> generateTest_expectedAlerts_;
    private boolean generateTest_notYetImplemented_;
    private String generateTest_testName_;
    private int nbJSThreadsBeforeTest_;

    static {
        try {
            URL_FIRST = new URL("http://localhost:" + PORT + "/");
            URL_SECOND = new URL("http://localhost:" + PORT + "/second/");
            URL_THIRD = new URL("http://127.0.0.1:" + PORT + "/third/");
        }
        catch (final MalformedURLException e) {
            // This is theoretically impossible.
            throw new IllegalStateException("Unable to create URL constants");
        }
    }

    /**
     * Constructor.
     */
    protected WebTestCase() {
        generateTest_browserVersion_.remove();
    }

    /**
     * Load a page with the specified HTML using the default browser version.
     * @param html the HTML to use
     * @return the new page
     * @throws Exception if something goes wrong
     */
    public final HtmlPage loadPage(final String html) throws Exception {
        return loadPage(html, null);
    }

    /**
     * Load a page with the specified HTML and collect alerts into the list.
     * @param browserVersion the browser version to use
     * @param html the HTML to use
     * @param collectedAlerts the list to hold the alerts
     * @return the new page
     * @throws Exception if something goes wrong
     */
    public final HtmlPage loadPage(final BrowserVersion browserVersion,
            final String html, final List<String> collectedAlerts) throws Exception {
        if (generateTest_browserVersion_.get() == null) {
            generateTest_browserVersion_.set(browserVersion);
        }
        return loadPage(browserVersion, html, collectedAlerts, getDefaultUrl());
    }

    /**
     * User the default browser version to load a page with the specified HTML
     * and collect alerts into the list.
     * @param html the HTML to use
     * @param collectedAlerts the list to hold the alerts
     * @return the new page
     * @throws Exception if something goes wrong
     */
    public final HtmlPage loadPage(final String html, final List<String> collectedAlerts) throws Exception {
        generateTest_browserVersion_.set(FLAG_ALL_BROWSERS);
        final BrowserVersion version = (browserVersion_ != null) ? browserVersion_ : BrowserVersion.getDefault();
        return loadPage(version, html, collectedAlerts, getDefaultUrl());
    }

    /**
     * Loads an external URL, accounting for the fact that the remote server may be down or the
     * machine running the tests may not be connected to the internet.
     * @param url the URL to load
     * @return the loaded page, or <tt>null</tt> if there were connectivity issues
     * @throws Exception if an error occurs
     */
    protected static final HtmlPage loadUrl(final String url) throws Exception {
        try {
            final WebClient client = new WebClient();
            client.setUseInsecureSSL(true);
            return client.getPage(url);
        }
        catch (final ConnectException e) {
            // The remote server is probably down.
            System.out.println("Connection could not be made to " + url);
            return null;
        }
        catch (final SocketException e) {
            // The local machine may not be online.
            System.out.println("Connection could not be made to " + url);
            return null;
        }
        catch (final UnknownHostException e) {
            // The local machine may not be online.
            System.out.println("Connection could not be made to " + url);
            return null;
        }
    }

    /**
     * Loads a page with the specified HTML and collect alerts into the list.
     * @param html the HTML to use
     * @param collectedAlerts the list to hold the alerts
     * @param url the URL that will use as the document host for this page
     * @return the new page
     * @throws Exception if something goes wrong
     */
    protected final HtmlPage loadPage(final String html, final List<String> collectedAlerts,
            final URL url) throws Exception {

        return loadPage(BrowserVersion.getDefault(), html, collectedAlerts, url);
    }

    /**
     * Load a page with the specified HTML and collect alerts into the list.
     * @param browserVersion the browser version to use
     * @param html the HTML to use
     * @param collectedAlerts the list to hold the alerts
     * @param url the URL that will use as the document host for this page
     * @return the new page
     * @throws Exception if something goes wrong
     */
    protected final HtmlPage loadPage(final BrowserVersion browserVersion,
            final String html, final List<String> collectedAlerts, final URL url) throws Exception {

        webClient_ = new WebClient(browserVersion);
        return loadPage(webClient_, html, collectedAlerts, url);
    }

    /**
     * Load a page with the specified HTML and collect alerts into the list.
     * @param client the WebClient to use (webConnection and alertHandler will be configured on it)
     * @param html the HTML to use
     * @param collectedAlerts the list to hold the alerts
     * @param url the URL that will use as the document host for this page
     * @return the new page
     * @throws Exception if something goes wrong
     */
    protected static final HtmlPage loadPage(final WebClient client,
            final String html, final List<String> collectedAlerts, final URL url) throws Exception {

        if (collectedAlerts != null) {
            client.setAlertHandler(new CollectingAlertHandler(collectedAlerts));
        }

        final MockWebConnection webConnection = new MockWebConnection();
        webConnection.setDefaultResponse(html);
        client.setWebConnection(webConnection);

        return client.getPage(url);
    }

    /**
     * Load a page with the specified HTML and collect alerts into the list.
     * @param client the WebClient to use (webConnection and alertHandler will be configured on it)
     * @param html the HTML to use
     * @param collectedAlerts the list to hold the alerts
     * @return the new page
     * @throws Exception if something goes wrong
     */
    protected final HtmlPage loadPage(final WebClient client,
            final String html, final List<String> collectedAlerts) throws Exception {

        return loadPage(client, html, collectedAlerts, getDefaultUrl());
    }

    /**
     * Assert that the specified object is null.
     * @param object the object to check
     */
    public static void assertNull(final Object object) {
        Assert.assertNull("Expected null but found [" + object + "]", object);
    }

    /**
     * Facility to test external form of urls. Comparing external form of URLs is
     * really faster than URL.equals() as the host doesn't need to be resolved.
     * @param expectedUrl the expected URL
     * @param actualUrl the URL to test
     */
    protected void assertEquals(final URL expectedUrl, final URL actualUrl) {
        Assert.assertEquals(expectedUrl.toExternalForm(), actualUrl.toExternalForm());
    }

    /**
     * Asserts the two objects are equal.
     * @param expected the expected object
     * @param actual the object to test
     */
    protected void assertEquals(final Object expected, final Object actual) {
        Assert.assertEquals(expected, actual);
    }

    /**
     * Facility to test external form of urls. Comparing external form of URLs is
     * really faster than URL.equals() as the host doesn't need to be resolved.
     * @param message the message to display if assertion fails
     * @param expectedUrl the string representation of the expected URL
     * @param actualUrl the URL to test
     */
    protected void assertEquals(final String message, final URL expectedUrl, final URL actualUrl) {
        Assert.assertEquals(message, expectedUrl.toExternalForm(), actualUrl.toExternalForm());
    }

    /**
     * Facility to test external form of an URL.
     * @param expectedUrl the string representation of the expected URL
     * @param actualUrl the URL to test
     */
    protected void assertEquals(final String expectedUrl, final URL actualUrl) {
        Assert.assertEquals(expectedUrl, actualUrl.toExternalForm());
    }

    /**
     * Facility method to avoid having to create explicitly a list from
     * a String[] (for example when testing received alerts).
     * Transforms the String[] to a List before calling
     * {@link junit.framework.Assert#assertEquals(java.lang.Object, java.lang.Object)}.
     * @param expected the expected strings
     * @param actual the collection of strings to test
     */
    protected void assertEquals(final String[] expected, final List<String> actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Facility method to avoid having to create explicitly a list from
     * a String[] (for example when testing received alerts).
     * Transforms the String[] to a List before calling
     * {@link junit.framework.Assert#assertEquals(java.lang.String, java.lang.Object, java.lang.Object)}.
     * @param message the message to display if assertion fails
     * @param expected the expected strings
     * @param actual the collection of strings to test
     */
    protected void assertEquals(final String message, final String[] expected, final List<String> actual) {
        Assert.assertEquals(message, Arrays.asList(expected).toString(), actual.toString());
    }

    /**
     * Facility to test external form of an URL.
     * @param message the message to display if assertion fails
     * @param expectedUrl the string representation of the expected URL
     * @param actualUrl the URL to test
     */
    protected void assertEquals(final String message, final String expectedUrl, final URL actualUrl) {
        Assert.assertEquals(message, expectedUrl, actualUrl.toExternalForm());
    }

    /**
     * Assert the specified condition is true.
     * @param condition condition to test
     */
    protected void assertTrue(final boolean condition) {
        Assert.assertTrue(condition);
    }

    /**
     * Assert the specified condition is true.
     * @param message message to show
     * @param condition condition to test
     */
    protected void assertTrue(final String message, final boolean condition) {
        Assert.assertTrue(message, condition);
    }

    /**
     * Assert the specified condition is false.
     * @param condition condition to test
     */
    protected void assertFalse(final boolean condition) {
        Assert.assertFalse(condition);
    }

    /**
     * Returns an input stream for the specified file name. Refer to {@link #getFileObject(String)}
     * for details on how the file is located.
     * @param fileName the base file name
     * @return the input stream
     * @throws FileNotFoundException if the file cannot be found
     */
    public static InputStream getFileAsStream(final String fileName) throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(getFileObject(fileName)));
    }

    /**
     * Returns a File object for the specified file name. This is different from just
     * <code>new File(fileName)</code> because it will adjust the location of the file
     * depending on how the code is being executed.
     *
     * @param fileName the base filename
     * @return the new File object
     * @throws FileNotFoundException if the file doesn't exist
     */
    public static File getFileObject(final String fileName) throws FileNotFoundException {
        final String localizedName = fileName.replace('/', File.separatorChar);

        File file = new File(localizedName);
        if (!file.exists()) {
            file = new File("../../" + localizedName);
        }

        if (!file.exists()) {
            try {
                System.out.println("currentDir=" + new File(".").getCanonicalPath());
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
            throw new FileNotFoundException(localizedName);
        }
        return file;
    }

    /**
     * Facility method transforming expectedAlerts to a list and calling
     * {@link #createTestPageForRealBrowserIfNeeded(String, List)}.
     * @param content the content of the HTML page
     * @param expectedAlerts the expected alerts
     * @throws IOException if writing file fails
     */
    protected void createTestPageForRealBrowserIfNeeded(final String content, final String[] expectedAlerts)
        throws IOException {
        createTestPageForRealBrowserIfNeeded(content, Arrays.asList(expectedAlerts));
    }

    /**
     * Generates an instrumented HTML file in the temporary dir to easily make a manual test in a real browser.
     * The file is generated only if the system property {@link #PROPERTY_GENERATE_TESTPAGES} is set.
     * @param content the content of the HTML page
     * @param expectedAlerts the expected alerts
     * @throws IOException if writing file fails
     */
    protected void createTestPageForRealBrowserIfNeeded(final String content, final List<String> expectedAlerts)
        throws IOException {

        // save the information to create a test for WebDriver
        generateTest_content_ = content;
        generateTest_expectedAlerts_ = expectedAlerts;
        final Method testMethod = findRunningJUnitTestMethod();
        generateTest_testName_ = testMethod.getDeclaringClass().getSimpleName() + "_" + testMethod.getName() + ".html";

        if (System.getProperty(PROPERTY_GENERATE_TESTPAGES) != null) {
            // should be optimized....

            // calls to alert() should be replaced by call to custom function
            String newContent = StringUtils.replace(content, "alert(", "htmlunitReserved_caughtAlert(");

            final String instrumentationJS = createInstrumentationScript(expectedAlerts);

            // first version, we assume that there is a <head> and a </body> or a </frameset>
            if (newContent.indexOf("<head>") > -1) {
                newContent = StringUtils.replaceOnce(newContent, "<head>", "<head>" + instrumentationJS);
            }
            else {
                newContent = StringUtils.replaceOnce(newContent, "<html>",
                        "<html>\n<head>\n" + instrumentationJS + "\n</head>\n");
            }
            final String endScript = "\n<script>htmlunitReserved_addSummaryAfterOnload();</script>\n";
            if (newContent.contains("</body>")) {
                newContent = StringUtils.replaceOnce(newContent, "</body>",  endScript + "</body>");
            }
            else {
                LOG.info("No test generated: currently only content with a <head> and a </body> is supported");
            }

            final File f = File.createTempFile("TEST" + '_', ".html");
            FileUtils.writeStringToFile(f, newContent, "ISO-8859-1");
            LOG.info("Test file written: " + f.getAbsolutePath());
        }
        else {
            LOG.debug("System property \"" + PROPERTY_GENERATE_TESTPAGES
                    + "\" not set, don't generate test HTML page for real browser");
        }
    }

    /**
     * @param expectedAlerts the list of the expected alerts
     * @return the script to be included at the beginning of the generated HTML file
     * @throws IOException in case of problem
     */
    private String createInstrumentationScript(final List<String> expectedAlerts) throws IOException {
        // generate the js code
        final InputStream is = getClass().getClassLoader().getResourceAsStream("alertVerifier.js");
        final String baseJS = IOUtils.toString(is);
        IOUtils.closeQuietly(is);

        final StringBuilder sb = new StringBuilder();
        sb.append("\n<script type='text/javascript'>\n");
        sb.append("var htmlunitReserved_tab = [");
        for (final ListIterator<String> iter = expectedAlerts.listIterator(); iter.hasNext();) {
            if (iter.hasPrevious()) {
                sb.append(", ");
            }
            String message = iter.next();
            message = StringUtils.replace(message, "\\", "\\\\");
            message = message.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
            sb.append("{expected: \"").append(message).append("\"}");
        }
        sb.append("];\n\n");
        sb.append(baseJS);
        sb.append("</script>\n");
        return sb.toString();
    }

    /**
     * Convenience method to pull the MockWebConnection out of an HtmlPage created with
     * the loadPage method.
     * @param page HtmlPage to get the connection from
     * @return the MockWebConnection that served this page
     */
    protected static final MockWebConnection getMockConnection(final HtmlPage page) {
        return (MockWebConnection) page.getWebClient().getWebConnection();
    }

    /**
     * Runs the calling JUnit test again and fails only if it already runs.<br/>
     * This is helpful for tests that don't currently work but should work one day,
     * when the tested functionality has been implemented.<br/>
     * The right way to use it is:
     * <pre>
     * public void testXXX() {
     *   if (notYetImplemented()) {
     *       return;
     *   }
     *
     *   ... the real (now failing) unit test
     * }
     * </pre>
     * @return <tt>false</tt> when not itself already in the call stack
     */
    protected boolean notYetImplemented() {
        generateTest_notYetImplemented_ = true;
        if (notYetImplementedFlag.get() != null) {
            return false;
        }
        notYetImplementedFlag.set(Boolean.TRUE);

        final Method testMethod = findRunningJUnitTestMethod();
        try {
            LOG.info("Running " + testMethod.getName() + " as not yet implemented");
            testMethod.invoke(this, (Object[]) new Class[] {});
            Assert.fail(testMethod.getName() + " is marked as not implemented but already works");
        }
        catch (final Exception e) {
            LOG.info(testMethod.getName() + " fails which is normal as it is not yet implemented");
            // method execution failed, it is really "not yet implemented"
        }
        finally {
            notYetImplementedFlag.set(null);
        }

        return true;
    }

    /**
     * Finds from the call stack the active running JUnit test case
     * @return the test case method
     * @throws RuntimeException if no method could be found
     */
    private Method findRunningJUnitTestMethod() {
        final Class< ? > cl = getClass();
        final Class< ? >[] args = new Class[] {};

        // search the initial junit test
        final Throwable t = new Exception();
        for (int i = t.getStackTrace().length - 1; i >= 0; i--) {
            final StackTraceElement element = t.getStackTrace()[i];
            if (element.getClassName().equals(cl.getName())) {
                try {
                    final Method m = cl.getMethod(element.getMethodName(), args);
                    if (isPublicTestMethod(m)) {
                        return m;
                    }
                }
                catch (final Exception e) {
                    // can't access, ignore it
                }
            }
        }

        throw new RuntimeException("No JUnit test case method found in call stack");
    }

    /**
     * From Junit. Test if the method is a junit test.
     * @param method the method
     * @return <code>true</code> if this is a junit test
     */
    private boolean isPublicTestMethod(final Method method) {
        return method.getParameterTypes().length == 0
            && (method.getName().startsWith("test") || method.getAnnotation(Test.class) != null)
            && method.getReturnType() == Void.TYPE
            && Modifier.isPublic(method.getModifiers());
    }

    private static final ThreadLocal<Boolean> notYetImplementedFlag = new ThreadLocal<Boolean>();

    /**
     * Load the specified resource for the supported browsers and tests
     * that the generated log corresponds to the expected one for this browser.
     *
     * @param fileName the resource name which resides in /resources folder and
     *        belongs to the same package as the test class.
     *
     * @throws Exception if the test fails
     */
    protected void testHTMLFile(final String fileName) throws Exception {
        final String resourcePath = getClass().getPackage().getName().replace('.', '/') + '/' + fileName;
        final URL url = getClass().getClassLoader().getResource(resourcePath);

        final Map<String, BrowserVersion> testedBrowser = new HashMap<String, BrowserVersion>();
        testedBrowser.put("FIREFOX_2", BrowserVersion.FIREFOX_2);
        testedBrowser.put("INTERNET_EXPLORER_6_0", BrowserVersion.INTERNET_EXPLORER_6);

        for (final Map.Entry<String, BrowserVersion> entry : testedBrowser.entrySet()) {
            final String browserKey = entry.getKey();
            final BrowserVersion browserVersion = entry.getValue();

            final WebClient client = new WebClient(browserVersion);

            final HtmlPage page = client.getPage(url);
            final HtmlElement want = page.getHtmlElementById(browserKey);

            final HtmlElement got = page.getHtmlElementById("log");

            final List<String> expected = readChildElementsText(want);
            final List<String> actual = readChildElementsText(got);

            Assert.assertEquals(expected, actual);
        }
    }

    private List<String> readChildElementsText(final HtmlElement elt) {
        final List<String> list = new ArrayList<String>();
        for (final HtmlElement child : elt.getChildElements()) {
            list.add(child.asText());
        }
        return list;
    }

    void setBrowserVersion(final BrowserVersion browserVersion) {
        browserVersion_ = browserVersion;
    }

    /**
     * Returns the WebClient instance for the current test with the current {@link BrowserVersion}.
     * @return a WebClient with the current {@link BrowserVersion}
     */
    protected final WebClient createNewWebClient() {
        return new WebClient(getBrowserVersion());
    }

    /**
     * Returns the WebClient instance for the current test with the current {@link BrowserVersion}.
     * @return a WebClient with the current {@link BrowserVersion}
     */
    protected final WebClient getWebClient() {
        if (webClient_ == null) {
            webClient_ = createNewWebClient();
        }
        return webClient_;
    }

    /**
     * Returns the WebClient instance for the current test with the current {@link BrowserVersion}.
     * @return a WebClient with the current {@link BrowserVersion}
     */
    protected final WebClient getWebClientWithMockWebConnection() {
        if (webClient_ == null) {
            webClient_ = createNewWebClient();
            webClient_.setWebConnection(getMockWebConnection());
        }
        return webClient_;
    }

    /**
     * Returns the mock WebConnection instance for the current test.
     * @return the mock WebConnection instance for the current test
     */
    protected MockWebConnection getMockWebConnection() {
        if (mockWebConnection_ == null) {
            mockWebConnection_ = new MockWebConnection();
        }
        return mockWebConnection_;
    }

    /**
     * Sets the mock WebConnection instance for the current test.
     * @param connection the connection to use
     */
    protected void setMockWebConnection(final MockWebConnection connection) {
        mockWebConnection_ = connection;
    }

    /**
     * Returns the current {@link BrowserVersion}.
     * @return current {@link BrowserVersion}
     */
    protected final BrowserVersion getBrowserVersion() {
        if (browserVersion_ == null) {
            throw new IllegalStateException("You must annotate the test class with '@RunWith(BrowserRunner.class)'");
        }
        return browserVersion_;
    }

    /**
     * Sets the expected alerts.
     * @param expectedAlerts the expected alerts
     */
    protected void setExpectedAlerts(final String... expectedAlerts) {
        expectedAlerts_ = expectedAlerts;
    }

    /**
     * Returns the expected alerts.
     * @return the expected alerts
     */
    protected String[] getExpectedAlerts() {
        return expectedAlerts_;
    }

    /**
     * Defines the provided HTML as the response of the MockWebConnection for {@link #getDefaultUrl()}
     * and loads the page with this URL using the current browser version; finally, asserts that the
     * alerts equal the expected alerts (in which "§§URL§§" has been expanded to the default URL).
     * @param html the HTML to use
     * @return the new page
     * @throws Exception if something goes wrong
     */
    protected final HtmlPage loadPageWithAlerts(final String html) throws Exception {
        return loadPageWithAlerts(html, getDefaultUrl(), -1);
    }

    /**
     * Defines the provided HTML as the response of the MockWebConnection for {@link #getDefaultUrl()}
     * and loads the page with this URL using the current browser version; finally, asserts the alerts
     * equal the expected alerts.
     * @param html the HTML to use
     * @param url the URL from which the provided HTML code should be delivered
     * @param waitForJS the milliseconds to wait for background JS tasks to complete. Ignored if -1.
     * @return the new page
     * @throws Exception if something goes wrong
     */
    protected final HtmlPage loadPageWithAlerts(final String html, final URL url, final int waitForJS)
        throws Exception {
        if (expectedAlerts_ == null) {
            throw new IllegalStateException("You must annotate the test class with '@RunWith(BrowserRunner.class)'");
        }

        // expand variables in expected alerts
        expandExpectedAlertsVariables(url);

        createTestPageForRealBrowserIfNeeded(html, expectedAlerts_);

        final WebClient client = getWebClientWithMockWebConnection();
        final List<String> collectedAlerts = new ArrayList<String>();
        client.setAlertHandler(new CollectingAlertHandler(collectedAlerts));

        final MockWebConnection webConnection = getMockWebConnection();
        webConnection.setResponse(url, html);

        final HtmlPage page = client.getPage(url);
        if (waitForJS > 0) {
            assertEquals(0, client.waitForBackgroundJavaScriptStartingBefore(waitForJS));
        }
        assertEquals(expectedAlerts_, collectedAlerts);
        return page;
    }

    /**
     * Expand "§§URL§§" to the provided URL in the expected alerts.
     * @param url the url to expand
     */
    protected void expandExpectedAlertsVariables(final URL url) {
        if (expectedAlerts_ == null) {
            throw new IllegalStateException("You must annotate the test class with '@RunWith(BrowserRunner.class)'");
        }
        for (int i = 0; i < expectedAlerts_.length; ++i) {
            expectedAlerts_[i] = expectedAlerts_[i].replaceAll("§§URL§§", url.toExternalForm());
        }
    }

    /**
     * A generics-friendly version of {@link SerializationUtils#clone(Serializable)}.
     * @param <T> the type of the object being cloned
     * @param object the object being cloned
     * @return a clone of the specified object
     */
    @SuppressWarnings("unchecked")
    protected <T extends Serializable> T clone(final T object) {
        return (T) SerializationUtils.clone(object);
    }

    /**
     * Gets the default URL used for the tests.
     * @return the url
     */
    protected static URL getDefaultUrl() {
        return URL_FIRST;
    }

    /**
     * Generates an HTML file that can be loaded and understood as a test.
     * @throws IOException in case of problem
     */
    @After
    public void generateTestForWebDriver() throws IOException {
        if (generateTest_content_ != null && !generateTest_notYetImplemented_) {
            final File targetDir = new File("target/generated_tests");
            targetDir.mkdirs();

            final File outFile = new File(targetDir, generateTest_testName_);

            // replace alert(x) by a storage in window's scope
            // Convert to string here due to: http://code.google.com/p/webdriver/issues/detail?id=209
            final String newContent = StringUtils.replace(generateTest_content_, "alert(",
                "(function(t){var x = window.__huCatchedAlerts; x = x ? x : []; "
                + "window.__huCatchedAlerts = x; x.push(String(t))})(");

            FileUtils.writeStringToFile(outFile, newContent);

            // write the expected alerts
            final String suffix;
            BrowserVersion browser = generateTest_browserVersion_.get();
            if (browser == null) {
                browser = getBrowserVersion();
            }
            if (browser == FLAG_ALL_BROWSERS) {
                suffix = ".expected";
            }
            else {
                suffix = "." + browser.getNickname() + ".expected";
            }

            final File expectedLog = new File(outFile.getParentFile(), outFile.getName() + suffix);

            final FileOutputStream fos = new FileOutputStream(expectedLog);
            final ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(generateTest_expectedAlerts_);
            oos.close();
        }
    }

    /**
     * Reads the number of JS threads remaining from unit tests run before the current one.
     * Ideally it should be always 0.
     */
    @Before
    public void readJSThreadsBeforeTest() {
        nbJSThreadsBeforeTest_ = getJavaScriptThreads().size();
    }

    /**
     * Cleanup after a test.
     */
    @After
    public void releaseResources() {
        if (webClient_ != null) {
            webClient_.closeAllWindows();
        }
        webClient_ = null;
        mockWebConnection_ = null;

        final List<Thread> jsThreads = getJavaScriptThreads();
        if (jsThreads.size() > nbJSThreadsBeforeTest_) {
            System.err.println("JS threads still running:");
            for (final Thread t : jsThreads) {
                System.err.println(t.getName());
                final StackTraceElement elts[] = t.getStackTrace();
                for (final StackTraceElement elt : elts) {
                    System.err.println(elt);
                }
            }
            throw new RuntimeException("JS threads are still running: " + jsThreads.size());
        }
    }

    /**
     * Gets the active JavaScript threads.
     * @return the threads
     */
    protected List<Thread> getJavaScriptThreads() {
        final Thread[] threads = new Thread[Thread.activeCount() + 10];
        Thread.enumerate(threads);
        final List<Thread> jsThreads = new ArrayList<Thread>();
        for (final Thread t : threads) {
            if (t != null && t.getName().startsWith("JavaScript Job Thread")) {
                jsThreads.add(t);
            }
        }

        return jsThreads;
    }
}
