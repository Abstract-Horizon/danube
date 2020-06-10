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
package org.abstracthorizon.danube.beanconsole.gwt.client;

import java.util.HashMap;
import java.util.Map;
import com.smartgwt.client.data.XJSONDataSource;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * This class represents the tree navigator component in the Danube Bean Console. It constructs
 * the component containing the tree and provide many methods to access various details from the
 * tree.
 * 
 * @author Mile Lukic
 */
public class NavigatorTree {
    private TreeGrid tGrid;
    private Tree dataTree;
    private Map<String, TreeNode> nodes = new HashMap<String, TreeNode>();

    /**
     * Constructor. Constructs the tree navigator component and customises its appearence.
     */
    public NavigatorTree() {
        tGrid = new TreeGrid() {
            /* Overrides the getCellCSSText method in order to allow "followable" items in the 
             * tree to be displayed in a blue colour. NOTE: currently ALL tree nodes are 
             * followable, since no non-followable items are included in the tree. */
            protected String getCellCSSText(ListGridRecord record, int rowNum, int colNum) {
                String followable = record.getAttribute("followable");
                if (record.getAttribute("group").equals("methods") || followable.equals(null) || followable.equals("false") || !(getFieldName(colNum).equals("name"))) {
                    return super.getCellCSSText(record, rowNum, colNum);
                } else {
                    return "color:blue;";
                }
            }
        };
        //customise tree appearence
        tGrid.setTop(120);
        tGrid.setWidth100();
        tGrid.setHeight100();
        tGrid.setWrapCells(true);
        tGrid.setFixedRecordHeights(false);
        tGrid.setShowAllRecords(true);
        tGrid.setAlternateRecordStyles(true);
        tGrid.setShowConnectors(true);
        tGrid.setShowOpenIcons(false);
        tGrid.setIconSize(1);
        tGrid.setCellPadding(0);
        
        dataTree = new Tree();
        dataTree.setShowRoot(true);
        //create TreeGRidFields and add them to the tree component.
        TreeGridField nameField = new TreeGridField("name", "Navigator");
        tGrid.setFields(nameField);
        tGrid.setShowRoot(true);
        tGrid.setData(dataTree);
        
        // TODO
        // This is not right - we need to control rendering of icon somewhere else!
        nameField.setCellFormatter(new CellFormatter() {
            public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
                String link = record.getAttribute("link");
                // String name = record.getAttribute("name");
                String icon = record.getAttribute("icon");
                
                return "<table border=\"0\"><tr><td valign=\"center\"><a href=\"" + link + "\"><img src=images/" + icon + " width=\"16\" height=\"16\" border=\"0\" /></a></td><td>" + value + "</td></tr></table>";
                // return "<span style=\"float:left;width:32px;height:100%;vertical-align:middle;\"><a href=\"" + link + "\"><img src=images/" + icon + " width=\"16\" height=\"16\" border=\"0\" /></a></span>" + value;
            }
        });
    }

    /**
     * Adds the tree node provided in the parameters to the branch in the tree specified by the
     * parentNode parameter.
     * 
     * @param node - node to be added to the tree.
     * @param parentNode - parent of the node being added.
     */
    public void addNode(TreeNode node, TreeNode parentNode) {
        dataTree.add(node, parentNode);
        String url = node.getAttribute("link");
        this.nodes.put(url, node);
    }

    /**
     * This method takes the array of tree node objects provided in the parameters and adds all 
     * of them to the parent node provided. 
     * 
     * @param nodes - array of TreeNode objects to add to the tree.
     * @param parentNode - parent node to which all the nodes in the array are added.
     */
    public void addNodeList(TreeNode[] nodes, TreeNode parentNode) {
        dataTree.addList(nodes, parentNode);
        if (nodes.length > 0) {
            dataTree.openFolder(parentNode);
            for (TreeNode node : nodes) {
                String url = node.getAttribute("link");
                this.nodes.put(url, node);
            }
        }
    }

    /**
     * Remove all the nodes from the parent node specified in the parameter.
     * 
     * @param parentNode - node from which all child nodes are to be removed.
     */
    public void removeNodes(TreeNode parentNode) {
        TreeNode[] nodes = dataTree.getChildren(parentNode);
        dataTree.removeList(nodes);
    }

    /**
     * Gets the root of the tree.
     * 
     * @return TreeNode - the node representing the root of the tree.
     */
    public TreeNode getRoot() {
        return dataTree.getRoot();
    }
    
    /**
     * Gets the node identified by the URL in the parameter.
     * 
     * @param url - the URL identifying a node in the tree.
     * @return TreeNode - the tree node corresponding to the URL in the parameter.
     */
    public TreeNode getNode(String url) {
        return nodes.get(url);
    }

    /**
     * Get the tree grid component.
     * 
     * @return tGrid - TreeGrid component containing the tree.
     */
    public TreeGrid getTreeGrid() {
        return tGrid;
    }

    /**
     * Get The tree.
     * 
     * @return Tree - the tree component in the TreeGrid.
     */
    public Tree getTree() {
        return dataTree;
    }

    /**
     * Clears the tree of all data.
     */
    public void clearTree() {
        dataTree.removeList(dataTree.getChildren(dataTree.getRoot()));
    }

    /**
     * Checks whether the node provided in the parameters has any child nodes.
     * 
     * @param parentNode - the node to be examined to see if it has any child nodes. 
     * @return hasChildren - boolean: "true" if there are children, "false" if not.
     */
    public boolean hasChildren(TreeNode parentNode) {
        boolean hasChildren = true;
        if (dataTree.getChildren(parentNode).equals(null)) {
            hasChildren = false;
        }
        return hasChildren;
    }

    /**
     * Redraw the tree component.
     */
    public void redrawTree() {
        tGrid.redraw();
    }

    /**
     * Sets the datasource for the tree component.
     * 
     * @param dbcData - data source for the tree.
     */
    public void setDataSource(XJSONDataSource dbcData) {
        tGrid.setDataSource(dbcData);
    }
}
