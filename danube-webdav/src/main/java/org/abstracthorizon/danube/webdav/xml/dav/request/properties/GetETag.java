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
package org.abstracthorizon.danube.webdav.xml.dav.request.properties;

import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import org.xml.sax.SAXException;

/**
 * This class models WebDAV's getetag request property tag
 *
 * @author Daniel Sendula
 */
public class GetETag extends RequestProperty {

    /** ETag */
    protected String eTag;

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public GetETag(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        eTag = value;
        return super.end(current, tag, value);
    }

    /**
     * Returns etag
     * @return etag
     */
    public String getETag() {
        return eTag;
    }

    /**
     * If etag is present returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetETag}
     * @param adapter adapter
     * @param resource resource
     * @return {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetETag}
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        String eTag = adapter.getResourceETag(resource);
        if (eTag != null) {
            return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetETag(eTag);
        } else {
            return super.processResponse(adapter, resource);
        }
    }

    @Override
    public String toString() {
        return "GetETag[" + eTag + "]";
    }
}
