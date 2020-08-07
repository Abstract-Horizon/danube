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
package org.abstracthorizon.danube.webdav.xml.dav.request.properties;

import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import org.xml.sax.SAXException;

/**
 * This class models WebDAV's getcontentlength tag
 *
 * @author Daniel Sendula
 */
public class GetContentLength extends RequestProperty {

    /** Content length */
    protected long contentLength = -1;

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public GetContentLength(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        try {
            contentLength = Long.parseLong(value);
        } catch (NumberFormatException e) {
            contentLength = -1;
        }
        return super.end(current, tag, value);
    }

    /**
     * Returns content length
     * @return content length
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * Returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentLength}
     * @param adapter adapter
     * @param resource resource
     * @return {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentLength}
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentLength(adapter.resourceLength(resource));
    }

    @Override
    public String toString() {
        return "GetContentLength[" + getContentLength() + "]";
    }
}
