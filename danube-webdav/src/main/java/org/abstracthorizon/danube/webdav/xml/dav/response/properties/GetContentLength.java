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
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;

import java.io.PrintWriter;

/**
 * This class models WebDAV's getcontentlength response tag
 *
 * @author Daniel Sendula
 */
public class GetContentLength extends ResponseProperty {

    /** Tag name */
    public static final String TAG_NAME = "getcontentlength";

    /** Content length */
    protected long contentLength = -1;

    /**
     * Constructor
     * @param status status
     */
    public GetContentLength(Status status) {
        super(status);
    }

    /**
     * Constructor
     * @param contentLength content length
     */
    public GetContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Returns content length
     * @return content length
     */
    public long getContentLength() {
        return contentLength;
    }

    @Override
    public String toString() {
        return "GetContentLength[" + contentLength + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        if (contentLength >= 0) {
            writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, Long.toString(contentLength)));
        } else {
            writer.println(XMLUtils.createEmptyTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        }
    }

}
