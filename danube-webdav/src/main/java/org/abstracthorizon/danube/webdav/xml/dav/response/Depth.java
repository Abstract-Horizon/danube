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
 * This class models WebDAV's depth response property tag
 *
 * @author Daniel Sendula
 */
public class Depth implements XMLRenderer {

    /**
     * Depth array to be used when making tags. Array follows {@link org.abstracthorizon.danube.webdav.util.Depth#ZERO},
     * {@link org.abstracthorizon.danube.webdav.util.Depth#ONE} and {@link org.abstracthorizon.danube.webdav.util.Depth#INFINITY}
     * constants
     */
    public static final Depth[] DEPTHS = new Depth[]{
        new Depth(org.abstracthorizon.danube.webdav.util.Depth.ZERO),
        new Depth(org.abstracthorizon.danube.webdav.util.Depth.ONE),
        new Depth(org.abstracthorizon.danube.webdav.util.Depth.INFINITY)
    };

    /** Tag name &quot;depth&quot; */
    public static final String TAG_NAME = "depth";

    /** Depth */
    protected int depth;

    /**
     * Constructor
     * @param depth depth
     */
    public Depth(int depth) {
        this.depth =  depth;
    }

    /**
     * Returns depth
     * @return
     */
    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "Depth[" + org.abstracthorizon.danube.webdav.util.Depth.toString(depth) + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        if (depth == org.abstracthorizon.danube.webdav.util.Depth.INFINITY) {
            writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, "Infinity"));
        } else if (depth == org.abstracthorizon.danube.webdav.util.Depth.ONE) {
            writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, "1"));
        } else if (depth == org.abstracthorizon.danube.webdav.util.Depth.ZERO) {
            writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, "0"));
        } else {
            writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, null));
        }
    }

}
