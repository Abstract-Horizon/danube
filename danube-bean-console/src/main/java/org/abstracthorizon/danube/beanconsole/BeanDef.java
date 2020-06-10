
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
package org.abstracthorizon.danube.beanconsole;

/**
 * This bean represents a definition of an object or a property
 *
 * @author Daniel Sendula
 */
public class BeanDef {

    /** Constant describing no access */
    public static final String NO_ACCESS = "-";

    /** Constant describing read only access through bean info */
    public static final String RO = "RO";

    /** Constant describing read/write access through bean info */
    public static final String RW = "RW";

    /** Constant describing write only access through bean info */
    public static final String WO = "WO";

    /** Constant describing read/write access through reflection (no property editors for the type) */
    public static final String RW_RAW = "rw";

    /** Constant describing read only access through reflection (no property editors for the type) */
    public static final String RO_RAW = "ro";

    /** Constant describing write only access through reflection (no property editors for the type) */
    public static final String WO_RAW = "wo";

    /** Max value size */
    public static final int MAX_VALUE_SIZE = 70;

    /** Max type size */
    public static final int MAX_TYPE_SIZE = 16;

    /** Object's or property's name */
    protected String name;

    /** Object's or property's description */
    protected String desc;

    /** Object's or property's type */
    protected String type;

    /** Object's or property's type shortened to the {@link #MAX_TYPE_SIZE} characters */
    protected String shortType;

    /** Object's or property's acess type */
    protected String access;

    /** Object's or property's value as an string */
    protected String value;

    /** Object's or property's value as an string shortened to the max {@link #MAX_VALUE_SIZE} characters */
    protected String shortValue;

    /** Does the object or property contain the reference that can be followed */
    protected boolean followable;

    /**
     * Constructor
     * @param name name
     * @param type type
     * @param access access
     * @param value value
     * @param followable is followable
     */
    public BeanDef(String name, String desc, String type, String access, String value, boolean followable) {
        this.name = name;
        this.desc = desc;
        this.type = type;
        this.access = access;
        this.value = value;
        this.followable = followable;

        if (value.length() > MAX_VALUE_SIZE) {
            shortValue = value.substring(0, MAX_VALUE_SIZE) + "...";
        } else {
            shortValue = value;
        }

        if (type.length() > MAX_TYPE_SIZE) {
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
     * Returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns desc
     * @return desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Returns type
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns short type
     * @return short type
     */
    public String getShortType() {
        return shortType;
    }

    /**
     * Returns access
     * @return access
     */
    public String getAccess() {
        return access;
    }

    /**
     * Returns value
     * @return value
     */
    public String getValue() {
        return value;
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
     * Returns followable flag
     * @return followable flag
     */
    public boolean getFollowable() {
        return followable;
    }
}
