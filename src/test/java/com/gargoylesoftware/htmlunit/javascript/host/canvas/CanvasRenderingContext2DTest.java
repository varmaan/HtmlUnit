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
package com.gargoylesoftware.htmlunit.javascript.host.canvas;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.BrowserRunner;
import com.gargoylesoftware.htmlunit.WebTestCase;

/**
 * Unit tests for {@link CanvasRenderingContext2D}.
 *
 * @version $Revision: 5351 $
 * @author Ahmed Ashour
 */
@RunWith(BrowserRunner.class)
public class CanvasRenderingContext2DTest extends WebTestCase {

    /**
     * @throws Exception if an error occurs
     */
    @Test
    public void test() throws Exception {
        final String html =
            "<html>\n"
            + "  <head>\n"
            + "    <script>\n"
            + "      function test() {\n"
            + "        var canvas = document.getElementById('myCanvas');\n"
            + "        if (canvas.getContext){\n"
            + "          var ctx = canvas.getContext('2d');\n"
            + "          ctx.fillStyle = 'rgb(200,0,0)';\n"
            + "          ctx.fillRect(10, 10, 55, 50);\n"
            + "          ctx.fillStyle = 'rgba(0, 0, 200, 0.5)';\n"
            + "          ctx.fillRect(30, 30, 55, 50);\n"
            + "          ctx.drawImage(canvas, 1, 2);\n"
            + "          ctx.drawImage(canvas, 1, 2, 3, 4);\n"
            + "          ctx.drawImage(canvas, 1, 1, 1, 1, 1, 1, 1, 1);\n"
            + "        }\n"
            + "      }\n"
            + "    </script>\n"
            + "  </head>\n"
            + "  <body onload='test()'><canvas id='myCanvas'></canvas></body>\n"
            + "</html>";
        loadPageWithAlerts(html);
    }

}
