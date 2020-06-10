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
package org.abstracthorizon.danube.http.matcher;

import org.abstracthorizon.danube.connection.ConnectionHandler;

/**
 * Implementation of {@link org.abstracthorizon.danube.http.matcher.Matcher} interface.
 *
 * @author Daniel Sendula
 */
public abstract class AbstractMatcher implements Matcher {

    /** Param connection handler */
    protected ConnectionHandler connectionHandler;

    /** Default value is <code>true</code> */
    protected boolean stopOnMatch = true;

    /**
     * Constructor
     */
    public AbstractMatcher() {
    }

    /**
     * Constructor
     * @param connectionHandler connection handler
     */
    public AbstractMatcher(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * Constructor
     * @param connectionHandler connection handler
     * @param stopOnMatch stop on match
     */
    public AbstractMatcher(ConnectionHandler connectionHandler, boolean stopOnMatch) {
        this.connectionHandler = connectionHandler;
        this.stopOnMatch = stopOnMatch;
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
     * @param connectionHandler
     */
    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * Returns if matching should stop with this one if positive
     * @return if matching should stop with this one if positive
     */
    public boolean isStopOnMatch() {
        return stopOnMatch;
    }

    /**
     * Sets if matching should stop with this one if positive
     * @param stopOnMatch <code>true<code> if matching should stop with this one if positive
     */
    public void setStopOnMatch(boolean stopOnMatch) {
        this.stopOnMatch = stopOnMatch;
    }

}
