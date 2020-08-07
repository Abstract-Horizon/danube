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
package org.abstracthorizon.danube.webdav.xml;


import org.abstracthorizon.danube.webdav.util.NamespacesProvider;

import java.io.IOException;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * WebDAV XML handler
 *
 * @author Daniel Sendula
 */
public class WebDAVXMLHandler extends DefaultHandler {

    /** Current object */
    protected Object currentObject;

    /** Namespace provider */
    protected NamespacesProvider namespacesProvider;

    /** String buffer for tag values */
    protected StringBuffer buffer = new StringBuffer();

    /**
     * Constructor
     * @param namespacesProvider namespace provider
     */
    public WebDAVXMLHandler(NamespacesProvider namespacesProvider) {
        this.namespacesProvider = namespacesProvider;
    }

    /**
     * Constructor
     * @param namespacesProvider namespace provider
     * @param startObject start object
     */
    public WebDAVXMLHandler(NamespacesProvider namespacesProvider, Object startObject) {
        this.currentObject = startObject;
        this.namespacesProvider = namespacesProvider;
    }

    /**
     * Returns resulted object
     * @return resulted object or <code>null</code>
     */
    public Object getResultObject() {
        return currentObject;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        XMLParserHandler handler = namespacesProvider.getParserHandler(uri);
        if (handler != null) {
            buffer.delete(0, buffer.length());
            currentObject = handler.start(currentObject, localName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        XMLParserHandler handler = namespacesProvider.getParserHandler(uri);
        if (handler != null) {
            currentObject = handler.end(currentObject, localName, buffer.toString().trim());
            buffer.delete(0, buffer.length());
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws IOException, SAXException {
        return null;
    }

    @Override
    public void characters(char[] chars, int start, int len) throws SAXException {
        buffer.append(chars, start, len);
    }
}
