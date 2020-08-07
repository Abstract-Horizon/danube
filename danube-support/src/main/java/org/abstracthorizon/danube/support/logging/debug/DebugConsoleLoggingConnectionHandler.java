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
package org.abstracthorizon.danube.support.logging.debug;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.support.logging.LoggingConnection;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connection handler that logs connection to a file.
 *
 * @author Daniel Sendula
 */
public class DebugConsoleLoggingConnectionHandler implements ConnectionHandler {

    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Connection handler connection to be passed to */
    private ConnectionHandler connectionHandler;

    /** Is logging switched on or off */
    private boolean logging = true;

    /** Is it direction/readable text logging or not */
    private boolean directional = true;

    /** Client socket address pattern string */
    private String addressPatternString;

    /** Client socket address pattern */
    protected Pattern addressPattern;

    /** Should remote host names be resolved for address pattern */
    private boolean resolveRemoteHostNames = false;

    /**
     * Constructor
     */
    public DebugConsoleLoggingConnectionHandler() {
        setAddressPattern(".*");
    }

    /**
     * Returns address pattern.
     *
     * @return returns address pattern.
     */
    public String getAddressPattern() {
        return addressPatternString;
    }

    /**
     * Sets socket address pattern. Only socket host addresses (or names, see {@link #setResolveRemoteHostNames(boolean))
     * that match this pattern will create log files or temporary log files.
     *
     *
     * @param addressPatternString
     */
    public void setAddressPattern(String addressPatternString) {
        this.addressPatternString = addressPatternString;
        this.addressPattern = Pattern.compile(addressPatternString);
    }

    /**
     * Returns connection handler
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

    /**
     * Is logging directional or not
     * @return if logging is directional
     */
    public boolean isDirectional() {
        return directional;
    }

    /**
     * Sets for logging to be directional or not
     * @param directional is logging directional or not
     */
    public void setDirectional(boolean directional) {
        this.directional = directional;
    }

    /**
     * Returns if logging is switched on or off. If it is switched off
     * no logging will occur for current connection
     *
     * @return if logging is switched on or off
     */
    public boolean isLogging() {
        return logging;
    }

    /**
     * Switches logging on or off
     * @param logging <code>true</code> if logging is to be switched on
     */
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    /**
     * Returns if host names should be resolved or not. It is used in
     * matching remote socket address ({@link #setAddressPattern(String)})
     *
     * @return if host names should be resolved or not.
     */
    public boolean isResolveRemoteHostNames() {
        return resolveRemoteHostNames;
    }

    /**
     * Sets if remote host names are to be resolved or not. It is used in
     * matching remote socket address ({@link #setAddressPattern(String)})
     *
     * @param resolveRemoteHostNames
     */
    public void setResolveRemoteHostNames(boolean resolveRemoteHostNames) {
        this.resolveRemoteHostNames = resolveRemoteHostNames;
    }

    /**
     * This method wrapps connection to logging connection and passes it further.
     * Will connection be wrapped or not depetns on
     * {@link #isLogging()}, {@link #getAddressPattern()} and {@link #isTempLogging()}.
     *
     * @param connection original connection
     */
    public void handleConnection(Connection connection) {
        boolean log = isLogging();
        boolean temporary = false;
        if (log) {
            Socket socket = (Socket)connection.adapt(Socket.class);
            if (socket != null) {
                String remoteHost = null;
                InetAddress remoteAddress = ((InetSocketAddress)socket.getRemoteSocketAddress()).getAddress();
                if (isResolveRemoteHostNames()) {
                    remoteHost = remoteAddress.getHostName();
                } else {
                    remoteHost = remoteAddress.getHostAddress();
                }
                log = addressPattern.matcher(remoteHost).matches();
            }
        }

        if (log) {
            LoggingConnection loggingConnection = new LoggingConnection(connection, System.out, directional, temporary);
            loggingConnection.setTemporaryLog(temporary);
            connectionHandler.handleConnection(loggingConnection);
        } else {
            connectionHandler.handleConnection(connection);
        }
    }
}
