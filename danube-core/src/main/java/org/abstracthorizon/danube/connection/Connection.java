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

import org.abstracthorizon.danube.adapter.Adaptable;

/**
 * This interface represents simple (two way) connection
 *
 * @author Daniel Sendula
 */
public interface Connection extends Adaptable {

    /**
     * This method closes the existing connection, if possible
     */
    void close();

    /**
     * Checks if connection has been closed
     *
     * @return <code>true</code> if connection has been closed
     */
    boolean isClosed();
}
