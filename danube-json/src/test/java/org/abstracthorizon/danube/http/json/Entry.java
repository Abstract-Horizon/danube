
/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
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

/**
 * This bean represents a definition of an object or a property
 *
 * @author Daniel Sendula
 */
public class Entry extends Type {

    /** Object's or property's name */
    protected String path;

    /** Object's or property's name */
    protected String name;

    /** Object's or property's acess type */
    protected String access;

    /** Object's or property's value as an string */
    protected String value;

    /** Object's or property's value as an string shortened to the max {@link #MAX_VALUE_SIZE} characters */
    protected String shortValue;

    /** Does the object or property contain the reference that can be followed */
    protected boolean followable;
    
    /** Is it expert entry (displayed only to experts) or not */
    protected boolean expert;
    
    /**
     * Default constructor.
     */
    public Entry() {
    }

    /**
     * Constructor
     * @param name name
     * @param type type
     * @param access access
     * @param value value
     * @param followable is followable
     */
    public Entry(String name, String desc, String type, String access, String value, boolean followable) {
        setName(name);
        setDesc(desc);
        setType(type);
        setAccess(access);
        setValue(value);
        setFollowable(followable);
    }

    /**
     * Returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns path
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets path
     * @param path path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Returns access
     * @return access
     */
    public String getAccess() {
        return access;
    }

    /**
     * Sets access.
     * @param access access
     */
    public void setAccess(String access) {
        this.access = access;
    }
    
    /**
     * Returns value
     * @return value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Returns value
     * @return value
     */
    public String getEscapedValue() {
        if (value != null) {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if ((c == '\\') || (c == '"')) {
                    res.append('\\');
                    res.append(c);
                } else if (c == '\n') {
                    res.append("\\n");
                } else if (c == '\r') {
                    res.append("\\r");
                } else if (c == '\t') {
                    res.append("\\t");
                } else if (c == '\b') {
                    res.append("\\b");
                } else if (c == '\f') {
                    res.append("\\f");
                } else if (c < ' ') {
                    res.append("\\u");
                    int h = c;
                    String hs = Integer.toHexString(h);
                    int j = hs.length();
                    while (j < 4) {
                        res.append('0');
                        j++;
                    }
                    res.append(hs);
                } else {
                    res.append(c);
                }
                    
            }
            
            return res.toString();
        }
        return value;
    }
    
    /**
     * Sets value
     * @param value value
     */
    public void setValue(String value) {
        this.value = value;
        if (value.length() > Definitions.MAX_VALUE_SIZE) {
            shortValue = value.substring(0, Definitions.MAX_VALUE_SIZE) + "...";
        } else {
            shortValue = value;
        }
    }

    /**
     * Returns short value
     * @return short value
     */
    public String getShortValue() {
        return shortValue;
    }

    /**
     * Returns followable flag
     * @return followable flag
     */
    public boolean isFollowable() {
        return followable;
    }

    /**
     * Sets followable
     * @param followable
     */
    public void setFollowable(boolean followable) {
        this.followable = followable;
    }
    
    /**
     * Returns followable flag
     * @return followable flag
     */
    public String getFollowableAsString() {
        return Boolean.toString(followable);
    }

    /**
     * Returns expert flag
     * @return expert flag
     */
    public boolean isExpert() {
        return expert;
    }

    /**
     * Sets expert
     * @param expert expert
     */
    public void setExpert(boolean expert) {
        this.expert = expert;
    }
    
    /**
     * Returns expert flag
     * @return expert flag
     */
    public boolean getExpert() {
        return expert;
    }

    /**
     * Returns string representation of this object.
     * @return string representation of this object
     */
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Entry[");
        res.append(path).append(",");
        res.append(name).append(",");
        res.append(desc).append(",");
        res.append(type).append(",");
        res.append(shortType).append(",");
        res.append(access).append(",");
        res.append(value).append(",");
        res.append(shortValue).append(",");
        res.append(followable).append(",");
        res.append(expert);
        res.append("]");
        return res.toString();
    }
}
