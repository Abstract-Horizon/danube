
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
public class Type {

    /** Object's or property's type */
    protected String type;

    /** Object's or property's type shortened to the {@link #MAX_TYPE_SIZE} characters */
    protected String shortType;
    
    /** Description */
    protected String desc;

    /**
     * Default constructor. 
     */
    public Type() {
    }

    /**
     * Constructor
     * @param path path
     * @param type type
     * @param shortType shortType
     */
    public Type(String type) {
        setType(type);
    }

    /**
     * Returns type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns type
     * @return type
     */
    public void setType(String type) {
        this.type = type;

        if (type.length() > Definitions.MAX_TYPE_SIZE) {
            int i = type.indexOf('.');
            int j = type.lastIndexOf('.');
            if (i+4 >= j) {
                shortType = type;
            } else {
                shortType = type.substring(0, i+4) + ".." + type.substring(j);
            }
        } else {
            shortType = type;
        }
    }

    /**
     * Returns short type
     * @return short type
     */
    public String getShortType() {
        return shortType;
    }
    
    /**
     * Returns desc
     * @return desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets description
     * @param desc description
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Returns string representation of this object.
     * @return string representation of this object
     */
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("Type[");
        res.append(type).append(",");
        res.append(shortType).append(",");
        if (desc != null) {
            res.append(desc);
        }
        res.append("]");
        return res.toString();
    }
}
