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
package org.abstracthorizon.danube.webdav.java.properties;

import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

/**
 *
 * This class obtains content length from given resource
 *
 * @author Daniel Sendula
 *
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
     * Sets the property
     * @param webDAVAdapter DAV adapter
     * @param resource resource
     * @return response property
     */
    public ResponseProperty processSetProperty(ResourceAdapter webDAVAdapter, Object resource) {
        long newLength = getContentLength();
        if (newLength >= 0) {
//            File file = (File)resource;
//            try {
//                RandomAccessFile raf = new RandomAccessFile(file, "rw");
//                long oldLength = raf.length();
//                if (newLength != oldLength) {
//                    raf.setLength(newLength);
//                    File f = new File(file.getParentFile(), file.getName());
//                    if (newLength == f.length()) {
//                        return processResponse(webDAVAdapter, file);
//                    }
//                }
//            } catch (IOException e) {
//
//            }
//            return constructResponse(Status.FORBIDDEN);
            return super.processSetProperty(webDAVAdapter, resource);
        } else {
            return super.processSetProperty(webDAVAdapter, resource);
        }
    }
}

