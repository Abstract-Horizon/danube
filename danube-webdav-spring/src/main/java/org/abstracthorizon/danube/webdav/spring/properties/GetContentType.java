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
package org.abstracthorizon.danube.webdav.spring.properties;

import org.abstracthorizon.danube.http.util.FileTypeMapUtil;
import org.abstracthorizon.danube.webdav.ResourceAdapter;
import org.abstracthorizon.danube.webdav.xml.XMLParserHandler;
import org.abstracthorizon.danube.webdav.xml.dav.response.properties.ResponseProperty;

import javax.activation.FileTypeMap;

import org.springframework.core.io.Resource;

/**
 * This request property returns file's mime type
 *
 * @author Daniel Sendula
 */
public class GetContentType extends org.abstracthorizon.danube.webdav.xml.dav.request.properties.GetContentType {

    /** File type map */
    protected FileTypeMap fileTypeMap = FileTypeMapUtil.getDefaultFileTypeMap();

    /**
     * Constructor
     * @param parent parser handler
     */
    public GetContentType(XMLParserHandler parent) {
        super(parent);
    }

    /**
     * Returns file's content type consulting {@link FileTypeMap}
     * @param adapter adapter
     * @param resource a file
     * @return response property
     */
    public ResponseProperty processResponse(ResourceAdapter adapter, Object resource) {
        String contentType;
        Resource res = (Resource)resource;
        String filename = res.getFilename();
        contentType = fileTypeMap.getContentType(filename);
        return new org.abstracthorizon.danube.webdav.xml.dav.response.properties.GetContentType(contentType);
    }
}

