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

import org.abstracthorizon.danube.beanconsole.gwt.client.DetailTabContainer;
import org.abstracthorizon.danube.beanconsole.gwt.client.StatusBar;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModel;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.DataModelListener;
import org.abstracthorizon.danube.beanconsole.gwt.client.model.Resource;
import org.abstracthorizon.danube.beanconsole.gwt.client.util.Utils;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * Observer for the details section of the Danube Bean Console. This observer is to subscribe
 * to changes communicated by the publisher of the domain model. The observer will once notified
 * of any changes update the details section of the Danube Bean Console.
 * 
 * @author Mile Lukic
 * @author Daniel Sendula
 */
public class DetailTabObserver implements DataModelListener {
    protected DataModel model;
    private DetailTabContainer details;

    /**
     * Constructor. Sets this observers copy of the view it will update, and a copy of the current
     * domain model.
     * 
     * @param detailTabContainer - the view component to be updated by this observer
     * @param model - domain model whose changes this observer subscribes to.
     */
    public DetailTabObserver(DetailTabContainer detailTabContainer, DataModel model) {
        this.details = detailTabContainer;
        this.model = model;
    }

    /**
     * Updates the view with new data from the data model. Old data is removed completely and 
     * replaced with data from the updated model. A Click Handler is added to each record
     * that is "followable"
     */
    public void modelUpdated(String url, Resource resource) {
        details.setResource(resource, getListGridRecords(resource));
    }

    /**
     * Prepares the records to be displayed on the grids in the TabSet.
     * 
     * @param records
     * @param resource
     * @param type
     * @param prefix
     */
    private void createRecords(ListGridRecord[] records, Resource resource, String type, String prefix) {
        JSONArray elements = resource.getElements(type);
        if (elements != null) {
            StatusBar.setStatus("Status: Received response - added " + elements.size() + " " + type);
            
            for (int i = 0; i < elements.size(); i++) {
                JSONValue value = elements.get(i);
                JSONObject b = value.isObject();

                ListGridRecord record = new ListGridRecord();

                String name = null; 
                
                if(!(type.equals("collection"))) {
                    name = b.get("name").isString().stringValue();
                } else {
                    /* Even though collections do not have a name attribute a dummy name is required to 
                     * give each collection entry a unique ID*/ 
                    name = Integer.toString(i);
                }
                record.setAttribute("name", name);
                record.setAttribute("icon", type + ".png");
                record.setAttribute("group", type);
                if (record.getAttribute("name").equals("toString")) {
                    record.setAttribute("name", "toString ");
                }

                if (type.equals("methods")) {
                    JSONValue parameterTypes = b.get("parameterTypes");
                    if (parameterTypes != null) {
                        record.setAttribute("parameterTypes", parseParameterTypes(parameterTypes.isArray()));
                    } else {
                        record.setAttribute("parameterTypes", "");
                    }
                } else {
                    record.setAttribute("type", b.get("type").isString().stringValue());
                    record.setAttribute("value", b.get("value").isString().stringValue());
                    record.setAttribute("followable", Boolean.toString(b.get("followable").isBoolean().booleanValue()));
                }

                String link = Utils.concatenatePaths(resource.getPath(), prefix + name);
                record.setAttribute("link", link);
                record.setLinkText(link);
                if (!type.equals("methods")) {
                    record.setAttribute("access", b.get("access").isString().stringValue());
                }
//                if (type.equals("beans") || type.equals("properties")) {
//                    record.setAttribute("access", b.get("access").isString().stringValue());
//                } else {
//                    record.setAttribute("access", "rw");
//                }
                records[records.length] = record;
            }
        }
    }

    /**
     * Prepare and return the array of ListGridRecords for each of the types to be displayed
     * in the Tabset. 
     * 
     * @param resource
     * @return
     */
    public ListGridRecord[] getListGridRecords(Resource resource) {
        ListGridRecord[] records = {};

        createRecords(records, resource, "beans", "b_");
        createRecords(records, resource, "collection", "i_");
        createRecords(records, resource, "map", "k_");
        createRecords(records, resource, "properties", "p_");
        createRecords(records, resource, "methods", "m_");

        return records;
    }

    /**
     * Parse the parameter types for the methods.
     * 
     * @param array
     * @return
     */
    protected String parseParameterTypes(JSONArray array) {
        String res = "";
        boolean first = true;
        if (array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                if (first) { first = false; } else { res = res + ","; }
                
                JSONValue value = array.get(i);
                JSONObject param = value.isObject();

                String type = param.get("type").isString().stringValue();
                res = res + type;
            }
        }
        return res;
    }
}
