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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.service.ServiceException;

/**
 * This is server service that is implemented using sockets. This service accepts connections from a
 * server socket, creates new {@link ConnectionHandlerThread} objects
 * and executes them in a given thread by {@link Executor}.
 *
 * @author Daniel Sendula
 */
public class MultiThreadServerSocketService extends MultiThreadServerService {

    /** Initial socket timeout */
    protected int serverSocketTimeout = 1000;

    /** New socket's timeout */
    protected int newSocketTimeout = 60000;

    /** Reference to the server socket */
    protected ServerSocket serverSocket;

    /**
     * Default constructor
     */
    public MultiThreadServerSocketService() {
        waitForStateTimeout = serverSocketTimeout * 2;
    }

    /**
     * Returns initial socket timeout
     * @return initial socket timeout
     */
    public int getServerSocketTimeout() {
        return serverSocketTimeout;
    }

    /**
     * Sets server socket timeout
     * @param socketTimeout initial socket timeout
     */
    public void setServerSocketTimeout(int socketTimeout) {
        this.serverSocketTimeout = socketTimeout;
        waitForStateTimeout = serverSocketTimeout * 2;
    }

    /**
     * Returns new socket timeout
     * @return new socket timeout
     */
    public int getNewSocketTimeout() {
        return newSocketTimeout;
    }

    /**
     * Sets new socket timeout
     * @param socketTimeout new socket timeout
     */
    public void setNewSocketTimeout(int socketTimeout) {
        this.newSocketTimeout = socketTimeout;
    }

    /**
     * Creates the socket
     * @throws ServiceException
     */
    public void create() throws ServiceException {
        super.create();
        createServerSocket();
    }

    /**
     * Closes the server socket
     * @throws ServiceException
     */
    public void destroy() throws ServiceException {
        try {
            serverSocket.close();
        } catch (IOException ignore) { }
        super.destroy();
        destroyServerSocket();
    }

    /**
     * This method processes connections
     */
    protected void processConnections() {
        try {
            Socket socket = serverSocket.accept();
            try {
                processNewlyAcceptedSocket(socket);
            } catch (Exception e) {
                logger.error("Cannot process connection for socket; " + socket, e);
            }
        } catch (IOException ignore) {
            // ignore.printStackTrace();
        }
    }

    protected void processNewlyAcceptedSocket(Socket socket) throws IOException {
        if (logger.isDebugEnabled()) { logger.debug("Accepted new connection;  " + socket.toString()); }

        socket.setSoTimeout(getNewSocketTimeout());
        Connection serverConnection = createSocketConnection(socket);
        ConnectionHandlerThread connectionHandlerThread = new ConnectionHandlerThread(serverConnection);
        connectionHandlerThread.start();
    }

    /**
     * Creates server socket
     * @return server socket
     * @throws ServiceException
     */
    protected void createServerSocket()throws ServiceException {
        try {
            int port = getPort();
            if (port == -1) {
                serverSocket = new ServerSocket(0, 0, getSocketAddress().getAddress());
                InetSocketAddress socketAddress = (InetSocketAddress)serverSocket.getLocalSocketAddress();
                setSocketAddress(socketAddress);
            } else if (port < -1) {
                int i = 0;
                IOException exception = null;
                while ((i < 20) && (serverSocket == null)) {
                    try {
                        serverSocket = new ServerSocket(-port, 0, getSocketAddress().getAddress());
                    } catch (IOException ex) {
                        exception = ex;
                        port = port - 1;
                        setPort(port);
                    }
                    i++;
                }
                if (serverSocket == null) {
                    if (exception != null) {
                        throw exception;
                    } else {
                        throw new ServiceException("Problem creating server socket " + getSocketAddress());
                    }
                }
            } else {
                serverSocket = new ServerSocket(port, 0, getSocketAddress().getAddress());
            }
            serverSocket.setSoTimeout(getServerSocketTimeout());
        } catch (IOException e) {
            throw new ServiceException("Problem creating server socket " + getSocketAddress(), e);
        }
    }

    /**
     * Closes server socket
     * @throws ServiceException
     */
    protected void destroyServerSocket() throws ServiceException {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new ServiceException("Problem closing server socket " + getSocketAddress(), e);
        }
    }

    /**
     * Creates server connection and new instance of {@link ConnectionHandlerThread} to process
     * socket under the given executor
     * @param socket socket
     */
    protected void processConnection(Socket socket) {
        if (logger.isDebugEnabled()) { logger.debug("Accepted new connection;  " + socket.toString()); }

        try {
            Connection serverConnection = createSocketConnection(socket);
            ConnectionHandlerThread connectionHandlerThread = new ConnectionHandlerThread(serverConnection);
            connectionHandlerThread.start();
        } catch (Exception e) {
            logger.error("Cannot process connection for socket; " + socket, e);
        }
    }

    /**
     * Creates new socket connection
     * @param socket socket
     * @return socket connection
     * @throws IOException
     * @throws Exception
     */
    protected Connection createSocketConnection(Socket socket) throws IOException {
        Connection serverConnection = new SocketConnection(socket);
        return serverConnection;
    }
}
