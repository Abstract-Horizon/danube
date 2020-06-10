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
package org.abstracthorizon.danube.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Connection wrapper class.
 *
 * @author Daniel Sendula
 */
public abstract class ConnectionWrapper implements Connection {

    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Wrapped connection */
    protected Connection connection;

    /**
     * Constructor
     * @param connection
     */
    public ConnectionWrapper(Connection connection) {
        this.connection = connection;

    }

    /**
     * Closes the connection
     */
    public void close() {
        connection.close();
    }

    /**
     * Checks if underlaying connection closed
     * @return <code>true</code> if underlaying connection closed
     */
    public boolean isClosed() {
        return connection.isClosed();
    }

    /**
     * Adopts this object using supplied adopter manager
     * @param cls class to be adopted to
     * @return this connection adopted to asked class
     */
    @SuppressWarnings("unchecked")
    public <T> T adapt(Class<T> cls) {
        if (cls == getClass()) {
            return (T)this;
        } else {
            return connection.adapt(cls);
        }
    }
}
