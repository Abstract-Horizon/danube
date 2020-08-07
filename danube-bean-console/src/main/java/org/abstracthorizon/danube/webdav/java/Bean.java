/*
 * Copyright (c) 2006-2020 Creative Sphere Limited.
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
 * This class represents a path to the bean
 *
 * @author Daniel Sendula
 */
public class Bean {

    /** Path from the root object */
    protected String path;

    /**
     * Constructor
     * @param path a path from the root object
     */
    public Bean(String path) {
        this.path = path;
    }

    /**
     * Returns path
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns path string's hash code
     * @return path string's hash code
     */
    public int hashCode() {
        return path.hashCode();
    }

    /**
     * Compares path's of two objects
     * @param object other object
     * @return <code>true</code> if two paths are equal
     */
    public boolean equals(Object object) {
        if (object instanceof Bean) {
            Bean other = (Bean)object;
            if (path == other.path) {
                return true;
            } else {
                return path.equals(other.path);
            }
        } else {
            return false;
        }
    }
}
