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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Convenience class for deletage. It allows resource's value to be obtained
 * as a string and resource's value to be stored as a string.
 *
 * @author Daniel Sendula
 */
public abstract class StringDelegate extends Delegate {

    /**
     * Constructor
     * @param path path
     */
    public StringDelegate(String path) {
        super(path);
        objectPath = IOUtils.parentPath(path);
    }

    /**
     * Returns value as a string
     * @param adapter adapter
     * @return value of the resource as a string
     */
    protected abstract String getValueAsString(JavaWebDAVResourceAdapter adapter);

    /**
     * Returns resource length by checking resource's value string length
     * @param adapter adapter
     * @return resource length
     */
    public int resourceLength(JavaWebDAVResourceAdapter adapter) {
        String p = getValueAsString(adapter);
        if (p != null) {
            return p.length();
        } else {
            return -1;
        }
    }

    /**
     * Returns an input stream of the value returned from the {@link #getValueAsString(JavaWebDAVResourceAdapter)}
     * method.
     * @param adapter adapter
     * @return input stream or <code>null</code>
     */
    public InputStream getInputStream(JavaWebDAVResourceAdapter adapter) {
        String value = getValueAsString(adapter);
        if (value != null) {
            return new ByteArrayInputStream(value.getBytes());
        } else {
            return null;
        }
    }

    /**
     * Returns type as &quot;text/plain&quot;
     * @param adapter
     * @return &quot;text/plain&quot;
     */
    public String getContentType(JavaWebDAVResourceAdapter adapter) {
        return "text/plain";
    }

}
