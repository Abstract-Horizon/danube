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
package org.abstracthorizon.danube.http.session;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.session.SessionManager;

/**
 * This interface defines simple session handling interface. It defines a
 * factory that keeps track of sessions. This interface adds new utility
 * method that obtains session id from {@link HTTPConnection}. Also,
 * it adds method to rewrite URLs if needed.
 *
 * @author Daniel Sendula
 */
public interface HTTPSessionManager extends SessionManager {

    /**
     * Finds existing session or creates new one if there is no existing session
     * and create parameter is set to <code>true</code>. This method should
     * call {@link SessionManager#findSession(Object, boolean)} when session id is found.
     *
     * @param connection http connection for session id to be extracted from
     * @param create should new session be created if there is no existing session
     * @return existing session object, new session if existing session is not there and
     *         create param is set to <code>true</code> or <code>null</code> otherwise.
     */
    Object findSession(HTTPConnection connection, boolean create);

    /**
     * This method should take care session ids kept in the url. If such thing is not used then
     * it shouold return url parameter unchanged.
     *
     * @param connection http connection object
     * @param url url to be updated
     * @return updated url
     */
    String rewriteURL(HTTPConnection connection, String url);

    /**
     * Removes session from connection
     * @param connection connection
     */
    void removeSession(HTTPConnection connection);
}
