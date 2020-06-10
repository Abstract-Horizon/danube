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
 * This class models WebDAV's getcontenttype response tag
 *
 * @author Daniel Sendula
 */
public class GetContentType extends ResponseProperty {

    /** Tag name */
    public static final String TAG_NAME = "getcontenttype";

    /** Content type */
    protected String contentType;

    /**
     * Constructor
     * @param status status
     */
    public GetContentType(Status status) {
        super(status);
    }

    /**
     * Constructgor
     * @param contentType content type
     */
    public GetContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns contnet type
     * @return content type
     */
    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        return "GetContentType[" + contentType + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, contentType));
    }

}
