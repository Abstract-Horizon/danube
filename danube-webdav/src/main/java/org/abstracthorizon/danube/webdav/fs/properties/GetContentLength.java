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
package org.abstracthorizon.danube.webdav.fs.properties;

import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This property can set file's length.
 *
 * @author Daniel Sendula
 */
public class GetContentLength extends org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetContentLength {

    /**
     * Constructor
     * @param parent
     */
    public GetContentLength(XMLParserHandler parent) {
        super(parent);
    }

    /**
     * Sets file's length
     * @param adpate adapter
     * @param resource a file
     * @return response property
     */
    public ResponseProperty processSetProperty(ResourceAdapter adapter, Object resource) {
        long newLength = getContentLength();
        if (newLength >= 0) {
            File file = (File)resource;
            try {
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                try {
                    long oldLength = raf.length();
                    if (newLength != oldLength) {
                        raf.setLength(newLength);
                        File f = new File(file.getParentFile(), file.getName());
                        if (newLength == f.length()) {
                            return processResponse(adapter, file);
                        }
                    }
                } finally {
                    raf.close();
                }
            } catch (IOException e) {

            }
            return constructResponse(Status.FORBIDDEN);
        } else {
            return super.processSetProperty(adapter, resource);
        }
    }
}

