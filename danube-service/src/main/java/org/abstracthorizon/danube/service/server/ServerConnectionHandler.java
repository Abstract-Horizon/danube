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
package org.abstracthorizon.danube.service.server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.net.Socket;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionException;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.support.logging.LoggingConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is entry point for typical server that handles request
 * sequentially.
 *
 * @author Daniel Sendula
 */
public class ServerConnectionHandler implements ConnectionHandler {

    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Connection Handler */
    protected ConnectionHandler connectionHandler;

    /** Constructor */
    public ServerConnectionHandler() {
    }

    /**
     * Sets connection handler
     * @param connectionHandler
     */
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * Returns connection handler
     * @return connection handler
     */
    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    /**
     * This template method allows connection to be wrapped and then
     * processes sequential requests/commands while socket is open or
     * an exception occurs.
     *
     * @param connection socket connection
     * @throws ConnectionException
     */
    public void handleConnection(Connection connection) throws ConnectionException {
        boolean closedConnection = false;
        Connection decoratedConnection = decorateConnection(connection);
        try {

            boolean persistConnection = true;
            while (persistConnection) {
                try {
                    processConnection(decoratedConnection);
                } catch (RuntimeException e) {
                    Throwable w = e.getCause();
                    if (w == null) {
                        w = e;
                    }
                    throw w;
                }

                // TODO this doesn't have to be a socket connection!
                persistConnection = postProcessing(decoratedConnection);
            }
        } catch (InterruptedIOException e) {
            LoggingConnection loggingConnection = (LoggingConnection)decoratedConnection.adapt(LoggingConnection.class);
            if (loggingConnection != null) {
                loggingConnection.setTemporaryLog(false);
                PrintStream out = new PrintStream(loggingConnection.getDebugOutputStream());
                out.println("Closing because of inactivity");
                out.flush();
            }

        } catch (IOException e) {
            closeConnection(decoratedConnection);
            closedConnection = true;
            // Don't log sudden and proper stream closes.
        } catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("Got problem: ", t);
            }
            closeConnection(decoratedConnection);
            closedConnection = true;
        } finally {
            finishProcessingConnection(decoratedConnection, closedConnection);
        }
    }

    /**
     * Template method for processing of connection. Override it to perform what is needed.
     * This method invokes given connection handler
     *
     * @param connection decorated
     */
    protected void processConnection(Connection connection) {
        getConnectionHandler().handleConnection(connection);
    }

    /**
     * This method can &quot;decorate&quot; connection wrapping it with own. This
     * implementation does nothing
     *
     * @param connection original conneciton
     *
     * @return decorated connection
     */
    protected Connection decorateConnection(Connection connection) {
        return connection;
    }

    /**
     * Post processing that should determine shall we persist connection (invoke handler
     * again) or leave connection handler.
     *
     * @param connection decorated connection
     * @return <code>true</code> if we should persist connection
     */
    protected boolean postProcessing(Connection connection) {
        boolean persistConnection = true;
        Socket socket = (Socket)connection.adapt(Socket.class);
        if (socket != null) {
            persistConnection = (socket != null) && !socket.isInputShutdown() && !socket.isOutputShutdown() && !socket.isClosed();
        }

        return persistConnection;
    }

    /**
     * Closes connection. This method is called if there was an error and connection should not
     * remain open. Can be overridden if something else needs to be performed before or after closing connection
     * or to prevent closing connection.
     * @param connection decorated connection
     */
    protected void closeConnection(Connection connection) {
        connection.close();
    }

    /**
     * Finishes connection for this server connection handler. This method can do some last minute
     * clean up on decorated connection.
     *
     * @param connection decorated connection
     * @param closedConnection has {@link #closeConnection(Connection)} method called before
     */
    protected void finishProcessingConnection(Connection connection, boolean closedConnection) {
        closeConnection(connection);
    }
}
