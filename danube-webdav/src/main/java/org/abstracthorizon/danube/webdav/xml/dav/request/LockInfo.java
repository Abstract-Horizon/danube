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
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's lockinfo tag
 *
 * @author Daniel Sendula
 */
public class LockInfo extends LockEntry {

    /** Owner */
    protected Owner owner;

    /**
     * Constructor
     * @param parent parent parser handler
     * @param factory factory
     */
    public LockInfo(XMLParserHandler parent, DAVFactory factory) {
        super(parent, factory);
        lockScope = factory.newLockScope(this);
        lockType = factory.newLockType(this);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("owner".equals(tag)) {
            owner = new Owner(this);
            return owner;
        }  else {
            return super.start(current, tag, attributes);
        }
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        return this;
    }

    /**
     * Returns owner
     * @return owner
     */
    public Object getOwner() {
        if (owner != null) {
            return owner.getOwner();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        if (owner == null) {
            return "LockInfo[" + lockScope + "," + lockType + "]";
        } else {
            return "LockInfo[" + lockScope + "," + lockType + "," + owner + "]";
        }
    }
}
