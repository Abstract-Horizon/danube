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
package org.abstracthorizon.danube.webdav.xml.dav;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.request.AbstractSimpleXMLHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's lock type tag. It is always write.
 *
 * @author Daniel Sendula
 */
public class LockType extends AbstractSimpleXMLHandler {

    /**
     * Constructor
     * @param parent
     */
    public LockType(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("write".equals(tag)) {
            return this;
        } else {
            return super.start(current, tag, attributes);
        }
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        if ("write".equals(tag)) {
            return this;
        } else {
            return super.end(current, tag, value);
        }
    }

    @Override
    public String toString() {
        return "LockType[write]";
    }

}
