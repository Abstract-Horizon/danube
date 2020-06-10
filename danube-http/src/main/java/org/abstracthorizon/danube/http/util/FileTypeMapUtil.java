/*
 * Copyright (c) 2005-2019 Creative Sphere Limited.
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
package org.abstracthorizon.danube.http.util;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

/**
 * Class to provide creating default file type map
 *
 * @author daniel
 *
 */
public class FileTypeMapUtil {

    private FileTypeMapUtil() {}

    public static FileTypeMap getDefaultFileTypeMap() {
        FileTypeMap fileTypeMap = FileTypeMap.getDefaultFileTypeMap();

        if (fileTypeMap instanceof MimetypesFileTypeMap) {
            MimetypesFileTypeMap mimetypesFileTypeMap = (MimetypesFileTypeMap) fileTypeMap;

            mimetypesFileTypeMap.addMimeTypes("text/css css CSS Css");
            mimetypesFileTypeMap.addMimeTypes("image/png png");
        }

        return fileTypeMap;
    }

    public static void main(String[] args) throws Exception {
        FileTypeMap fileTypeMap = getDefaultFileTypeMap();

        System.out.println("File type map " + fileTypeMap.toString());

        String mime = fileTypeMap.getContentType("styles.css");

        System.out.println("Mime type for .css " + mime);
    }

}
