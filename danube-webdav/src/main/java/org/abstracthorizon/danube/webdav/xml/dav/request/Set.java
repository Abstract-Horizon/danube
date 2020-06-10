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

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.DAVAbstractXMLParser;
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;
import org.abstracthorizon.danube.webdav.xml.dav.RequestProp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's set property
 *
 * @author Daniel Sendula
 */
public class Set extends DAVAbstractXMLParser {

    /** Nested request property */
    protected RequestProp prop;

    /**
     * Constructor
     * @param parent request property
     * @param factory factory
     */
    public Set(XMLParserHandler parent, DAVFactory factory) {
        super(parent, factory);
        prop = factory.newProp(this);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("prop".equals(tag)) {
            return prop;
        } else {
            return super.start(current, tag, attributes);
        }
    }

    @Override
    public String toString() {
        return "Set[" + prop + "]";
    }
}
