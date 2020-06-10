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
package org.abstracthorizon.danube.http.matcher;

import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.HTTPConnection;

/**
 * This interface defines matcher for {@link org.abstracthorizon.danube.http.Selector} class.
 *
 * @author Daniel Sendula
 */
public interface Matcher {

    /**
     * Returns stored {@link ConnectionHandler}
     * @return stored {@link ConnectionHandler}
     */
    public ConnectionHandler getConnectionHandler();

    /**
     * Returns <code>true</code> if no other {@link ConnectionHandler} should be processed after this one
     * @return <code>true</code> if no other {@link ConnectionHandler} should be processed after this one
     */
    public boolean isStopOnMatch();

    /**
     * Returns <code>true</code> if uri from connection is matched
     * @param connection http connection
     * @return <code>true</code> if uri from connection is matched
     */
    public boolean matches(HTTPConnection connection);

    /**
     * Adjusts connection for nested connection handler to be invoked
     * @param connection connection to be a used
     */
    public void adjustForInvocation(HTTPConnection connection);
}
