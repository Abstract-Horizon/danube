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
import org.abstracthorizon.danube.webdav.xml.dav.request.AbstractSimpleXMLHandler;

/**
 * WebDAV abstract XML parser
 *
 * @author Daniel Sendula
 */
public class DAVAbstractXMLParser extends AbstractSimpleXMLHandler {

    /** Factory to create requred objects */
    protected DAVFactory davFactory;

    /**
     * Constructor
     */
    protected DAVAbstractXMLParser() {
    }

    /**
     * Constructor
     * @param parent parent parser handler
     * @param davFactory factory
     */
    public DAVAbstractXMLParser(XMLParserHandler parent, DAVFactory davFactory) {
        super(parent);
        this.davFactory = davFactory;
    }

}
