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

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.service.ServiceException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;

/**
 * This is server service that is implemented using socket channels.
 * This service works in blocked IO mode and accepts connections from a
 * server socket channel, creates new {@link ConnectionHandlerThread} objects
 * and executes them in a given thread by {@link Executor}.
 *
 * @author Daniel Sendula
 */
public class MultiThreadServerSocketChannelService extends MultiThreadServerService {

    /** Initial socket timeout */
    protected int serverSocketTimeout = 1000;

    // TODO implement this!!!
    /** New socket timeout */
    protected int newSocketTimeout = 60000;

    /** Reference to the server socket */
    protected ServerSocketChannel serverSocketChannel;

    /**
     * Default constructor
     */
    public MultiThreadServerSocketChannelService() {
    }

    /**
     * Returns server socket timeout
     * @return server socket timeout
     */
    public int getServerSocketTimeout() {
        return serverSocketTimeout;
    }

    /**
     * Sets initial socket timeout
     * @param socketTimeout initial socket timeout
     */
    public void setServerSocketTimeout(int socketTimeout) {
        this.serverSocketTimeout = socketTimeout;
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
        super.destroy();
        destroyServerSocket();
    }

    /**
     * This method processes connections
     */
    protected void processConnections() {
        try {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (logger.isDebugEnabled()) { logger.debug("Accepted new connection;  " + socketChannel.socket()); }

            try {
                Connection serverConnection = createSocketConnection(socketChannel);
                ConnectionHandlerThread connectionHandlerThread = new ConnectionHandlerThread(serverConnection);
                connectionHandlerThread.start();
            } catch (Exception e) {
                logger.error("Cannot process connection for socket; " + socketChannel.socket(), e);
            }
        } catch (IOException ignore) {
        }
    }

    /**
     * Creates server socket
     * @return server socket
     * @throws ServiceException
     */
    protected void createServerSocket()throws ServiceException {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(true);
            InetSocketAddress socketAddress = getSocketAddress();
            serverSocketChannel.socket().bind(socketAddress);
            serverSocketChannel.socket().setSoTimeout(getServerSocketTimeout());
        } catch (IOException e) {
            throw new ServiceException("Problem creating server socket", e);
        }
    }

    /**
     * Closes server socket
     * @throws ServiceException
     */
    protected void destroyServerSocket() throws ServiceException {
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            throw new ServiceException("Problem closing server socket", e);
        }
    }

    /**
     * Creates socket connection and new instance of {@link ConnectionHandlerThread} to process
     * socket under the given executor
     * @param socketChannel socket channel
     */
    protected void processConnection(SocketChannel socketChannel) {
        if (logger.isDebugEnabled()) { logger.debug("Accepted new connection;  " + socketChannel.socket()); }

        try {
            Connection socketConnection = createSocketConnection(socketChannel);
            ConnectionHandlerThread connectionHandlerThread = new ConnectionHandlerThread(socketConnection);
            connectionHandlerThread.start();
        } catch (Exception e) {
            logger.error("Cannot process connection for socket; " + socketChannel.socket(), e);
        }
    }

    /**
     * Creates new socket connection
     * @param socketChannel socket channel
     * @return server connection
     * @throws IOException
     * @throws Exception
     */
    protected Connection createSocketConnection(SocketChannel socketChannel) throws IOException {
        Connection serverConnection = new SocketChannelConnection(socketChannel);
        return serverConnection;
    }
}
