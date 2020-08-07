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
import org.abstracthorizon.danube.webdav.xml.dav.HRef;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's owner tag
 *
 * @author Daniel Sendula
 */
public class Owner extends AbstractSimpleXMLHandler {

    /** Owner */
    protected Object owner;

    /**
     * Constructor
     * @param parent parent
     */
    public Owner(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("href".equals(tag)) {
            owner = new HRef(this);
            return owner;
        } else {
            return super.start(current, tag, attributes);
        }
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        if (this.owner == null) {
            this.owner = value;
        }
        return super.end(current, tag, value);
    }

    /**
     * Returns owner
     * @return owner
     */
    public Object getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        if (owner != null) {
            return "Owner[" + owner + "]";
        } else {
            return "Owner[]";
        }
    }

}
