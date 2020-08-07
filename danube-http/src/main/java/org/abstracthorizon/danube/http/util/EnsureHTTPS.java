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
package org.abstracthorizon.danube.http.util;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.Status;

import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This wrapper forces JAAS authentication to happen at client side:
 * if &quot;Authorization&quot; header is missing it would return 401
 * code requesting one.
 *
 * @author Daniel Sendula
 */
public class EnsureHTTPS implements ConnectionHandler {

    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(EnsureHTTPS.class);

    public static final String LOCATION_RESPONSE_HEADER = "Location";

    /** Wrapped handler */
    protected ConnectionHandler handler;

    /** HTTPS port - default 443 */
    protected int port = 443;

    /**
     * Constructor
     */
    public EnsureHTTPS() {
    }

    /**
     * Constructor
     */
    public EnsureHTTPS(ConnectionHandler handler) {
        setHandler(handler);
    }

    /**
     * This method creates sets context path to be same as context path
     * up to here plus this component's path. Component's path is reset
     * to &quot;<code>/<code>&quot;
     *
     * @param connection socket connection
     */
    public void handleConnection(final Connection connection) {
        if (((Socket)connection.adapt(Socket.class)) instanceof SSLSocket) {
            handler.handleConnection(connection);
        } else {
            HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
            String host = httpConnection.getRequestHeaders().getOnly("Host");
            if (host != null) {
                int i = host.indexOf(':');
                if (i >= 0) {
                    host = host.substring(0, i);
                }
            } else {
                InetSocketAddress socketAddress = (InetSocketAddress)((Socket)httpConnection.adapt(Socket.class)).getLocalSocketAddress();
                host = socketAddress.getHostName();
            }
            String uri = "https://" + host + ":" + port + httpConnection.getRequestURI();
            httpConnection.setResponseStatus(Status.MOVED_PERMANENTLY);
            httpConnection.getResponseHeaders().putOnly(LOCATION_RESPONSE_HEADER, uri);
        }
    }


    /**
     * Returns wrapped handler
     * @return wrapped handler
     */
    public ConnectionHandler getHandler() {
        return handler;
    }

    /**
     * Sets wrapped handler
     * @param handler wrapped handler
     */
    public void setHandler(ConnectionHandler handler) {
        this.handler = handler;
    }

    /**
     * Returns https port
     * @return https port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets https port
     * @param port https port
     */
    public void setPort(int port) {
        this.port = port;
    }
}
