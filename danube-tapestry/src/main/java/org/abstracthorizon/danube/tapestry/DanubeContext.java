/*
 * Copyright (c) 2005-2020 Creative Sphere Limited.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   Creative Sphere - initial API and implementation
 *
 */
package org.abstracthorizon.danube.tapestry;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.FileTypeMap;

import org.apache.tapestry.describe.DescriptionReceiver;
import org.apache.tapestry.web.WebContext;

/**
 * {@link WebContext} interface implementation.
 *
 *
 * @author Daniel Sendula
 */
public class DanubeContext implements WebContext {

    /** Contexct attributes */
    protected Map<String, Object> attributes = new HashMap<String, Object>();

    /** Reference to handler */
    protected TapestryConnectionHandler handler;

    /**
     * Constructor
     * @param handler handler
     */
    public DanubeContext(TapestryConnectionHandler handler) {
        this.handler = handler;
    }

    /**
     * Returns resource as URL. It uses this classes class loader.
     * @param path resource path. If starts with &quot;/&quot; then it is removed.
     * @return resource as URL.
     */
    public URL getResource(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource(path);
        return url;
    }

    /**
     * Returns mime type of given resource path
     * @param resourcePath resource path
     * @return mime type of given resource path
     */
    public String getMimeType(String resourcePath) {
        if (resourcePath.endsWith(".js")) {
            return "text/javascript";
        }

        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }

        FileTypeMap map = FileTypeMap.getDefaultFileTypeMap();
        // URL resource = getResource(resourcePath);
        String mimeType = map.getContentType(resourcePath);
        return mimeType; // TODO
    }

    /**
     * Returns attribute names
     * @return attribute names as a list
     */
    public List<String> getAttributeNames() {
        return new ArrayList<String>(attributes.keySet());
    }

    /**
     * Returns attribute
     * @param name attribute name
     * @return attribute
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * Sets attribute
     * @param name attribte name
     * @param attribute attribute value
     */
    public void setAttribute(String name, Object attribute) {
        attributes.put(name, attribute);
    }

    /**
     * Returns initial parameters names.
     * @return list of initial parameter names
     */
    public List<String> getInitParameterNames() {
        ArrayList<String> names = new ArrayList<String>(handler.getInitialParameters().keySet());
        return names;
    }

    /**
     * Gets initial parameter's value
     * @param name attribute name
     * @return initial parameter's value
     */
    public String getInitParameterValue(String name) {
        return handler.getInitialParameters().get(name);
    }

    /**
     * Does nothing
     * @param receiver receiver
     */
    public void describeTo(DescriptionReceiver receiver) {
    }

}
