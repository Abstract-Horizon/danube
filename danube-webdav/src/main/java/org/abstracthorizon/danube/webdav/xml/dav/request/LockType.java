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
package org.abstracthorizon.danube.webdav.xml.dav.request;

import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's locktype tag. This class represents
 * only {@link LockingMechanism#TYPE_WRITE}
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
