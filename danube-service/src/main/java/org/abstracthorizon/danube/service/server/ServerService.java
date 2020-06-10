/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.service.server;

import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.service.Service;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * This abstract cass introduces port for potential socket to listen to
 * and connection handler to be invoked with created connection.
 *
 * @author Daniel Sendula
 */
public abstract class ServerService extends Service {

    /** Socket address */
    protected InetSocketAddress socketAddress;

    /** Connection handler new connection to be handed to */
    protected ConnectionHandler connectionHandler;

    /** Port */
    protected int port = -1;
    
    /**
     * Default constructor
     */
    public ServerService() {
    }

    /**
     * Returns the port service is expecting connections on
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port. It has to be set before {@link #create()} method is called.
     * @param port the port
     */
    public void setPort(int port) {
        this.port = port;
        if (port < -1) {
            port = -port;
        }
        if (socketAddress == null) {
            socketAddress = new InetSocketAddress(port);
        } else {
            String ip = socketAddress.getAddress().getHostAddress();
            socketAddress = new InetSocketAddress(ip, port);
        }
    }

    public void setAddress(String address) {
        int port = getPort();
        if (port < -1) {
            port = - port;
        }
        socketAddress = new InetSocketAddress(address, port);
    }

    public String getAddress() {
        if (socketAddress == null) {
            return "0.0.0.0";
        } else {
            InetAddress inetAddress = socketAddress.getAddress();
            if (inetAddress == null) {
                return "0.0.0.0";
            } else {
                return inetAddress.getHostAddress();
            }
        }
    }

    public void setSocketAddress(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
        this.port = socketAddress.getPort();
    }

    public InetSocketAddress getSocketAddress() {
        if (socketAddress == null) {
            socketAddress = new InetSocketAddress("0.0.0.0", 0);
        }
        return socketAddress;
    }

    /**
     * Returns connection handler connections are handed to.
     * @return connection handler
     */
    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    /**
     * Sets connection handler
     * @param connectionHandler connection handler
     */
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

}
