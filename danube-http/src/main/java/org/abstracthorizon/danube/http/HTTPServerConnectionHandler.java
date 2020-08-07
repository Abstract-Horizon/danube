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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.util.ErrorConnectionHandler;
import org.abstracthorizon.danube.http.util.MultiStringMap;
import org.abstracthorizon.danube.service.server.ServerConnectionHandler;
import org.abstracthorizon.danube.support.RuntimeIOException;

/**
 * This class is entry point for HTTP server.
 * It extends {@link org.abstracthorizon.danube.http.Selector} class adding
 * &quot;Server&quot; header and handles HTTP/1.1 &quot;Connection: keep-alive&quot;
 * (multiple requests over singe socket connection.
 *
 * @author Daniel Sendula
 */
public class HTTPServerConnectionHandler extends ServerConnectionHandler {

    /** Version string */
    public static final String VERSION_STRING = "1.0";

    /** Full version string */
    public static final String FULL_VERSION_STRING = "Danube/" + VERSION_STRING;

    /** RFC822 date format */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    /** Default buffer size of 8Kb */
    public static final int DEFAULT_BUFFER_SIZE = 1024*8; // 8Kb

    /** Error handler */
    protected ConnectionHandler errorHandler;

    /** Default buffer size */
    protected int defaultBufferSize = DEFAULT_BUFFER_SIZE;

    /** Constructor */
    public HTTPServerConnectionHandler() {
    }

    /**
     * Returns generic error handler
     * @return generic error handler
     */
    public ConnectionHandler getErrorHandler() {
        if (errorHandler == null) {
            errorHandler = new ErrorConnectionHandler();
        }
        return errorHandler;
    }

    /**
     * Sets generic error handler
     * @param errorHandler generic error handler
     */
    public void setErrorHandler(ConnectionHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    /**
     * Returns default buffer size
     * @return default buffer size
     */
    public int getDefaultBufferSize() {
        return defaultBufferSize;
    }

    /**
     * Sets default buffer size
     * @param defaultBufferSize default buffer size
     */
    public void setDefaultBufferSize(int defaultBufferSize) {
        this.defaultBufferSize = defaultBufferSize;
    }

    /**
     * Processes connection
     *
     * @param httpConnection http connection to be processed
     * @throws IOException
     */
    protected void processConnection(Connection connection) {
        HTTPConnectionImpl httpConnection = (HTTPConnectionImpl)connection;
        try {
            try {
                httpConnection.processRequest();
            } catch (EOFException eof) {
                throw eof;
            } catch (IOException e) {
                throw new EOFException(e.getMessage());
            }

            MultiStringMap headers = httpConnection.getResponseHeaders();

            headers.putOnly("Server", FULL_VERSION_STRING);

            // This header should be present so presetting it to text/html should not make any harm
            headers.putOnly("Content-Type", "text/html");

            // Mandatory field
            headers.putOnly("Date", DATE_FORMAT.format(new Date()));

            try {
                connectionHandler.handleConnection(httpConnection);
                // Ensure that all output to the user is commited
                OutputStream out = (OutputStream)httpConnection.adapt(OutputStream.class);
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            } catch (Throwable e) {
                Socket socket = (Socket)httpConnection.adapt(Socket.class);
                if ((socket != null) && (socket.isConnected() && !socket.isClosed() && !socket.isInputShutdown() && !socket.isOutputShutdown())) {
                    httpConnection.setResponseStatus(Status.INTERNAL_SERVER_ERROR);
                    ConnectionHandler connectionHandler = getErrorHandler();
                    httpConnection.getAttributes().put("_exception", e);
                    connectionHandler.handleConnection(httpConnection);
                } else {
                   throw new EOFException();
                }
            } finally {
                InputStream in = (InputStream)httpConnection.adapt(InputStream.class);
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    protected Connection decorateConnection(Connection connection) {
        return new HTTPConnectionImpl(connection, this, getDefaultBufferSize());
    }

    protected boolean postProcessing(Connection connection, boolean persistConnection) {
        HTTPConnectionImpl httpConnection = (HTTPConnectionImpl)connection;
        MultiStringMap headers = httpConnection.getResponseHeaders();

        if (persistConnection) {
            String code = httpConnection.getResponseStatus().getCode();
            // If response doesn't start with 1xx and is not 204 and 304
            // and there is no Content-Length present then only way to determine size of
            // message is for server to drop connection.

            // TODO Transfer-Encoding has different way of defining size of the message
            // it is not implemented here!
            if (!code.startsWith("1")
                    && !"204".equals(code)
                    && !"304".equals(code)
                    && !headers.containsKey("Content-Length")
                    && !"chunked".equals(headers.getOnly("Transfer-Encoding"))) {

                persistConnection = false;
            }
        }

        if (persistConnection) {
            if ("HTTP/1.1".equals(httpConnection.getRequestProtocol())) {
                try {
                    if ("Close".equalsIgnoreCase(headers.getOnly("Connection"))) {
                        persistConnection = false;
                    }
                } catch (IllegalStateException ignore) {
                }
            } else {
                try {
                    if (!"Keep-Alive".equalsIgnoreCase(headers.getOnly("Connection"))) {
                        persistConnection = false;
                    }
                } catch (IllegalStateException ignore) {
                }
            }
        }
        return persistConnection;
    }

    protected void finishConnection(Connection connnection) {
    }

}
