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

import org.abstracthorizon.danube.http.HTTPServerConnectionHandler;
import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;
import java.util.Date;

public class GetLastModified extends ResponseProperty {

    public static final String TAG_NAME = "getlastmodified";

    protected long modified = -1;
    protected String cachedDate;

    /**
     * Constructor
     * @param status status
     */
    public GetLastModified(Status status) {
        super(status);
    }

    /**
     * Constructor
     * @param modified modified timestamp
     */
    public GetLastModified(long modified) {
        this.modified = modified;
    }

    /**
     * Returns last modified timestamp
     * @return last modified timestamp
     */
    public long getLastModified() {
        return modified;
    }

    /**
     * Returns last modified as a formatted string. Format used is {@link HTTPServerConnectionHandler#DATE_FORMAT}
     * @return last modified as a formatted string
     */
    public String asString() {
        if (cachedDate == null) {
            if (modified != -1) {
                cachedDate = HTTPServerConnectionHandler.DATE_FORMAT.format(new Date(modified));
            }
        }
        return cachedDate;
    }

    @Override
    public String toString() {
        if (modified != -1) {
            return "GetLastModified[" + asString() + "]";
        } else {
            return "GetLastModified[]";
        }
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, asString()));
    }

}
