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

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * DAV namespace.
 *
 * @author Daniel Sendula
 */
public class DAVNamespace implements XMLParserHandler {

    /** Namespace URL */
    public static final String DAV_NAMESPACE_URL = "DAV:";

    /** Namespace prefered prefix */
    public static final String DAV_NAMESPACE_PREFERRED_PREFIX = "D";

    /** Factory */
    protected DAVFactory davFactory = new DAVFactory();

    /**
     * Constructor
     */
    public DAVNamespace() {
    }

    /**
     * Retruns factory
     * @return factory
     */
    public DAVFactory getDAVFactory() {
        return davFactory;
    }

    /**
     * Sets factory
     * @param factory factory
     */
    public void setDAVFactory(DAVFactory factory) {
        this.davFactory = factory;
    }

    /**
     * Returns {@link #DAV_NAMESPACE_URL}
     * @return {@link #DAV_NAMESPACE_URL}
     */
    public String getURLString() {
        return DAV_NAMESPACE_URL;
    }

    /**
     * Returns {@link #DAV_NAMESPACE_PREFERRED_PREFIX}
     * @return {@link #DAV_NAMESPACE_PREFERRED_PREFIX}
     */
    public String getPreferredPrefix() {
        return DAV_NAMESPACE_PREFERRED_PREFIX;
    }

    /**
     * Start tag handling
     * @param current current object
     * @param tag tag
     * @param attributes tag attributes
     * @return new object
     * @throws SAXException sax exception
     */
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {

        if (current instanceof XMLParserHandler) {
            return ((XMLParserHandler)current).start(current, tag, attributes);
        } else {
            XMLParserHandler parent = this;
            if (current instanceof XMLParserHandler) {
                parent = (XMLParserHandler)current;
            }
            if ("propertyupdate".equals(tag)) {
                return davFactory.newPropertyUpdate(parent);
            } else if ("lockinfo".equals(tag)) {
                return davFactory.newLockInfo(parent);
            } else if ("propfind".equals(tag)) {
                return davFactory.newPropFind(parent);
            } else if ("propertybehavior".equals(tag)) {
                return davFactory.newPropertyBehavior(parent);
            } else if ("href".equals(tag)) {
                return new HRef(parent);
            }
            return current;
        }
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
        if (current instanceof XMLParserHandler) {
            return ((XMLParserHandler)current).end(current, tag, value);
        } else {

            // TODO is that right?
            return current;
        }
    }

}
