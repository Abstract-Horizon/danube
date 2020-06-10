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
package org.abstracthorizon.danube.webdav.util;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.webdav.ResourceAdapter;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * URI utility methods
 *
 * @author Daniel Sendula
 */
public class URIUtils {

    /**
     * Returns resource from the given URI
     * @param httpConnection connection
     * @param adapter resource adapter
     * @param uriString URI
     * @return an object or <code>null</code>
     */
    public static Object uriToResource(HTTPConnection httpConnection, ResourceAdapter adapter, String uriString) {
        URI uri;
        try {

            uri = new URI(uriString);

            if (uri.getScheme() != null) {
                // TODO check for scheme

                if (uri.getPort() > 0) {
                    Socket socket = (Socket)httpConnection.adapt(Socket.class);
                    if ((socket != null) && (uri.getPort() >= 0) && (socket.getLocalPort() != uri.getPort())) {
                        return null;
                    }
                }
                // TODO check for host name too

                String path = uri.getPath();
                String p = IOUtils.addPaths(httpConnection.getContextPath(), httpConnection.getComponentPath());
                if (!path.startsWith(p)) {
                    return null;
                }

                return adapter.findResource(path.substring(p.length()));
            } else {
                return adapter.findResource(uriString);
            }
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
