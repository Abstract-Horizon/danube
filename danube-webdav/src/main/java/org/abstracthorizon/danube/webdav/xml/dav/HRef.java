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
package org.abstracthorizon.danube.webdav.xml.dav;

import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.XMLRenderer;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.request.AbstractSimpleXMLHandler;

import java.io.PrintWriter;

import org.xml.sax.SAXException;

/**
 * This object models WebDAV's href tag
 *
 * @author Daniel Sendula
 */
public class HRef extends AbstractSimpleXMLHandler implements XMLRenderer {

    /** Tag name */
    public static final String TAG_NAME = "href";

    /** URL path */
    protected String path;

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public HRef(XMLParserHandler parent) {
        super(parent);
    }

    /**
     * Constructor
     * @param uri URL
     */
    public HRef(String uri) {
        super(null);
        this.path = uri.replace(" ", "%20");
    }

    /**
     * End tag handling
     * @param current current object
     * @param tag tag
     * @param value tag's value
     * @return new object
     * @throws SAXException sax exception
     */
    public Object end(Object current, String tag, String value) throws SAXException {
        path = value;
        return super.end(current, tag, value);
    }

    /**
     * Returns object's string representation
     * @return object's string representation
     */
    public String toString() {
        return "HRef[" + path + "]";
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME, path));
    }
}
