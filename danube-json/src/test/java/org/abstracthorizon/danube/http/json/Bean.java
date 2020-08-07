
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
package org.abstracthorizon.danube.http.json;

import java.util.ArrayList;
import java.util.List;

/**
 * This bean represents a definition of an object or a property
 *
 * @author Daniel Sendula
 */
public class Bean extends Entry {

    /** Contains list of beans. */
    protected List<Entry> beans;

    /** Contains list of saved beans in session. */
    protected List<Entry> savedBeans;
    
    /**Contains list of map entries. Name is key (if it is string) or toString() method call on key. */
    protected List<Entry> map;
    
    /** Contains list of collection entries. Entries in this way have no name assigned to them. */
    protected List<Entry> collection;
    
    /** Contains list of bean properties entries. */
    protected List<Entry> properties = new ArrayList<Entry>();

    /** List of methods. */
    protected List<Method> methods = new ArrayList<Method>();
    
    /**
     * Default constructor. 
     */
    public Bean() {
    }

    /**
     * @return the beans
     */
    public List<Entry> getBeans() {
        return beans;
    }

    /**
     * @param beans the beans to set
     */
    public void setBeans(List<Entry> beans) {
        this.beans = beans;
    }

    /**
     * @return the beans
     */
    public List<Entry> getSavedBeans() {
        return savedBeans;
    }

    /**
     * @param beans the beans to set
     */
    public void setSavedBeans(List<Entry> savedBeans) {
        this.savedBeans = savedBeans;
    }

    /**
     * @return the map
     */
    public List<Entry> getMap() {
        return map;
    }

    /**
     * @param map the map to set
     */
    public void setMap(List<Entry> map) {
        this.map = map;
    }

    /**
     * @return the collection
     */
    public List<Entry> getCollection() {
        return collection;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(List<Entry> collection) {
        this.collection = collection;
    }

    /**
     * @return the properties
     */
    public List<Entry> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(List<Entry> properties) {
        this.properties = properties;
    }

    /**
     * @return the methods
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * @param methods the methods to set
     */
    public void setMethods(List<Method> methods) {
        this.methods = methods;
    }

    /**
     * Returns string representation of this object.
     * @return string representation of this object
     */
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Bean[");
        res.append(path).append(",");
        res.append(type).append(",");
        res.append(shortType);
        if (beans != null) {
            res.append(",");
            res.append("beans[");
            boolean first = false;
            for (Entry entry : beans) {
                if (first) { first = false; } else { res.append(","); }
                res.append(entry.getName());
            }
            res.append("]");
        }
        if (savedBeans != null) {
            res.append(",");
            res.append("savedBeans[");
            boolean first = false;
            for (Entry entry : beans) {
                if (first) { first = false; } else { res.append(","); }
                res.append(entry.getName());
            }
            res.append("]");
        }
        if (map != null) {
            res.append(",");
            res.append("map[");
            boolean first = false;
            for (Entry entry : map) {
                if (first) { first = false; } else { res.append(","); }
                res.append(entry.getName());
                res.append("=").append(entry.getShortValue());
            }
            res.append("]");
        }
        if (collection != null) {
            res.append(",");
            res.append("collection[");
            boolean first = false;
            for (Entry entry : collection) {
                if (first) { first = false; } else { res.append(","); }
                res.append(entry.getShortValue());
            }
            res.append("]");
        }
        if (properties != null) {
            res.append(",");
            res.append("properties[");
            boolean first = false;
            for (Entry entry : properties) {
                if (first) { first = false; } else { res.append(","); }
                res.append(entry.getName());
            }
            res.append("]");
        }
        if (properties != null) {
            res.append(",");
            res.append("methods[");
            boolean first = false;
            for (Method method : methods) {
                if (first) { first = false; } else { res.append(","); }
                res.append(method.getName());
                List<Type> parameterTypes = method.getParameterTypes();
                res.append("(");
                if (parameterTypes != null) {
                    boolean f = false;
                    for (Type type : parameterTypes) {
                        if (f) { f = false; } else { res.append(","); }
                        res.append(type.getShortType());
                    }
                }
                res.append(")");
            }
            res.append("]");
        }
        
        res.append("]");
        return res.toString();
    }
}
