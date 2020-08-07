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
package org.abstracthorizon.danube.webdav.xml.dav.response.properties;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.lock.Lock;
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;
import org.abstracthorizon.danube.webdav.xml.dav.HRef;
import org.abstracthorizon.danube.webdav.xml.dav.response.Depth;
import org.abstracthorizon.danube.webdav.xml.dav.response.LockScope;
import org.abstracthorizon.danube.webdav.xml.dav.response.LockType;
import org.abstracthorizon.danube.webdav.xml.dav.response.Owner;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models WebDAV's lockdiscovery response tag
 *
 * @author Daniel Sendula
 */
public class LockDiscovery extends ResponseProperty {

    /** Tag name */
    public static final String TAG_NAME = "lockdiscovery";

    /** Active lock tag name */
    public static final String ACTIVE_LOCK_TAG_NAME = "activelock";

    /** Timeout tag name */
    public static final String TIMEOUT_TAG_NAME = "timeout";

    /** Lock token tag name */
    public static final String LOCK_TOKEN_TAG_NAME = "locktoken";

    /** List of locks */
    protected List<Lock> locks;

    /**
     * Constructor
     * @param status status
     */
    public LockDiscovery(Status status) {
        super(status);
    }

    @Override
    public String toString() {
        return "LockDiscovery[]";
    }

    /**
     * Returns list of locks. It will never be <code>null</code>.
     * @return list of locks
     */
    public List<Lock> getLocks() {
        if (locks == null) {
            locks = new ArrayList<Lock>();
        }
        return locks;
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        if (locks == null) {
            writer.println(XMLUtils.createEmptyTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        } else {
            writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
            for (Lock lock : locks) {
                writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, ACTIVE_LOCK_TAG_NAME));
                LockType.WRITE.render(writer, provider);
                if (lock.getScope() == LockingMechanism.SCOPE_EXCLUSIVE) {
                    LockScope.EXCLUSIVE.render(writer, provider);
                } else {
                    LockScope.SHARED.render(writer, provider);
                }
                Depth.DEPTHS[lock.getDepth()].render(writer, provider);
                if (lock.getOwner() != null) {
                    Owner owner = new Owner(lock.getOwner());
                    owner.render(writer, provider);
                }
                if (lock.getTimeout() != null) {
                    writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TIMEOUT_TAG_NAME, lock.getTimeout().asString()));
                }
                if (lock.getToken() != null) {
                    HRef href = new HRef(lock.getToken().toString());
                    writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, LOCK_TOKEN_TAG_NAME));
                    href.render(writer, provider);
                    writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, LOCK_TOKEN_TAG_NAME));
                }

                writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, ACTIVE_LOCK_TAG_NAME));
            }
            writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        }
    }
}
