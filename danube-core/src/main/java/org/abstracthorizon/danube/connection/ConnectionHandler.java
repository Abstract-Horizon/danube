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
 * This is connection handler. Implementations of this class should be able to
 * handle (process) requests passed down the connection and generate response
 * (to be send via same connection back to the client.
 *
 * @author Daniel Sendula
 *
 * @depend - - - org.abstracthorizon.danube.connection.Connection
 */
public interface ConnectionHandler {

    /**
     * This method processes a connection - handles requests and processes response.
     * @param connection connection
     */
    void handleConnection(Connection connection);

}
