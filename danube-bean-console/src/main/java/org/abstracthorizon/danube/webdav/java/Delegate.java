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

import org.abstracthorizon.danube.http.util.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class adds several methods to the bean (path) object. Adapter is, then,
 * using these methods to obtain various attributes of the object.
 *
 * @author Daniel Sendula
 */
public abstract class Delegate extends Bean {

    /** Parent object path - parent portion of the supplied path */
    protected String objectPath;

    /**
     * Constructor
     * @param path path to the object
     */
    public Delegate(String path) {
        super(path);
        objectPath = IOUtils.parentPath(path);
    }

    /**
     * Returns resource length
     * @param adapter adapter it is called from
     * @return resource length or -1 if unknown.
     */
    public abstract int resourceLength(JavaWebDAVResourceAdapter adapter);

    /**
     * Returns etag of the object. If not overriden then parent's object
     * identity hash + class name is returned as a weak etag (see RFC-2616)
     * @param adapter adapter
     * @return an etag
     */
    public String getResourceETag(JavaWebDAVResourceAdapter adapter) {
        Object object = adapter.findObjectImpl(objectPath);
        if (object != null) {
            String eTag = "W/\"" + Integer.toHexString(System.identityHashCode(object)) + "-" + getClass().getName() + "\"";
            return eTag;
        } else {
            String path = getPath();
            return "W/\"" + path + "-NULL\"";
        }
    }

    /**
     * Returns input stream of the object/field's value. This implementation returns <code>null</code>
     * @param adapter adapter
     * @return input stream or <code>null</code>
     */
    public InputStream getInputStream(JavaWebDAVResourceAdapter adapter) {
        return null;
    }

    /**
     * Returns input stream of a given range of the object/field's value. This implementation returns <code>null</code>
     * @param adapter adapter
     * @param from offset
     * @param length length
     * @return input stream or <code>null</code>
     */
    public InputStream getInputStream(JavaWebDAVResourceAdapter adapter, long from, long length) {
        InputStream is = getInputStream(adapter);
        if (is == null) {
            return null;
        }
        return null;
    }

    /**
     * Returns output stream of the object/field's value. It is used to write into the object/field.
     * This implementation returns <code>null</code>
     * @param adapter adapter
     * @return output stream or <code>null</code>
     */
    public OutputStream getOutputStream(JavaWebDAVResourceAdapter adapter) {
        return null;
    }

    /**
     * Returns output stream of the object/field's value. It is used to write into the object/field.
     * This implementation returns <code>null</code>
     * @param adapter adapter
     * @param from offset
     * @param length length
     * @return output stream or <code>null</code>
     */
    public OutputStream getOutputStream(JavaWebDAVResourceAdapter adapter, long from, long length) {
        OutputStream out = getOutputStream(adapter);
        if (out == null) {
            return null;
        }
        return null;
    }

    /**
     * Returns content type. This implementation returns &quot;unknown/unknown&quot;.
     * @param adapter adapter
     * @return content type
     */
    public String getContentType(JavaWebDAVResourceAdapter adapter) {
        return "unknown/unknown";
    }

}
