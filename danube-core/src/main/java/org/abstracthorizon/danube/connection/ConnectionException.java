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
package org.abstracthorizon.danube.connection;

/**
 * Runtime connection exception. Base class to be used for all connection related exceptions.
 *
 *
 * @author Daniel Sendula
 */
public class ConnectionException extends RuntimeException {

    /**
     * Constructor
     */
    public ConnectionException() {
        super();
    }

    /**
     * Constructor
     * @param msg message
     */
    public ConnectionException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     * @param cause underlaying cause
     */
    public ConnectionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     * @param msg message
     * @param cause underlaying cause
     */
    public ConnectionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
