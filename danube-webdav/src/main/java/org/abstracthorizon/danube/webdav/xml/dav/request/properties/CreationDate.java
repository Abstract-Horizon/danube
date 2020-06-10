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
 * This class models WebDAV's request property creationdate
 *
 * @author Daniel Sendula
 */
public class CreationDate extends RequestProperty {

    /** Requested date string */
    protected String dateString;

    /**
     * Cosntructor
     * @param parent parent parser handler
     */
    public CreationDate(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        dateString = value;
        return super.end(current, tag, value);
    }

    /**
     * Returns date string
     * @return date string
     */
    public String asString() {
        return dateString;
    }

    /**
     * Returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.CreationDate}
     * @param adapater adapter
     * @param resource resource
     * @return {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.CreationDate}
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.CreationDate(adapter.resourceCreated(resource));
    }

    @Override
    public String toString() {
        return "CreationDate[" + dateString + "]";
    }
}
