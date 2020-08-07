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
package org.abstracthorizon.danube.webdav.java.properties;

import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.java.JavaWebDAVResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

/**
 * Returns content type of the object as defined in java resource adapter
 *
 * @author Daniel Sendula
 */
public class GetContentType extends org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetContentType {

    /**
     * Constuctor
     * @param parent parser handler
     */
    public GetContentType(XMLParserHandler parent) {
        super(parent);
    }

    /**
     * Returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentType}
     * using adapter's @link {@link JavaWebDAVResourceAdapter#getContentType(Object)}
     * @param adapter adapter
     * @return {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentType}
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        String contentType = ((JavaWebDAVResourceAdapter)adapter).getContentType(resource);
        return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentType(contentType);
    }
}

