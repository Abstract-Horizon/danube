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
package org.abstracthorizon.danube.http.session;

import org.abstracthorizon.danube.http.HTTPConnection;

import java.util.Iterator;

/**
 * Simple session manager implementation that checks for expired sessions
 * on invocation of {@link HTTPSessionManager} methods. If method is invoked sooner then
 * {@link #minScanInterval} then scan won't happen.
 *
 * @author Daniel Sendula
 */
public class SimpleSessionManager extends SimpleCookieSessionManager {

    /** Default session timeout as 30 minutes */
    public static final int DEFAULT_SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes

    /** Default minimum interval between scans for expired sessions */
    public static final int DEFAULT_MIN_SCAN_INTERVAL = 10 * 1000; // 10 secs

    /** Current session timeout */
    protected int sessionTimeout = DEFAULT_SESSION_TIMEOUT;

    /** When was last scan performed */
    protected long lastScan = 0;

    /** Minimum interval between two scans for expired sessions */
    protected int minScanInterval = DEFAULT_MIN_SCAN_INTERVAL;

    /**
     * Finds a session for supplied connection
     * @param connection connection
     * @param create flag defining should session be created it doesn't exist already
     */
    public Object findSession(HTTPConnection connection, boolean create) {
        checkForExpiredSessions();
        Session session = (Session)super.findSession(connection, create);
        if (session != null) {
            session.lastAccessed = System.currentTimeMillis();
        }
        return session;
    }

    /**
     * Searches for the session using givne session id
     * @param sessionId session id object
     * @param create flag defining should session be created it doesn't exist already
     */
    public Object findSession(Object sessionId, boolean create) {
        checkForExpiredSessions();
        Session session = (Session)super.findSession(sessionId, create);
        if (session != null) {
            session.lastAccessed = System.currentTimeMillis();
        }
        return session;
    }


    /**
     * Removes session from connection
     * @param connection connection
     */
    public void removeSession(HTTPConnection connection) {
        checkForExpiredSessions();
        super.removeSession(connection);
    }

    /**
     * This object removes existing session
     * @param session session to be removed
     */
    public void removeSession(Object session) {
        checkForExpiredSessions();
        super.removeSession(session);
    }

    /**
     * Rewrites URL for given connection
     * @param url URL to be rewritten
     */
    public String rewriteURL(HTTPConnection connection, String url) {
        return url;
    }

    /**
     * Checks if there are expired sessions. This method won't run if
     * it was invoked in less then {@link #minScanInterval} time.
     */
    protected void checkForExpiredSessions() {
        if (lastScan + minScanInterval < System.currentTimeMillis()) {
            lastScan = System.currentTimeMillis();
            scanForExpiredSessions();
        }
    }

    /**
     * Scans all defined sessions for expired ones
     */
    protected void scanForExpiredSessions() {
        synchronized (sessions) {
            Iterator<Session> it = sessions.values().iterator();
            long then = System.currentTimeMillis() - getSessionTimeout();
            while (it.hasNext()) {
                Session session = it.next();
                if (session.lastAccessed < then) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Returns session timeout
     * @return session timeout
     */
    public int getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * Sets session timeout
     * @param sessionTimeout session timeout
     */
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    /**
     * Returns minimum interval between scans for expired sessions
     * @return minimum interval between scans for expired sessions
     */
    public int getMinScanInterval() {
        return minScanInterval;
    }

    /**
     * Sets minimum interval between scans for expired sessions
     * @param minScanInterval minimum interval between scans for expired sessions
     */
    public void setMinScanInterval(int minScanInterval) {
        this.minScanInterval = minScanInterval;
    }

}
