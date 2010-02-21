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
package com.gargoylesoftware.htmlunit.libraries;

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.BrowserRunner.Browser;
import com.gargoylesoftware.htmlunit.BrowserRunner.NotYetImplemented;

/**
 * Tests for compatibility with local loading of
 * version 1.2.6 of the <a href="http://jquery.com/">jQuery JavaScript library</a>.
 *
 * @version $Revision: 5301 $
 * @author Daniel Gredler
 * @author Ahmed Ashour
 * @see JQuery126Test
 */
@RunWith(BrowserRunner.class)
public class JQuery126LocalTest extends JQueryTestBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExpectedPath() throws Exception {
        final String v = getVersion();
        final String resource = "libraries/jquery/" + v + "/local." + getBrowserVersion().getNickname() + ".txt";
        final URL url = getClass().getClassLoader().getResource(resource);
        return url.toURI().getPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getUrl() {
        final String v = getVersion();
        return getClass().getClassLoader().getResource("libraries/jquery/" + v + "/test/index.html").toExternalForm();
    }

    /**
     * @throws Exception if an error occurs
     */
    @Test
    @NotYetImplemented(Browser.IE8)
    public void test() throws Exception {
        runTest();
    }
}
