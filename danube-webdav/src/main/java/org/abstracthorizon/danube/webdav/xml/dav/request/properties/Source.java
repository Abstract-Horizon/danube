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

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;

import org.xml.sax.SAXException;

/**
 * This class models WebDAV's source request property
 *
 * @author Daniel Sendula
 */
public class Source extends RequestProperty {

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public Source(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        return super.end(current, tag, value);
    }

    @Override
    public String toString() {
        return "Source[]";
    }

}
