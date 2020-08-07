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

import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;
import org.abstracthorizon.danube.webdav.xml.dav.request.LockEntry;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class models WebDAV's supportedlock request property
 *
 * @author Daniel Sendula
 */
public class SupportedLock extends RequestProperty {

    /** Factory */
    protected DAVFactory davFactory;

    /** List of lock entries */
    protected List<LockEntry> lockEntries = new ArrayList<LockEntry>();

    /**
     * Constructor
     * @param parent parnet parser handler
     * @param factory factory
     */
    public SupportedLock(XMLParserHandler parent, DAVFactory factory) {
        super(parent);
        this.davFactory = factory;
    }

    @Override
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if ("lockentry".equals(tag)) {
            LockEntry lockEntry = davFactory.newLockEntry(this);
            lockEntries.add(lockEntry);
            return lockEntry;
        }  else {
            return super.start(current, tag, attributes);
        }
    }

    /**
     * Returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.SupportedLock}
     * @param adapter adapter
     * @param resource resource
     * @return {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.SupportedLock}
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        int[] scopes = lockingMechanism.getSupportedLockScopes(resource);
        return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.SupportedLock(scopes);
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer("SupportedLock[");
        for (LockEntry lockEntry : lockEntries) {
            res.append(lockEntry.toString());
        }
        res.append(']');
        return res.toString();
    }
}
