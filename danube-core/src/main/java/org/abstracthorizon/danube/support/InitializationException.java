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
package org.abstracthorizon.danube.support;

/**
 * Initialisation exception. Base class to be used for all intialisation related exceptions.
 *
 *
 * @author Daniel Sendula
 */
public class InitializationException extends RuntimeException {

    /**
     * Constructor
     */
    public InitializationException() {
        super();
    }

    /**
     * Constructor
     * @param msg message
     */
    public InitializationException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     * @param cause underlaying cause
     */
    public InitializationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor
     * @param msg message
     * @param cause underlaying cause
     */
    public InitializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
