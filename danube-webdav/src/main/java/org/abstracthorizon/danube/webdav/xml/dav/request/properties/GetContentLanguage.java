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
package org.abstracthorizon.danube.webdav.xml.dav.request.properties;

import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.util.Locale;

import org.xml.sax.SAXException;

/**
 * This class models WebDAV's getcontentlanguage tag
 *
 * @author Daniel Sendula
 */
public class GetContentLanguage extends RequestProperty {

    /** Cached local content language */
    protected static String cachedContentLanguage = Locale.getDefault().getLanguage();

    /** Content language */
    protected String contentLanguage;

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public GetContentLanguage(XMLParserHandler parent) {
        super(parent);
    }

    @Override
    public Object end(Object current, String tag, String value) throws SAXException {
        contentLanguage = value;
        return super.end(current, tag, value);
    }

    /**
     * Returns content language
     * @return content language
     */
    public String getContentLanguage() {
        return contentLanguage;
    }

    /**
     * Returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentLanguage}
     * @param adapter adapter
     * @param resource resource
     * @return {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentLanguage}
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentLanguage(cachedContentLanguage);
    }

    @Override
    public String toString() {
        return "GetContentLanguage[" + contentLanguage + "]";
    }
}
