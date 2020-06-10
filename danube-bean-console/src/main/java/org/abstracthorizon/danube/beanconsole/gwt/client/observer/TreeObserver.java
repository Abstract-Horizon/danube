/*
 * Copyright (c) 2009 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Criterion Design Concepts  - initial API and implementation
 *   
 */
package org.abstracthorizon.danube.beanconsole.gwt.client.observer;

import org.abstracthorizon.danube.beanconsole.gwt.client.ClientServerTransaction;
import org.abstracthorizon.danube.beanconsole.gwt.client.NavigatorTree;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModelListener;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.Resource;
import org.abstracthorizon.danube.beanconsole.gwt.client.util.Utils;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.CellClickEvent;
import com.smartgwt.client.widgets.grid.events.CellClickHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Observer for the tree navigator of the Danube Bean Console. This observer is to subscribe 
 * to changes communicated by the publisher of the domain model. The observer will once notified
 * of any changes update the header in the details section of the Danube Bean Console.
 *
 * @author Mile Lukic
 * @author Daniel Sendula
 */
public class TreeObserver implements DataModelListener {

    private NavigatorTree navigatorTree;

    /**
     * Updates the view with new data from the data model. A Click Handler is added to each node.
     * 
     * @param navigatorTree
     * @param model
     */
    public TreeObserver(NavigatorTree navigatorTree, final DataModel model) {
        this.navigatorTree = navigatorTree;
        TreeGrid tGrid = navigatorTree.getTreeGrid();

        tGrid.addCellClickHandler(new CellClickHandler() {
            public void onCellClick(CellClickEvent event) {
                ListGridRecord record = event.getRecord();
                //We only want to process a click event if the tree node contains an item that is "followable".
                String url = record.getAttribute("link");

                ClientServerTransaction transaction = new ClientServerTransaction(url, model);
                transaction.sendGetRequest();
            }

        });
    }

    /**
     * Updates the tree with the updated model.
     */
    public void modelUpdated(String url, Resource resource) {
        if (resource.isFollowable() || resource.getPath().startsWith("s_")) {
            
            TreeNode [] nodes = {};
    
            TreeNode node = navigatorTree.getNode(url);
            if (node == null) {
                node = navigatorTree.getRoot();
                if (!url.equals("/")) {
                    node = buildPath(node, "/", url);
                }
            }
    
            navigatorTree.removeNodes(node);
            
            createNodes(resource, nodes, "beans", "b_");
    
            JSONArray elements = resource.getElements("collection");
            if (elements != null) {
                for (int i = 0; i < elements.size(); i++) {
                    TreeNode n = new TreeNode();
                    
                    String name = Integer.toString(i);
                    String link = Utils.concatenatePaths(resource.getPath(), "i_" + name);
        
                    n.setName(name);
                    n.setAttribute("icon", "collection.png");
                    n.setAttribute("group", "collection");
                    n.setAttribute("link", link);
        
                    nodes[nodes.length] = n;
                }
            }
    
            createNodes(resource, nodes, "map", "k_");
    
            createNodes(resource, nodes, "properties", "p_");
    
            createNodes(resource, nodes, "savedBeans", "s_");
    
            navigatorTree.addNodeList(nodes, node);     
        }
    }

    /**
     * Creates nodes for the tree navigator.
     * 
     * @param resource
     * @param nodes
     * @param type
     * @param prefix
     */
    private void createNodes(Resource resource, TreeNode [] nodes, String type, String prefix) {

        JSONArray elements = resource.getElements(type);
        if (elements != null) {
            for (int i = 0; i < elements.size(); i++) {
                JSONObject element = elements.get(i).isObject();
    
                boolean followable = element.get("followable").isBoolean().booleanValue();
                if (followable) {
                    
                    TreeNode n = new TreeNode();
                    
                    String name = element.get("name").isString().stringValue();
                    n.setName(name);
                    n.setAttribute("icon", type + ".png");
                    n.setAttribute("group", type);
                    n.setAttribute("link", Utils.concatenatePaths(resource.getPath(), prefix + name));
        
                    nodes[nodes.length] = n;
                }
            }
        }        
    }
    
    private TreeNode buildPath(TreeNode parent, String parentUrl, String url) {
        int i = url.indexOf('/', parentUrl.length());
        
        // SC.logWarn("BuildPath: " + parentUrl + "  " + url + "   " + i);
        
        boolean last = true;
        String segment;
        if (i >= 0) {
            segment = url.substring(parentUrl.length(), i);
            // SC.logWarn("BuildPath: segment=" + segment + "  last=" + false);
            last = false;
        } else {
            segment = url.substring(parentUrl.length());
            // SC.logWarn("BuildPath: segment=" + segment + "  last=" + true);
        }

        TreeNode n = navigatorTree.getNode(parentUrl + segment);
        if (n == null) {
            n = new TreeNode();
            
            String type = Utils.typeFromPrefix(segment);
            // SC.logWarn("BuildPath: type=" + type);
            
            n.setName(segment.substring(2));
            n.setAttribute("icon", type + ".png");
            n.setAttribute("group", type);
            n.setAttribute("link", parentUrl + segment);
    
            navigatorTree.addNode(n, parent);
        }        
        parentUrl = url.substring(0 , i + 1);
        if (last) {
            return n;
        } else {
            return buildPath(n, parentUrl, url);
        }
    }
    
}
