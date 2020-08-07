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
package org.abstracthorizon.danube.webdav.xml.dav.request;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.DAVAbstractXMLParser;
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's propertybehaviour tag
 *
 * @author Daniel Sendula
 */
public class PropertyBehavior extends DAVAbstractXMLParser {

    /** Omit flag */
    protected boolean omit;

    /** Keep alive */
    protected KeepAlive keepAlive;

    /**
     * Constructor
     * @param parent parent parser handler
     * @param factory factory
     */
    public PropertyBehavior(XMLParserHandler parent, DAVFactory factory) {
        super(parent, factory);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("omit".equals(tag)) {
            omit = true;
            return this;
        } else if ("keepalive".equals(tag)) {
            keepAlive = davFactory.newKeepAlive(this);
            return keepAlive;
        }
        return super.start(current, tag, attributes);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        return this;
    }

    @Override
    public String toString() {
        if (keepAlive != null) {
            return "PropertyBehavior[" + keepAlive + "]";
        } else {
            return "PropertyBehavior[omit]";
        }
    }
}
