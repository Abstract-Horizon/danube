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
package org.abstracthorizon.danube.support;

import java.io.IOException;

/**
 * Runtime IO exception. Base class to be used for all IO related exceptions.
 *
 *
 * @author Daniel Sendula
 */
public class RuntimeIOException extends RuntimeException {

    /**
     * Constructor
     */
    public RuntimeIOException() {
        super();
    }

    /**
     * Constructor
     * @param msg message
     */
    public RuntimeIOException(String msg) {
        super(msg);
    }

    /**
     * Constructor
     * @param cause underlaying cause
     */
    public RuntimeIOException(IOException cause) {
        super(cause);
    }

    /**
     * Constructor
     * @param msg message
     * @param cause underlaying cause
     */
    public RuntimeIOException(String msg, IOException cause) {
        super(msg, cause);
    }

    /**
     * Returns cause as {@link IOException}
     * @return cause as {@link IOException}
     */
    public IOException getIOCause() {
        return (IOException)getCause();
    }
}
