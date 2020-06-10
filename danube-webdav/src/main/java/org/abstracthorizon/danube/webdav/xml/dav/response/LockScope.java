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
package org.abstracthorizon.danube.webdav.xml.dav.response;

import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.XMLRenderer;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;

/**
 * This class models WebDAV's lockscope response tag
 *
 * @author Daniel Sendula
 */
public class LockScope implements XMLRenderer {

    /** Tag name */
    public static final String TAG_NAME = "lockscope";

    /** Exclusive tag singleton */
    public static final LockScope EXCLUSIVE = new LockScope(true);

    /** Shared tag singleton */
    public static final LockScope SHARED = new LockScope(false);

    /** Exclusive flag */
    protected boolean exclusive = false;

    /**
     * Constructor
     * @param exclusive exclusive
     */
    public LockScope(boolean exclusive) {
        this.exclusive = exclusive;
    }

    @Override
    public String toString() {
        if (exclusive) {
            return "LockScope[exclusive]";
        } else {
            return "LockScope[shared]";
        }
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.print(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        if (exclusive) {
            writer.print(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, "exclusive", null));
        } else {
            writer.print(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, "shared", null));
        }
        writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
     }

}
