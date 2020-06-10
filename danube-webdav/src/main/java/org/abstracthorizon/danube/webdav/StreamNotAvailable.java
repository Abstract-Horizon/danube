/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.webdav;

import java.io.IOException;

/**
 * Exception denoting that stream is not available
 *
 * @author Daniel Sendula
 */
public class StreamNotAvailable extends IOException {

    /**
     * Constructor
     */
    public StreamNotAvailable() {
    }

    /**
     * Constructor
     * @param msg message
     */
    public StreamNotAvailable(String msg) {
        super(msg);
    }

}
