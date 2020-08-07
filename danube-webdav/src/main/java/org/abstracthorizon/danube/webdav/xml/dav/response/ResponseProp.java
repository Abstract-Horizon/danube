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
package org.abstracthorizon.danube.webdav.xml.dav.response;

import org.abstracthorizon.danube.webdav.util.NamespacesProvider;
import org.abstracthorizon.danube.webdav.xml.XMLRenderer;
import org.abstracthorizon.danube.webdav.xml.common.XMLUtils;
import org.abstracthorizon.danube.webdav.xml.dav.DAVNamespace;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models WebDAV's response prop tag.
 *
 * @author Daniel Sendula
 */
public class ResponseProp implements XMLRenderer {

    /** Tag name */
    public static final String TAG_NAME = "prop";

    /** List of properties */
    protected List<ResponseProperty> properties;

    /**
     * Constructor
     */
    public ResponseProp() {
    }

    /**
     * Returns a list of properties. It will never be null.
     * @return a list of properties
     */
    public List<ResponseProperty> getProperties() {
        if (properties == null) {
            properties = new ArrayList<ResponseProperty>();
        }
        return properties;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer("Prop[");
        if (properties != null) {
            boolean first = true;
            for (ResponseProperty property : properties) {
                if (first) {
                    first = false;
                } else {
                    result.append(',');
                }
                result.append(property.toString());
            }
        }
        result.append(']');
        return result.toString();
    }

    /**
     * Renders the tag
     * @param writer writer
     * @param provider namespace provider
     */
    public void render(PrintWriter writer, NamespacesProvider provider) {
        writer.println(XMLUtils.createStartTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        if (properties != null) {
            for (ResponseProperty property : properties) {
                property.render(writer, provider);
            }
        }
        writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
    }

    /**
     * Renders the tag with all namespaces. It is used as partial response xml.
     *
     * @param writer writer
     * @param provider namespace provider
     */
    public void renderWithNamespaces(PrintWriter writer, NamespacesProvider provider) {
        writer.print(XMLUtils.createStartOpenTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
        writer.print(' ');
        MultiStatus.renderNamespaces(writer, provider);
        writer.println('>');
        if (properties != null) {
            for (ResponseProperty property : properties) {
                property.render(writer, provider);
            }
        }
        writer.println(XMLUtils.createEndTag(provider, DAVNamespace.DAV_NAMESPACE_URL, TAG_NAME));
    }
}
