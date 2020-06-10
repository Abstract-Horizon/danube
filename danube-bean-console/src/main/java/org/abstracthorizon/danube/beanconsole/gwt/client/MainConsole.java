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

import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;
import org.abstracthorizon.danube.beanconsole.gwt.client.observer.DetailTabObserver;
import org.abstracthorizon.danube.beanconsole.gwt.client.observer.HeaderObserver;
import org.abstracthorizon.danube.beanconsole.gwt.client.observer.TreeObserver;

import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Builds the main sections of the Danube Bean Console. Creates the observers and subscribes
 * them to the domain model publisher.
 *
 * @author Mile Lukic
 */
public class MainConsole {
    private VLayout mainLayout;
    final StatusBar statusBar = new StatusBar();
    private DataModel model;
    
    /**
     * Constructor. Sets this components copy of the data model and initiates construction of the
     * sections contained in the Danube Bean Console.
     * 
     * @param model - a copy of the current domain model.
     */
    public MainConsole(DataModel model) {
        this.model = model;
        constructConsole();
    }
    
    /**
     * Performs the actual construction of the constituent components contained in the
     * Danube Bean Console. This method also creates the observers for each section and
     * subscribes them to the publisher which communicates any changes that may occur to 
     * the domain model.
     */
    private void constructConsole() {       
        //create the main console layout
        mainLayout = new VLayout();
        mainLayout.setPadding(3);
        mainLayout.setMargin(2);
        mainLayout.setWidth100();
        mainLayout.setHeight("*");

        //create the header bar
        final HeaderBar headerBar = new HeaderBar();
//      final DetailContainer details = new DetailContainer();
        final DetailTabContainer details = new DetailTabContainer(model);
        
        //create the layouts for the content part of the console
        HLayout contentLayout = new HLayout();
        VLayout navigatorLayout = new VLayout();
        navigatorLayout.setShowResizeBar(true);
        navigatorLayout.setWidth("30%");
        final VLayout dataLayout = new VLayout();
        dataLayout.setWidth100();
        dataLayout.addMember(headerBar.getHeaderBar());
        
        contentLayout.addMember(navigatorLayout);
        contentLayout.addMember(dataLayout);

        NavigatorTree navigatorTree = new NavigatorTree();
        
        TreeObserver treeObserver = new TreeObserver(navigatorTree, model);
        model.addModelListener(treeObserver);
        HeaderObserver hdrObserver = new HeaderObserver(headerBar, model);
        model.addModelListener(hdrObserver);
//     DetailObserver detailObserver = new DetailObserver(details);
 //     dataModel.attach(detailObserver);

        DetailTabObserver detailTabObserver = new DetailTabObserver(details, model);
        model.addModelListener(detailTabObserver);

        dataLayout.addMember(details.getDetails());
        
        navigatorTree.getRoot().setName("Application Context");
        navigatorTree.getRoot().setAttribute("icon", "app.png");
        navigatorTree.getRoot().setAttribute("link", "/");
        navigatorLayout.addMember(navigatorTree.getTreeGrid());
        
        //add content to the main layout
        mainLayout.addMember(contentLayout);
    }

    /**
     * Get the main console component containing the tree navigator and the details section 
     * containing the bean summary header and TabSet. 
     * 
     * @return mainLayout - the Layout component containing the navigator tree and details section
     */
    public VLayout getConsole() {
        return mainLayout;
    }

    /**
     * Get the status bar section of the console.
     * 
     * @return HLayout - the Layout component containing the status bar.
     */
    public HLayout getStatusBar() {
        return statusBar.getStatusBar();
    }
}
