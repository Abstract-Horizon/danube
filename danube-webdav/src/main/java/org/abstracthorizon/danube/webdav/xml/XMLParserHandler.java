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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * XML parser handler
 *
 * @author Daniel Sendula
 */
public interface XMLParserHandler {

    /**
     * Start tag handling
     * @param current current object
     * @param tag tag
     * @param attributes tag attributes
     * @return new object
     * @throws SAXException sax exception
     */
    Object start(Object current, String tag, Attributes attributes) throws SAXException;

    /**
     * End tag handling
     * @param current current object
     * @param tag tag
     * @param value tag's value
     * @return new object
     * @throws SAXException sax exception
     */
    Object end(Object current, String tag, String value) throws SAXException;
}
