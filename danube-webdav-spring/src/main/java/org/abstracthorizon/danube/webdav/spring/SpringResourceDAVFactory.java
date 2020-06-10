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
package org.abstracthorizon.danube.webdav.spring;

import org.abstracthorizon.danube.webdav.spring.properties.GetContentLength;
import org.abstracthorizon.danube.webdav.spring.properties.GetContentType;
import org.abstracthorizon.danube.webdav.spring.properties.GetLastModified;
import org.abstracthorizon.danube.webdav.spring.properties.Source;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.DAVFactory;

/**
 * DAV Factory that returns file system specific request properties
 *
 * @author Daniel Sendula
 */
public class SpringResourceDAVFactory extends DAVFactory {


    public GetContentLength newGetContentLength(XMLParserHandler parent) {
        return new GetContentLength(parent);
    }

    public GetContentType newGetContentType(XMLParserHandler parent) {
        return new GetContentType(parent);
    }

    public GetLastModified newGetLastModified(XMLParserHandler parent) {
        return new GetLastModified(parent);
    }

    public Source newSource(XMLParserHandler parent) {
        return new Source(parent);
    }

}
