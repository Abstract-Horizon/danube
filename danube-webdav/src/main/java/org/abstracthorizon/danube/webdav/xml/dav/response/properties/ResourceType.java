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
package org.abstracthorizon.danube.webdav.xml.dav.response.properties;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.XMLRenderer;
import org.abstracthorizon.danube.webdav.xml.common.Collection;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;

/**
 * This class models WebDAV's  resourcetype response tag
 *
 * @author Daniel Sendula
 */
public class ResourceType extends ResponseProperty {

    /** Tag name */
    public static final String TAG_NAME = "resourcetype";

    /** Collection tag singleton */
    public static final Collection COLLECTION = new Collection();

    /** Resource type */
    protected XMLRenderer resourceType;

    /**
     * Constructor
     * @param status status
     */
    public ResourceType(Status status) {
        super(status);
    }

    /**
     * Constructor
     * @param collection collection flag
     */
    public ResourceType(boolean collection) {
        if (collection) {
            resourceType = COLLECTION;
        } else {
            resourceType = null;
        }
    }

    /**
     * Constructor
     * @param resourceType resource type
     */
    public ResourceType(XMLRenderer resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String toString() {
        if (resourceType != null) {
            return "ResourceType[" + resourceType + "]";
        } else {
            return "ResourceType[]";
        }
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        if (resourceType == null) {
            writer.println(XMLUtils.createEmptyTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        } else {
            writer.print(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
            resourceType.render(writer, provider);
            writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        }
    }
}
