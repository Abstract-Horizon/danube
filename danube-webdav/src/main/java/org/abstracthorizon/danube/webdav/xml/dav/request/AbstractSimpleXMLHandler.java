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
package org.abstracthorizon.danube.webdav.xml.dav.request;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Abstract XML handler. This method defines some default actions for {@link XMLParserHandler#start(Object, String, Attributes)}
 *  and {@link XMLParserHandler#end(Object, String, String)} methods
 *
 * @author Daniel Sendula
 */
public abstract class AbstractSimpleXMLHandler implements XMLParserHandler {

    /** Parent parser handler */
    protected XMLParserHandler parent;

    /**
     * Constructor
     */
    protected AbstractSimpleXMLHandler() {
    }

    /**
     * Constructor
     * @param parent parent parser handler
     */
    public AbstractSimpleXMLHandler(XMLParserHandler parent) {
        this.parent = parent;
    }

    /**
     * Returns current object and does nothing else
     * @param current current object
     * @param tag tag
     * @param attributes attribtues
     */
    public Object start(Object current, String tag, Attributes attributes) throws SAXException {
        if (parent != null) {
            // return parent.start(current, tag, attributes);
            return current;
        } else {
            return current;
        }
    }

    /**
     * Returns parent object and does nothing else
     * @param current current object
     * @param tag tag
     * @param value tag value
     */
    public Object end(Object current, String tag, String value) throws SAXException {
        return parent;
    }
}
