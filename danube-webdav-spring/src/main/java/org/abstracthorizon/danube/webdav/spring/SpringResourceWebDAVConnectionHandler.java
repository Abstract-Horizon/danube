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

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.webdav.BaseWebDAVResourceConnectionHandler;

import org.springframework.core.io.Resource;

/**
 * This is connection handler for Spring framework resources
 *
 * @author Daniel Sendula
 */
public class SpringResourceWebDAVConnectionHandler extends BaseWebDAVResourceConnectionHandler {

    /**
     * Resources adapter
     */
    protected SpringResourceWebDAVResourceAdapter fsAdapter;

    /**
     * Constructor
     */
    public SpringResourceWebDAVConnectionHandler() {
        super();
        fsAdapter = new SpringResourceWebDAVResourceAdapter();
        setWebDAVResourceAdapter(fsAdapter);
    }

    /**
     * POST method is the same as GET method
     * @param connection http connection
     */
    public void methodPOST(HTTPConnection connection) {
        methodGET(connection);
    }


    /**
     * Sets path where files are stored
     * @return file path
     */
    public Resource getResourcePath() {
        return fsAdapter.getResourcePath();
    }

    /**
     * Sets path where files are stored
     * @param filePath file path
     */
    public void setResourcePath(Resource filePath) {
        fsAdapter.setResourcePath(filePath);
    }
}
