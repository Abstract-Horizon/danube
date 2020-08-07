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
package org.abstracthorizon.danube.webdav.java;

import org.abstracthorizon.danube.webdav.java.properties.GetContentLength;
import org.abstracthorizon.danube.webdav.java.properties.GetContentType;

import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;

/**
 * Java DAV factory. It returns java related properties
 *
 * @author Daniel Sendula
 */
public class JavaDAVFactory extends DAVFactory {

    /**
     * Returns new {@link GetContentLength} object
     * @param parent parent parser handler
     * @return new {@link GetContentLength} object
     */
    public GetContentLength newGetContentLength(XMLParserHandler parent) {
        return new GetContentLength(parent);
    }

    /**
     * Returns new {@link GetContentType} object
     * @param parent parent parser handler
     * @return new {@link GetContentType} object
     */
    public GetContentType newGetContentType(XMLParserHandler parent) {
        return new GetContentType(parent);
    }

}
