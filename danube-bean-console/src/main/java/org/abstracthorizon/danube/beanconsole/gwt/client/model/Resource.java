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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.smartgwt.client.util.SC;

/**
 * 
 *
 * @author Mile Lukic
 */
public class Resource {
    
    private String name;
    private String path;
    private String type;
    private String url;
    private String value;
    private boolean followable = true;
    private JSONObject jsonObj;
	
    public Resource(JSONObject jsonObj, String url) {
        this.jsonObj = jsonObj;
        this.url = url;
        path = jsonObj.get("path").isString().stringValue();
        
        if (!path.equals("/")) {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length());
            }
        }
//        if (path.startsWith("/")) {
//            path = path.substring(1);
//        }
        
        name = jsonObj.get("name").isString().stringValue();
        type = jsonObj.get("type").isString().stringValue();
        value = jsonObj.get("value").isString().stringValue();

        if (jsonObj.containsKey("followable")) {
            followable = jsonObj.get("followable").isBoolean().booleanValue();
            SC.logWarn("Resource has followable = " + followable);
        } else {
            SC.logWarn("Resource doesn't have followable = " + followable);
            followable = true;
        }

        
        //change name to "Application Context" if this is the root resource
        if (jsonObj.get("name").isString().stringValue().equals("&lt;root&gt;")) {
            name = "Application Context";
        }
    }

    public String getParent() {
        return url;
    }
	
    public String getName() {
        return name;
    }
	
    public String getPath() {
        return path;
    }
    
    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    
    public boolean isFollowable() {
        return followable;
    }

    public JSONArray getElements(String type) {
        
        JSONValue obj = jsonObj.get(type);
        if (obj == null) {
            return null;
        }
        JSONArray elements = obj.isArray();
        
        return elements;
    }
    

}
