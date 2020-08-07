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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface represents simple (two way) connection
 *
 * @author Daniel Sendula
 */
public interface StreamConnection extends Connection {

    /**
     * Returns connection's input stream
     * @return connection's input stream
     */
    InputStream getInputStream();

    /**
     * Returns connection's output stream
     * @return connection's output stream
     */
    OutputStream getOutputStream();
}
