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
package org.abstracthorizon.danube.service;

/**
 * Service exception
 *
 * @author Daniel Sendula
 */
public class ServiceException extends RuntimeException {

    /**
     * Empty constructor
     */
    public ServiceException() {
    }

    /**
     * Constructor
     * @param msg message
     */
    public ServiceException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     * @param cause cause
     */
    public ServiceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     * @param msg message
     * @param cause cause
     */
    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
