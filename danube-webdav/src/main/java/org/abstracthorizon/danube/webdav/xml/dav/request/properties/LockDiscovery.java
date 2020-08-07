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

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.lock.Lock;
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import org.xml.sax.SAXException;

/**
 * This class models WebDAV's lockdiscovery request property
 *
 * @author Daniel Sendula
 */
public class LockDiscovery extends RequestProperty {

    /**
     * Constructory
     * @param parent
     */
    public LockDiscovery(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        return super.end(current, tag, value);
    }

    @Override
    public String toString() {
        return "LockDiscovery[]";
    }

    /**
     * Returns lock discovetry response property based on locking mechanism defined in the adapter
     * @param adapter adapter
     * @param resource resource
     * @return lock discovery response property
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        LockingMechanism lockingMechanism = adapter.getLockingMechanism();
        if (lockingMechanism != null) {
            Lock[] locks = lockingMechanism.getLocks(resource);
            org.abstracthorizon.danube.webdav.xml.dav.response.properties.LockDiscovery lockDiscovery = new org.abstracthorizon.danube.webdav.xml.dav.response.properties.LockDiscovery(Status.OK);
            if ((locks != null) && (locks.length > 0)) {
                for (Lock l : locks) {
                    lockDiscovery.getLocks().add(l);
                }
            }
            return lockDiscovery;
        } else {
            return super.processResponse(adapter, resource);
        }
    }
}
