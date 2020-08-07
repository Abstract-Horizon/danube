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
package org.abstracthorizon.danube.webdav.xml.dav.response;

import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.XMLRenderer;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;

/**
 * This class models WebDAV's owner response tag
 *
 * @author Daniel Sendula
 */
public class Owner implements XMLRenderer {

    /** Tag name */
    public static final String TAG_NAME = "owner";

    /** Owner */
    protected Object owner;

    /**
     * Constructor
     * @param owner owner
     */
    public Owner(Object owner) {
        this.owner = owner;
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

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        if (owner != null) {
            if (owner instanceof XMLRenderer) {
                writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
                ((XMLRenderer)owner).render(writer, provider);
                writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
            } else {
                writer.print(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
                writer.print(owner);
                writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
            }
        } else {
            writer.print(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, null));
        }
     }

}
