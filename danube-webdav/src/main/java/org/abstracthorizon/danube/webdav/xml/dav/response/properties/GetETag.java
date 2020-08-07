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
 * This class models WebDAV's getetag response tag
 *
 * @author Daniel Sendula
 */
public class GetETag extends ResponseProperty {

    /** Tag name */
    public static final String TAG_NAME = "getetag";

    /** ETag */
    protected String eTag;

    /**
     * Constructor
     * @param status status
     */
    public GetETag(Status status) {
        super(status);
    }

    /**
     * Constructor
     * @param eTag etag
     */
    public GetETag(String eTag) {
        this.eTag = eTag;
    }

    /**
     * Returns etag
     * @return etag
     */
    public String getETag() {
        return eTag;
    }

    @Override
    public String toString() {
        return "GetETag[" + eTag + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, eTag));
    }

}
