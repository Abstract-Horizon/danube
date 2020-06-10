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
package org.abstracthorizon.danube.beanconsole;

/**
 * Exception explaining what went wrong with navigating through object with the given path
 *
 * @author Daniel Sendula
 */
public class BeanAccessException extends RuntimeException {

    /**
     * Constructor
     * @param path path
     */
    public BeanAccessException(String path) {
        super("Failed accessing " + path + " element.");
    }

    /**
     * Constructor
     * @param path path
     * @param cause cause
     */
    public BeanAccessException(String path, Exception cause) {
        super("Failed accessing " + path + " element.", cause);
    }

}
