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

import hudson.Extension;
import hudson.model.Label;
import hudson.widgets.Widget;
import jenkins.model.Jenkins;

/**
 * Jenkins UI widget to show information about requests being handled via NodePool
 *
 * @author Rackspcae
 */
@Extension
public class RequestsWidget extends Widget {

    private final NodePools nodePools;

    public RequestsWidget() {
        nodePools = NodePools.get();
    }

    public NodePools getNodePools() {
        return nodePools;
    }

    @Override
    public String getUrlName() {
        return "nodepoolRequests";
    }

    public String getLabelUrl(String l) {
        Jenkins j = Jenkins.getInstance();
        Label label = j.getLabel(l);
        return label.getUrl();
    }
}
