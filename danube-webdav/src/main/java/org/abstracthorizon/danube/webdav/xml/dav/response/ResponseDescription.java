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
 * This class models WebDAV's responsedescription tag
 *
 * @author Daniel Sendula
 */
public class ResponseDescription implements XMLRenderer {

    /** Tag name */
    public static final String TAG_NAME = "responsedescription";

    /** Response descirption */
    protected String responseDescription;

    /**
     * Constructor
     * @param responseDescription response description
     */
    public ResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    /**
     * Returns response description
     * @return response description
     */
    public String getResponseDescription() {
        return responseDescription;
    }

    @Override
    public String toString() {
        return "ResponseDescription[" + responseDescription + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, responseDescription));
    }
}
