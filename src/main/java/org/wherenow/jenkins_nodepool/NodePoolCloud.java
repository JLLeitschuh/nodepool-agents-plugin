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
package org.wherenow.jenkins_nodepool;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Label;
import hudson.slaves.NodeProvisioner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author hughsaunders
 */
public class NodePoolCloud extends hudson.slaves.Cloud {

    private static final Logger LOGGER = Logger.getLogger(NodePoolCloud.class.getName());
    private static final String NODEPOOL_LABEL_PREFIX = "nodepool-";

    private String connectionString;

    @DataBoundConstructor
    public NodePoolCloud(String name, String connectionString) {
        super(name);
	    this.connectionString = connectionString;
    }
    
    public String getConnectionString(){
	    return connectionString;
    }

    public String getName(){
	    return name;
    }

    @Override
    public Collection<NodeProvisioner.PlannedNode> provision(Label label, int i) {

        try {
            // remove label prefix
            final int startIndex = NODEPOOL_LABEL_PREFIX.length();
            final String nodePoolLabel = label.getName().substring(startIndex);
            LOGGER.log(Level.INFO, "Provisioning label " + nodePoolLabel);

            // Return a description of a node that is being built asynchronously:
            final NodePoolClient client = new NodePoolClient(connectionString);
            final NodeRequest request = client.requestNode(nodePoolLabel);

            final NodeProvisioner.PlannedNode plannedNode = new NodeProvisioner.PlannedNode(
                    label + request.getNodePoolID(), // display name of node
                    new NodePoolNode(client, request),
                    1 // number of executors

            );
            final List<NodeProvisioner.PlannedNode> nodeList =  new ArrayList<NodeProvisioner.PlannedNode>();
            nodeList.add(plannedNode);
            return nodeList;

        } catch (Exception e) {
            e.printStackTrace();

            // return empty collection on error?  signaling no additional capacity
            // provisioned?
            return Collections.<NodeProvisioner.PlannedNode>emptyList();
        }
    }

    @Override
    public boolean canProvision(Label label) {
        /**
         * For simplicity, assume that NodePool labels start with a "nodepool-"
         * prefix.  We'll take the remainder of the label and assume a matching
         * label exists in NodePool's configuration.
         */
        LOGGER.log(Level.INFO, "Request to provision label " + label);

        if (label.getName().startsWith(NODEPOOL_LABEL_PREFIX)) {
            // yep, we can service that request via NodePool.
            LOGGER.log(Level.INFO, "Yes, we can provision " + label);
            return true;
        } else {
            LOGGER.log(Level.INFO, "Refusing to provision " + label);
            return false;
        }
    }
    
    @Extension
    public static final class DescriptorImpl extends Descriptor<hudson.slaves.Cloud> {
	public DescriptorImpl(){
		load();
	}
	@Override	
	public String getDisplayName(){
		return "Nodepool Cloud";
	}
	
    }
    
}