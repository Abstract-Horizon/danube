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
 * This class models WebDAV's getcontentlanguage response tag
 *
 * @author Daniel Sendula
 */
public class GetContentLanguage extends ResponseProperty {

    /** Tag name */
    public static final String TAG_NAME = "getcontentlanguage";

    /** Content language */
    protected String contentLanguage;

    /**
     * Constructor
     * @param status status
     */
    public GetContentLanguage(Status status) {
        super(status);
    }

    /**
     * Constructor
     * @param contentLanguage content language
     */
    public GetContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }

    /**
     * Get content language
     * @return content language
     */
    public String getContentLanguage() {
        return contentLanguage;
    }

    @Override
    public String toString() {
        return "GetContentLanguage[" + contentLanguage + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        if ((contentLanguage == null) || (contentLanguage.length() == 0)) {
            writer.println(XMLUtils.createEmptyTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        } else {
            writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, contentLanguage));
        }
    }

}
