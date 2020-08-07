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
package org.abstracthorizon.danube.webdav.protocols.webdav;

import java.io.IOException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 * URL connection for WebDAV
 *
 * @author Daniel Sendula
 */
@SuppressWarnings("restriction")
public class WebDAVURLConnection extends HttpURLConnection {

    protected WebDAVURLConnection(URL u, Proxy p, Handler handler) throws IOException {
        super(u, p, handler);
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        if (connected) {
            throw new ProtocolException("Can't reset method: already connected");
        }
        this.method = method;
    }

}
