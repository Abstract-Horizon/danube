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
import org.abstracthorizon.danube.webdav.xml.dav.DAVAbstractXMLParser;
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's lockentry tag
 *
 * @author Daniel Sendula
 */
public class LockEntry extends DAVAbstractXMLParser {

    /** Requested lock scope */
    protected LockScope lockScope;

    /** Requested lock type */
    protected LockType lockType;

    /**
     * Constructor
     * @param parent parent parser handler
     * @param factory factory
     */
    public LockEntry(XMLParserHandler parent, DAVFactory factory) {
        super(parent, factory);
        lockScope = factory.newLockScope(this);
        lockType = factory.newLockType(this);
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("lockscope".equals(tag)) {
            return lockScope;
        } else if ("locktype".equals(tag)) {
            return lockType;
        }  else {
            return super.start(current, tag, attributes);
        }
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        return this;
    }

    /**
     * Returns requested lock's type.
     * @return lock's type
     * @see LockingMechanism#TYPE_WRITE
     */
    public int getType() {
        return LockingMechanism.TYPE_WRITE;
    }

    /**
     * Returns requested lock's scope.
     * @return lock's scope
     * @see LockingMechanism#SCOPE_EXCLUSIVE
     * @see LockingMechanism#SCOPE_SHARED
     */
    public int getScope() {
        if (lockScope == null) {
            return LockingMechanism.SCOPE_NONE;
        } else {
            return lockScope.getScope();
        }
    }

    @Override
    public String toString() {
        return "LockEntry[" + lockScope + "," + lockType + "]";
    }
}
