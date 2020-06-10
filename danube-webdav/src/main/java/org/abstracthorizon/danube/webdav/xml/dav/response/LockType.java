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
 * This class models WebDAV's locktype response tag
 *
 * @author Daniel Sendula
 */
public class LockType implements XMLRenderer {

    /** Tag name */
    public static final String TAG_NAME = "locktype";

    /** Write tag singleton */
    public static final LockType WRITE = new LockType();

    /**
     * Constructor
     */
    public LockType() {
    }

    @Override
    public String toString() {
        return "LockType[write]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.print(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        writer.print(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, "write", null));
        writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
     }
}
