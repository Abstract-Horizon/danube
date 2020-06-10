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
package org.abstracthorizon.danube.webdav.protocols.webdav;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;

/**
 * Test handler for webdav protocol
 *
 * @author Daniel Sendula
 */
public class Handler extends sun.net.www.protocol.http.Handler {


    public Handler () {
    }

    public Handler (String proxy, int port) {
        super(proxy, port);
    }

    protected java.net.URLConnection openConnection(URL u, Proxy p) throws IOException {
        return new WebDAVURLConnection(u, p, this);
    }

}
