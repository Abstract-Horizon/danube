
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

import java.util.List;

/**
 * This bean represents a definition of an object or a property
 *
 * @author Daniel Sendula
 */
public class Method extends Type {

    /** Object's or property's name */
    protected String name;

    /** Object's or property's description */
    protected String desc;

    /** Object's or property's acess type */
    protected String access;

    /** Parameters. */
    protected List<Type> parameterTypes;
    
    /** Is it expert entry (displayed only to experts) or not */
    protected boolean expert;
    
    /**
     * Constructor
     */
    public Method() {
    }
    
    /**
     * Constructor
     * @param name name
     * @param desc desc
     * @param type type
     * @param access access
     * @param parameterTypes parameter types
     */
    public Method(String name, String desc, String type, String access, List<Type> parameterTypes) {
        setName(name);
        setDesc(desc);
        setType(type);
        setAccess(access);
        setParameterTypes(parameterTypes);
   }

    /**
     * Returns name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set name.
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns desc
     * @return desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc
     * @param desc desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    /**
     * Returns access
     * @return access
     */
    public String getAccess() {
        return access;
    }

    /**
     * Set access
     * @param access access 
     */
    public void setAccess(String access) {
        this.access = access;
    }
    
    /**
     * Returns parameter types
     * @return parameter types
     */
    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    /**
     * Sets parameter types
     * @return parameterTypes parameter types
     */
    public void setParameterTypes(List<Type> parameterTypes) {
        this.parameterTypes = parameterTypes;
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
        res.append("Method[");
        res.append(name).append(",");
        res.append(desc).append(",");
        res.append(type).append(",");
        res.append(shortType).append(",");
        res.append(access).append(",");
        res.append(name).append("(");
        if (parameterTypes != null) {
            boolean first = false;
            for (Type type : parameterTypes) {
                if (first) { first = false; } else { res.append(","); }
                res.append(type.getShortType());
            }
        }
        res.append("),");
        res.append(expert);
        res.append("]");
        return res.toString();
    }
}
