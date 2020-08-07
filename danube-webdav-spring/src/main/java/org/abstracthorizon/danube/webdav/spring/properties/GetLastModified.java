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
package org.abstracthorizon.danube.webdav.spring.properties;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.Resource;

/**
 * This property can set file's last modified timestamp
 *
 * @author Daniel Sendula
 */
public class GetLastModified extends org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetLastModified {

    /**
     * Constructor
     * @param parent parser handler
     */
    public GetLastModified(XMLParserHandler parent) {
        super(parent);
    }

    /**
     * Sets file's timestamp
     * @param adapter adapter
     * @param resource a file
     * @return response property
     */
    public ResponseProperty processSetProperty(ResourceAdapter adapter, Object resource) {
        long newTimestamp = getLastModified();
        Resource res = (Resource)resource;
        try {
            File file = res.getFile();
            long oldTimestamp = file.lastModified();
            if (newTimestamp != oldTimestamp) {
                file.setLastModified(newTimestamp);
                File f = new File(file.getParentFile(), file.getName());
                if (newTimestamp != f.lastModified()) {
                    return processResponse(adapter, file);
                }
            }
        } catch (IOException ignore) {
        }
        return constructResponse(Status.FORBIDDEN);
    }
}

