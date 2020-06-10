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

import org.abstracthorizon.danube.beanconsole.gwt.client.HeaderBar;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModelListener;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.Resource;

/**
 * Observer for the header summary of the detail section of the Danube Bean Console. This 
 * observer is to subscribe to changes communicated by the publisher of the domain model. The 
 * observer will once notified of any changes update the header in the details section of the 
 * Danube Bean Console.
 *
 * @author Mile Lukic
 */
public class HeaderObserver implements DataModelListener {

    private HeaderBar headerBar;
    private Resource resource;
    
    /**
     * Constructor. Sets a reference to the header bar component.
     * 
     * @param headerBar - header bar component
     * @param model - data model
     */
    public HeaderObserver(HeaderBar headerBar, DataModel model) { // IS THE "MODEL" PARAMETER USED?
        this.headerBar = headerBar;
    }

    /**
     * Updates the header bar component with the summary details for the bean being viewed.
     */
    public void modelUpdated(String url, Resource resource) {
        String icon;
        if (resource.getName().equals("Application Context")) {
            icon = "app.png";
        } else {
            icon = "beans.png";
        }
        
        boolean updated = false;
        if (!resource.isFollowable()) {
            String thisPath = "";
            if (this.resource != null) {
                thisPath = this.resource.getPath();
            }
            String newPath = resource.getPath();
            if (newPath.startsWith(thisPath)) {
                String trail = newPath.substring(thisPath.length());
                if (trail.indexOf('/', 1) < 0) {
                    updated = true;
                }
            }
        }
        
        if (!updated) {
            this.resource = resource;
            headerBar.setHeaderValues(resource.getName(), resource.getType(), resource.getPath(), resource.getValue(), icon);
        }
    }
}
