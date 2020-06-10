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
package org.abstracthorizon.danube.webdav.xml.dav.request.properties;

import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import org.xml.sax.SAXException;

/**
 * This class models WebDAV's displayname request property tag
 *
 * @author Daniel Sendula
 */
public class DisplayName extends RequestProperty {

    /** Display name */
    protected String displayName;

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public DisplayName(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        displayName = value;
        return super.end(current, tag, value);
    }

    /**
     * Returns display name
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.DisplayName} if
     * display name is available or just super value
     * @param adpater adapter
     * @param resource resource
     * @return {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.DisplayName} if
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        Object root = adapter.findParentResource(resource);
        if ((root == null) || root.equals(resource)) {
            return super.processResponse(adapter, resource);
        } else {
            return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.DisplayName(adapter.getResourceName(resource));
        }
    }

    @Override
    public String toString() {
        return "DisplayName[" + displayName + "]";
    }
}
