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

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;

import org.xml.sax.SAXException;

/**
 * This class models WebDAV's getcontenttype request property tag
 *
 * @author Daniel Sendula
 */
public class GetContentType extends RequestProperty {

    /** Content type */
    protected String contentType;

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public GetContentType(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        contentType = value;
        return super.end(current, tag, value);
    }

    /**
     * Returns content type
     * @return content type
     */
    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "GetContentType[" + contentType + "]";
    }
}
