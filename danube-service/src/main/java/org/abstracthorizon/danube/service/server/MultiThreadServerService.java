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
 * This class models multi-threaded model of connection service. Each new connection is
 * spawned in a separate, possibly new, thread obtained from given executor. Connections
 * passed to the thread must be of {@link ServerConnection} type in order for close method
 * to be called at the end of processing.
 *
 * @author Daniel Sendula
 */
public abstract class MultiThreadServerService extends ServerService {

    /** Grace period for connections to finish after service state changes to STOPPING */
    protected int graceFinishPeriod = 2000;

    /** Executor (thread pool) to be used */
    protected Executor executor;

    /** Set of active connections */
    private Set<ConnectionHandlerThread> activeConnections = new HashSet<ConnectionHandlerThread>();

    /**
     * Default constructor
     */
    public MultiThreadServerService() {
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
     * Returns active connections
     * @return connections
     */
    public Set<ConnectionHandlerThread> getActiveConnections() {
        Set<ConnectionHandlerThread> activeConnections;
        synchronized (this) {
            activeConnections = new HashSet<>(this.activeConnections);
        }

        return activeConnections;
    }

    /**
     * Returns number of active connections
     * @return number of active connections
     */
    public int getNumberOfActiveConnections() {
        synchronized (this) {
            if (activeConnections != null) {
                return activeConnections.size();
            } else {
                return 0;
            }
        }
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
        // TODO We have java.util.ConcurrentModificationException on following iteration
        for (ConnectionHandlerThread thread : getActiveConnections()) {
            Connection serverConnection = thread.getConnection();
            if (!serverConnection.isClosed()) {
                serverConnection.close();
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
     * Accepts connections from processes them
     */
    public void run() {
        try {
            changeState(RUNNING);
            if (logger.isInfoEnabled()) {
                logger.info("Started service " + getName() + " on the address " + getAddress() + ":" + getPort());
            }

            while (!stopService) {
                processConnections();
            }
        } finally {
            changeState(STOPPED);
        }
    }

    /**
     * This method processes connections
     */
    protected abstract void processConnections();

    /**
     * This class is executed in under the given executor. It serves to keep
     * reference to server connection and enclosing {@link MultiThreadServerService} instance.
     */
    protected class ConnectionHandlerThread implements Runnable {

        /** Server connection */
        protected Connection serverConnection;

        /** Current thread */
        protected Thread thread;

        /**
         * Constructor
         * @param serverConnection server connection
         */
        public ConnectionHandlerThread(Connection serverConnection) {
            this.serverConnection = serverConnection;
        }

        /**
         * Returns server connection
         * @return server connection
         */
        public Connection getConnection() {
            return serverConnection;
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
                synchronized (this) {
                    activeConnections.add(this);
                }
                connectionHandler.handleConnection(serverConnection);
            } catch (Exception e) {
                logger.error("Connection finished with error; " + serverConnection, e);
            } finally {
                synchronized (this) {
                    activeConnections.remove(this);
                }
                serverConnection.close();
            }
        }
    }
}
