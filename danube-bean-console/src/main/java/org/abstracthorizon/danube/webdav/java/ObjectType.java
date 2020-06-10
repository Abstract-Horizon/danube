/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.webdav.java;

/**
 * This object defines path to the object type. It returns object's type as a
 * content of the resource (WebDAV's file)
 *
 * @author Daniel Sendula
 */
public class ObjectType extends StringDelegate {

    /**
     * Constructor
     * @param path path
     */
    public ObjectType(String path) {
        super(path);
    }

    /**
     * Returns string value of the object
     * @param adapter adapter
     * @return this object's class name
     */
    protected String getValueAsString(JavaWebDAVResourceAdapter adapter) {
        Object object = adapter.findObjectImpl(objectPath);
        if (object != null) {
            return object.getClass().getName();
        } else {
            return null;
        }
    }
}
