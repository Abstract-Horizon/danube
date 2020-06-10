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

import java.util.HashMap;
import java.util.Map;

/**
 * Simple session object
 *
 * @author Daniel Sendula
 */
public class Session {

    /** Session attributes */
    protected Map<String, Object> attributes = new HashMap<String, Object>();

    /** Time when session is accessed */
    protected long lastAccessed;

    /** Session id */
    protected String sessionId;

    /**
     * Constructor
     * @param sessionId session id
     */
    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Returns session id
     * @return session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Returns session attributes
     * @return session attributes
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Returns value of {{@link #lastAccessed } field.
     * @return value of {{@link #lastAccessed } field.
     */
    public long getLastAccessed() {
        return lastAccessed;
    }

    /**
     * Sets {@link #lastAccessed} field to current time.
     */
    protected void resetAccess() {
        lastAccessed = System.currentTimeMillis();
    }

}
