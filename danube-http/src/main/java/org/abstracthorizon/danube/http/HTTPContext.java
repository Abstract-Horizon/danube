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
package org.abstracthorizon.danube.http;

import org.abstracthorizon.danube.connection.Connection;

/**
 * This class marks start of &quot;web application&quot; by setting
 * context path. All, potential, session handling will be done using
 * context path - path this component is defined on.
 *
 * @author Daniel Sendula
 */
public class HTTPContext extends Selector {

    /** Constructor */
    public HTTPContext() {
    }

    /**
     * This method creates sets context path to be same as context path
     * up to here plus this component's path. Component's path is reset
     * to &quot;<code>/<code>&quot;
     *
     * @param connection socket connection
     */
    public void handleConnection(Connection connection) {
        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
        httpConnection.addComponentPathToContextPath();
//      httpConnection.addToContextPath(httpConnection.getComponentPath());
//      httpConnection.setComponentPath("/");

        super.handleConnection(connection);

    }
}
