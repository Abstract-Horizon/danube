/*
 * Copyright (c) 2005-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.http;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.mvc.MVCConnectionHandler;

import java.io.IOException;

/**
 * This class represents MVC controller for HTTP. It just adds
 * no caching headers to http response.
 *
 * @author Daniel Sendula
 */
public class HTTPMVCConnectionHandler extends MVCConnectionHandler {

    /** Constructor */
    public HTTPMVCConnectionHandler() {
    }

    /**
     * This method just adds no caching headers to response
     * @param connection connection
     * @throws IOException if thrown by super {@link #handleConnection(Connection)}
     */
    public void handleConnection(Connection connection) {
        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
        httpConnection.getResponseHeaders().putOnly("Pragma", "No-cache");
        httpConnection.getResponseHeaders().putOnly("Cache-Control", "no-cache");
        httpConnection.getResponseHeaders().putOnly("Cache-Control", "no-store");
        super.handleConnection(httpConnection);
    }

}
