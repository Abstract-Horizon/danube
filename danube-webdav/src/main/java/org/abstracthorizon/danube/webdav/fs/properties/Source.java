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
package org.abstracthorizon.danube.webdav.fs.properties;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

/**
 * This implementation does nothing
 *
 * @author Daniel Sendula
 */
public class Source extends org.abstracthorizon.danube.webdav.xml.dav.request.properties.Source {

    /**
     * Constructor
     * @param parent parser handler
     */
    public Source(XMLParserHandler parent) {
        super(parent);
    }

    /**
     * Returns {@link org.abstracthorizon.danube.webdav.xml.dav.response.properties.Source} with
     * {@link Status#OK} value.
     * @param adapter adapter
     * @param resource a file
     * @return request property
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.Source(Status.OK);
    }
}

