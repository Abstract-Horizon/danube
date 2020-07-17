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
package org.abstracthorizon.danube.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a Service that can run independently under some framework.
 *
 * @author Daniel Sendula
 *
 * @has 1 - n org.abstracthorizon.danube.service.ServiceNotificationListener
 */
public abstract class Service implements Runnable {

    public static final int NOT_INITIALIZED = 0;
    public static final int INITIALIZED = 1;
    public static final int STARTED = 2;
    public static final int RUNNING = 3;
    public static final int STOPPING = 4;
    public static final int STOPPED = 5;
    public static final int DESTROYED = 6;

    public static final String[] SERVICE_STATE_NAMES = new String[] {
        "NOT INSTALLED",
        "INITIALIZED",
        "STARTED",
        "RUNNING",
        "STOPPING",
        "STOPPED",
        "DESTROYED"
    };

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Service state */
    private int state = NOT_INITIALIZED;

    /** Flag to signal to service that it needs to stop */
    protected boolean stopService = false;

    /** Listeners */
    protected Set<ServiceNotificationListener> listeners;

    /** Service name */
    protected String name;

    /** Constructor */
    public Service() {
    }

    /**
     * Sets service's name
     * @param name service's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns service name
     * @return service name
     */
    public String getName() {
        return name;
    }

    /**
     * This method changes state. It notifies all registered listeners about the change.
     * @param newState new state
     */
    protected void changeState(int newState) {
        synchronized (this) {
            if (listeners != null) {
                Iterator<ServiceNotificationListener> it = listeners.iterator();
                while (it.hasNext()) {
                    ServiceNotificationListener listener = it.next();
                    listener.serviceAboutToChangeState(this, newState);
                }
            }
            int oldState = state;
            if (logger.isDebugEnabled()) {
                String name = getName();
                if (name != null) {
                    logger.debug("Changed service " + getName() + " state from " + SERVICE_STATE_NAMES[oldState] + " to " + SERVICE_STATE_NAMES[newState]);
                } else {
                    logger.debug("Changed unnamed service state from " + SERVICE_STATE_NAMES[oldState] + " to " + SERVICE_STATE_NAMES[newState]);
                }
            }
            state = newState;
            if (listeners != null) {
                Iterator<ServiceNotificationListener> it = listeners.iterator();
                while (it.hasNext()) {
                    ServiceNotificationListener listener = it.next();
                    listener.serviceChangedState(this, oldState);
                }
            }
            state = newState;
        }
    }

    /**
     * Adds new listener
     * @param listener listener
     */
    public void addListener(ServiceNotificationListener listener) {
        if (listeners == null) {
            listeners = new HashSet<ServiceNotificationListener>();
        }
        listeners.add(listener);
    }

    /**
     * Removes a listener
     * @param listener listener
     */
    public void removeListener(ServiceNotificationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns current state
     * @return current state
     */
    public int getState() {
        return state;
    }

    /**
     * Returns state as a string
     * @return state as a string
     */
    public String getStateName() {
        if ((state > 0) && (state < SERVICE_STATE_NAMES.length)) {
            return SERVICE_STATE_NAMES[state];
        } else {
            return "UNKNOWN(" + state + ")";
        }
    }

    /**
     * This method is to be called for service to be set-up.
     * This implementation does nothing.
     * @throws ServiceException
     */
    public void create() throws ServiceException {
        changeState(INITIALIZED);
    }

    /**
     * This method is to be called when service is to be removed from the system.
     * This implementation does nothing.
     * @throws Exception
     */
    public void destroy() throws ServiceException {
        if (state == STOPPING) {
            if (!waitForState(STOPPED, 100)) {
                throw new ServiceException("Couldn't reach stopped state within 100ms");
            }
        } else if (state != STOPPED || state == NOT_INITIALIZED || state == INITIALIZED) {
            throw new ServiceException("Cannot call destry while in " + SERVICE_STATE_NAMES[state] + " state.");
        }
        changeState(DESTROYED);
    }

    /**
     * This method is to be called for service to be started.
     * @throws Exception
     */
    public void start() throws ServiceException {
        synchronized (this) {
            stopService = false;
        }
        changeState(STARTED);
    }

    /**
     * This method is to be called for service to be stopped. Service won't stop
     * immediately but only {@link #stopService} flag is going to be set to <code>true</code>
     * @throws Exception
     */
    public void stop() throws ServiceException {
        changeState(STOPPING);
        synchronized (this) {
            stopService = true;
        }
    }

    /**
     * Wait for service to reach asked state.
     * @param state new state expected to be reached
     * @param millis number of milliseconds to wait or 0 forever
     * @return if state has been reached within timeout
     */
    public boolean waitForState(int state, int millis) {
        if (state == this.state) {
            return true;
        }
        long waitUtil = System.currentTimeMillis() + millis;
        synchronized (this) {
            while (state != this.state) {
                if (millis != 0 && System.currentTimeMillis() >= waitUtil) {
                    return false;
                }
                try {
                    this.wait(1);
                } catch (InterruptedException ignore) {
                }
            }
        }
        return true;
    }
}
