/*
 * The MIT License
 *
 * Copyright 2018 hughsaunders.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.rackspace.jenkins_nodepool;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlNumberInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import static org.junit.Assert.assertEquals;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.RestartableJenkinsRule;

/**
 * This test is in a separate class to the other NodePools tests as it uses a
 * restartable jenkins rule which is slower than the standard jenkins rule.
 *
 */
public class NodePoolsPersistenceTest {

    @Rule
    public RestartableJenkinsRule rr = new RestartableJenkinsRule();

    @Test
    public void testConfigWithRestart() {
        String connectionString = "localhost:2181";
        String labelPrefix = "testprefix";
        String requestTimeout = "20";
        rr.then(r -> {
            HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");

            HtmlTextInput connectionStringBox = config.getInputByName("_.connectionString");
            HtmlTextInput labelPrefixStringBox = config.getInputByName("_.labelPrefix");
            HtmlNumberInput requestTimeoutStringBox = config.getInputByName("_.requestTimeout");
            requestTimeoutStringBox.setText(requestTimeout);
            connectionStringBox.setText(connectionString);
            labelPrefixStringBox.setText(labelPrefix);
            r.submit(config);

        });
        rr.then(r -> {
            HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
            HtmlTextInput connectionStringBox = config.getInputByName("_.connectionString");
            HtmlTextInput labelPrefixStringBox = config.getInputByName("_.labelPrefix");
            HtmlNumberInput requestTimeoutStringBox = config.getInputByName("_.requestTimeout");
            assertEquals(connectionStringBox.getText(), connectionString);
            assertEquals(labelPrefixStringBox.getText(), labelPrefix);
            assertEquals(requestTimeoutStringBox.getText(), requestTimeout);
        });
    }
}
