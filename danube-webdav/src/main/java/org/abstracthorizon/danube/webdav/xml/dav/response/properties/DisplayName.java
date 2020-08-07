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
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;

/**
 * This class models WebDAV's displayname response tag
 *
 * @author Daniel Sendula
 */
public class DisplayName extends ResponseProperty {

    /** Tag name */
    public static final String TAG_NAME = "displayname";

    /** Display name */
    protected String displayName;

    /**
     * Constructor
     * @param status status
     */
    public DisplayName(Status status) {
        super(status);
    }

    /**
     * Constructor
     * @param displayName display name
     */
    public DisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns display name
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "DisplayName[" + displayName + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, displayName));
    }

}
