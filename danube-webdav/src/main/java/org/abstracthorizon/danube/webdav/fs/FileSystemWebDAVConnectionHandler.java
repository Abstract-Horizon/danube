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
package org.abstracthorizon.danube.webdav.fs;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.webdav.BaseWebDAVResourceConnectionHandler;

import java.io.File;

public class FileSystemWebDAVConnectionHandler extends BaseWebDAVResourceConnectionHandler {

    protected FileSystemWebDAVResourceAdapter fsAdapter;

    public FileSystemWebDAVConnectionHandler() {
        super();
        fsAdapter = new FileSystemWebDAVResourceAdapter();
        setWebDAVResourceAdapter(fsAdapter);
    }

    public void methodPOST(HTTPConnection connection) {
        methodGET(connection);
    }


    /**
     * Sets path where files are stored
     * @return file path
     */
    public File getFilePath() {
        return fsAdapter.getFilePath();
    }

    /**
     * Sets path where files are stored
     * @param filePath file path
     */
    public void setFilePath(File filePath) {
        fsAdapter.setFilePath(filePath);
    }


}
