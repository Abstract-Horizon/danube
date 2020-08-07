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
import org.abstracthorizon.danube.webdav.lock.LockingMechanism;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;
import org.abstracthorizon.danube.webdav.xml.dav.response.LockScope;
import org.abstracthorizon.danube.webdav.xml.dav.response.LockType;

import java.io.PrintWriter;

/**
 * This class models WebDAV's supportedlock response tag
 *
 * @author Daniel Sendula
 */
public class SupportedLock extends ResponseProperty {

    /** Tag name */
    public static final String TAG_NAME = "supportedlock";

    /** Lock tag entry tag name - &quot;lockentry&quot; */
    public static final String LOCK_ENTRY_TAG_NAME = "lockentry";

    /** Array of supported scopes */
    protected int[] scopes;

    /**
     * Constructor
     * @param status status
     */
    public SupportedLock(Status status) {
        super(status);
    }

    /**
     * Constructor
     * @param scopes an array of supported scopes
     */
    public SupportedLock(int[] scopes) {
        this.scopes = scopes;
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer("SupportedLock[");
        for (int scope : scopes) {
            if (scope == LockingMechanism.SCOPE_EXCLUSIVE) {
                res.append("LockEntry[exclusive,write]");
            } else if (scope == LockingMechanism.SCOPE_SHARED) {
                res.append("LockEntry[shared,write]");
            }
        }
        res.append(']');
        return res.toString();
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        for (int scope : scopes) {
            if (scope == LockingMechanism.SCOPE_EXCLUSIVE) {
                writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, LOCK_ENTRY_TAG_NAME));
                LockScope.EXCLUSIVE.render(writer, provider);
                LockType.WRITE.render(writer, provider);
                writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, LOCK_ENTRY_TAG_NAME));
            } else if (scope == LockingMechanism.SCOPE_SHARED) {
                writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, LOCK_ENTRY_TAG_NAME));
                LockScope.SHARED.render(writer, provider);
                LockType.WRITE.render(writer, provider);
                writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, LOCK_ENTRY_TAG_NAME));
            }
        }
        writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
    }

}
