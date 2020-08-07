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
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.service.Service;
import org.abstracthorizon.danube.service.ServiceException;
import org.abstracthorizon.danube.service.ServiceNotificationListener;

/**
 * This is server socket service. This service accepts connections from a
 * server socket, creates new {@link ConnectionHandlerThread} objects
 * and executes them in a given {@link Executor}.
 *
 * @author Daniel Sendula
 */
public class ServerSocketChannelService extends Service {

    /** Reference to the server socket */
    protected ServerSocketChannel serverSocketChannel;

    /** Port this service is going to listen on */
    protected int port = -1;

    /** Server socket timeout */
    protected int serverSocketTimeout = 1000;

    /** New socket timeout */
    protected int newSocketTimeout = 60000;

    /** Executor (thread pool) to be used */
    protected Executor executor;

    /** Set of active connections */
    protected Set<ConnectionHandlerThread> activeConnections = new HashSet<ConnectionHandlerThread>();

    /** Connection handler new connection to be handed with */
    protected ConnectionHandler connectionHandler;

    /** Grace period for connections to finish after service state changes to STOPPING */
    protected int graceFinishPeriod = 2000;

    /**
     * Default constructor
     */
    public ServerSocketChannelService() {
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
    }

    /**
     * Returns initial socket timeout
     * @return initial socket timeout
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
     * Returns grace finish period
     * @return grace finish period
     */
    public int getGraceFinishPeriod() {
        return graceFinishPeriod;
    }

    /**
     * Sets grace finish period
     * @param graceFinishPeriod grace finish period
     */
    public void setGraceFinishPeriod(int graceFinishPeriod) {
        this.graceFinishPeriod = graceFinishPeriod;
    }

    /**
     * Return the executor which is used or connections to be handled with
     * @return the executor
     */
    public Executor getExecutor() {
        if (executor == null) {
            executor = Executors.newCachedThreadPool();
        }
        return executor;
    }

    /**
     * Sets the executor for connections to be handled with
     * @param executor
     */
    public void setExecutor(Executor executor) {
        this.executor = executor;
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

    /**
     * Creates the socket
     * @throws ServiceException
     */
    public void create() throws ServiceException {
        super.create();
        serverSocketChannel = createServerSocket();
    }

    /**
     * Starts the service
     * @throws ServiceException
     */
    public void start() throws ServiceException {
        super.start();
        Executor executor = getExecutor();
        executor.execute(this);
    }

    /**
     * Stops the service.
     * @throws ServiceException
     */
    public void stop() throws ServiceException {
        ServiceNotificationListener listener = new ServiceNotificationListener() {

            public void serviceAboutToChangeState(Service service, int newState) {
            }

            public synchronized void serviceChangedState(Service service, int oldState) {
                if (getState() == STOPPED) {
                    notifyAll();
                }
            }
        };
        addListener(listener);
        synchronized (listener) {
            super.stop();
            try {
                listener.wait(graceFinishPeriod);
            } catch (InterruptedException ignore) {
            }
        }
        removeListener(listener);
        for (ConnectionHandlerThread thread : activeConnections) {
            Connection socketConnection = thread.getConnection();
            try {
                SocketChannel socketChannel = (SocketChannel)socketConnection.adapt(SocketChannel.class);
                socketChannel.close();

                Socket socket = (Socket)socketConnection.adapt(Socket.class);
//                socket.shutdownInput();
//                socket.shutdownOutput();
//                socket.close();
                socket.setSoTimeout(10);
                synchronized (socket) { // TODO
                    socket.notifyAll();
                }
            } catch (Exception ignore) {
                // TODO remove
                ignore.printStackTrace();
            }
            try {
                thread.getThread().interrupt();
            } catch (Exception ignore) {
            }
        }

        if (executor instanceof ExecutorService) {
            ((ExecutorService)executor).shutdownNow();
        }
    }

    /**
     * Closes the server socket
     * @throws ServiceException
     */
    public void destroy() throws ServiceException {
        super.destroy();
        try {
            serverSocketChannel.close();
        } catch (IOException e) {
            throw new ServiceException("Problem closing server socket", e);
        }
    }

    /**
     * Accepts connections from server socket and calls {@link #processConnection(Socket)} method
     */
    public void run() {
        try {
            changeState(RUNNING);
            while (!stopService) {
                try {
                    SocketChannel socket = serverSocketChannel.accept();
                    processConnection(socket);
                } catch (IOException ignore) {
                }
            }
        } finally {
            changeState(STOPPED);
        }
    }


    /**
     * Creates server socket
     * @return server socket
     * @throws ServiceException
     */
    protected ServerSocketChannel createServerSocket()throws ServiceException {
        try {
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(true);
            serverSocket.socket().bind(new InetSocketAddress(port));
            serverSocket.socket().setSoTimeout(getServerSocketTimeout());
            return serverSocket;
        } catch (IOException e) {
            throw new ServiceException("Problem creating server socket", e);
        }
    }

    /**
     * Creates new socket connection
     * @param socket socket
     * @return socketChannel socket channel
     * @throws IOException
     * @throws Exception
     */
    protected Connection createSocketConnection(SocketChannel socketChannel) throws IOException {
        Connection socketConnection = new SocketChannelConnection(socketChannel);
        return socketConnection;
    }

    /**
     * Creates socket connection and new instance of {@link ConnectionHandlerThread} to process
     * socket under the given executor
     * @param socket socket
     */
    protected void processConnection(SocketChannel socket) {
        if (logger.isDebugEnabled()) { logger.debug("Accepted new connection;  " + socket.toString()); }

        try {
            socket.socket().setSoTimeout(getNewSocketTimeout());
            Connection socketConnection = createSocketConnection(socket);
            ConnectionHandlerThread connectionHandlerThread = new ConnectionHandlerThread(socketConnection);
            connectionHandlerThread.start();
        } catch (Exception e) {
            logger.error("Cannot process connection for socket; " + socket, e);
        }
    }

    /**
     * This class is executed in under the given executor. It serves to keep
     * reference to socket connection and enclosing {@link ServerSocketChannelService} instance.
     */
    public class ConnectionHandlerThread implements Runnable {

        /** Socket connection */
        protected Connection socketConnection;

        /** Current thread */
        protected Thread thread;

        /**
         * Constructor
         * @param socketConnection socket connection
         */
        public ConnectionHandlerThread(Connection socketConnection) {
            this.socketConnection = socketConnection;
        }

        /**
         * Returns socket connection
         * @return socket connection
         */
        public Connection getConnection() {
            return socketConnection;
        }

        /**
         * Returns thread
         * @return thread
         */
        public Thread getThread() {
            return thread;
        }

        /**
         * Gives this object to the executor for execution
         */
        public void start() {
            executor.execute(this);
        }

        /**
         * Handles connection invoking {@link ConnectionHandler#handleConnection(org.abstracthorizon.danube.connection.Connection)} method.
         */
        public void run() {
            try {
                thread = Thread.currentThread();
                activeConnections.add(this);
                connectionHandler.handleConnection(socketConnection);
            } catch (Exception e) {
                logger.error("Connection finished with error; " + socketConnection, e);
            } finally {
                activeConnections.remove(this);
                try {
                    ((Socket)socketConnection.adapt(Socket.class)).close();
                } catch (Exception ignore) {
                }
            }
        }
    }
}
