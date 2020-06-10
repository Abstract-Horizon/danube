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
package org.abstracthorizon.danube.beanconsole.gwt.client.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONObject;

/**
 * 
 * 
 * @author Mile Lukic
 * @author Daniel Sendula
 */
public class DataModel {

//    private LinkedHashMap<String, Resource> resources = new LinkedHashMap<String, Resource>();
    private List<DataModelListener> listeners = new ArrayList<DataModelListener>();

    private Resource selectedResource;
    
    public DataModel() {
    }

    public void addModelListener(DataModelListener modelListener) {
        listeners.add(modelListener);
    }

    public void removeModelListener(DataModelListener modelListener) {
        listeners.remove(modelListener);
    }
    
    protected void fireChange(String url, Resource resource) {
        for (DataModelListener modelListener : listeners) {
            modelListener.modelUpdated(url, resource);
        }
    }
    
    public void addResource(JSONObject jObj, String url) {
        
        
        Resource resource = new Resource(jObj, url);
        
        selectedResource = resource;
        fireChange(url, resource);
    }

    public Resource getSelectedResource() {
        return selectedResource;
    }
}
