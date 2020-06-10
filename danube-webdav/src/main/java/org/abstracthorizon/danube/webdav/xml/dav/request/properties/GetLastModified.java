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

import org.abstracthorizon.danube.http.HTTPServerConnectionHandler;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.text.ParseException;
import java.util.Date;

import org.xml.sax.SAXException;

/**
 * This class models WebDAV's getlastmodified request property tag
 *
 * @author Daniel Sendula
 */
public class GetLastModified extends RequestProperty {

    /** Last modified timestamp */
    protected long lastModified;

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public GetLastModified(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        if ((value != null) && (value.length() >0)) {
            try {
                lastModified = HTTPServerConnectionHandler.DATE_FORMAT.parse(value).getTime();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return super.end(current, tag, value);
    }

    /**
     * This class returns last modified timestamp
     * @return last modified timestamp
     */
    public long getLastModified() {
        return lastModified;
    }

    /**
     * Returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetLastModified}
     * @param adapter adapter
     * @param resource resource
     * @return {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetLastModified}
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetLastModified(adapter.resourceLastModified(resource));
    }

    /**
     * Retunrs timestamp as string using {@HTTPServerConnectionHandler~DATE_FORMAT} format.
     * @return timestamp as string
     */
    public String asString() {
        return HTTPServerConnectionHandler.DATE_FORMAT.format(new Date(lastModified));
    }

    @Override
    public String toString() {
        if (lastModified != 0) {
            return "GetLastModified[" + asString() + "]";
        } else {
            return "GetLastModified[]";
        }
    }
}
