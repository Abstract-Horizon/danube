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
package org.abstracthorizon.danube.session;

/**
 * This interface defines simple session handling interface. It defines a
 * factory that keeps track of sessions.
 *
 * @author Daniel Sendula
 */
public interface SessionManager {

    /**
     * Finds existing session or creates new one if there is no existing session
     * and create parameter is set to <code>true</code>. Session is found
     * based on sessionId object.
     *
     * @param sessionId object that represents the session. Usually a string extracted
     *                  from request
     * @param create should new session be created if there is no existing session
     * @return existing session object, new session if existing session is not there and
     *         create param is set to <code>true</code> or <code>null</code> otherwise.
     */
    Object findSession(Object sessionId, boolean create);

    /**
     * This object removes existing session
     * @param session session to be removed
     */
    void removeSession(Object session);

}
