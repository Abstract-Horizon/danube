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
package org.abstracthorizon.danube.support.logging.patternsupport;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.support.logging.util.StringUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * This processor handles following parameters (if present):
 * <ul>
 * <li><code>%A</code> - local IP address</li>
 * <li><code>%a</code> - remote IP address</li>
 * <li><code>%p</code> - local port number</li>
 * <li><code>%h</code> - remote host name</li>
 * </ul>
 *
 * @author Daniel Sendula
 */
public class SocketDetailsProcessor implements PatternProcessor {

    /** Cached index of local ip address parameter */
    protected int localIPIndex = -1;

    /** Cached index of local port number parameter */
    protected int localPortIndex = -1;

    /** Cached index of remote ip address parameter */
    protected int remoteIPIndex = -1;

    /** Cached index of remote host name parameter */
    protected int remoteHostNameIndex = -1;

    /**
     * Constructor
     */
    public SocketDetailsProcessor() {
    }

    /**
     * Checks if parameters are present and if so replaces it and caches their indexes
     * @param index next index to be used
     * @param message message to be altered
     */
    public int init(int index, StringBuffer message) {
        localIPIndex = -1;
        localPortIndex = -1;
        remoteIPIndex = -1;
        remoteHostNameIndex = -1;

        if (message.indexOf("%A") >= 0) {
            StringUtil.replaceAll(message, "%A", "{" + index + "}");
            localIPIndex = index;
            index = index + 1;
        }
        if (message.indexOf("%a") >= 0) {
            StringUtil.replaceAll(message, "%a", "{" + index + "}");
            remoteIPIndex = index;
            index = index + 1;
        }
        if (message.indexOf("%p") >= 0) {
            StringUtil.replaceAll(message, "%p", "{" + index + "}");
            localPortIndex = index;
            index = index + 1;
        }
        if (message.indexOf("%h") >= 0) {
            StringUtil.replaceAll(message, "%h", "{" + index + "}");
            remoteHostNameIndex = index;
            index = index + 1;
        }
        return index;
    }

    /**
     * Adds parameter values to cached index positions
     * @param connection connection
     * @param array array
     */
    public void process(Connection connection, Object[] array) {
        Socket socket = (Socket)connection.adapt(Socket.class);
        String localIP = socket.getLocalAddress().getHostAddress();
        String localPort = Integer.toString(socket.getLocalPort());
        InetSocketAddress inetSocketAddress = (InetSocketAddress)socket.getRemoteSocketAddress();
        InetAddress inetAddress = inetSocketAddress.getAddress();
        String remoteIP = inetAddress.getHostAddress();
        String remoteHostName = inetAddress.getHostName();
        if (localIPIndex >= 0) {
            array[localIPIndex] = localIP;
        }
        if (localPortIndex >= 0) {
            array[localPortIndex] = localPort;
        }
        if (remoteIPIndex >= 0) {
            array[remoteIPIndex] = remoteIP;
        }
        if (remoteHostNameIndex >= 0) {
            array[remoteHostNameIndex] = remoteHostName;
        }
    }



}
