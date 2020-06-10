/*
 * Copyright (c) 2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.support;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class that deals for URLs
 *
 * @author Daniel Sendula
 */
public class URLUtils {

    /**
     * Adds a path to existing URL
     * @param url an URL for path to be added to
     * @param path path to be added to URL
     * @return new URL
     * @throws MalformedURLException
     */
    public static URL addPath(URL url, String path) throws MalformedURLException {

        String file = url.getFile();
        if (file.endsWith("/")) {
            if (path.startsWith("/")) {
                file = file + path.substring(1);
            } else {
                file = file + path;
            }
        } else if (path.startsWith("/")) {
            file = file + path;
        } else {
            file = file + "/" + path;
        }


        URL newURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), file);

        return newURL;
    }

}
