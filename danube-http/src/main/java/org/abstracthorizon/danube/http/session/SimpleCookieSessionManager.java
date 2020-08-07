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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.cookie.Cookie;
import org.abstracthorizon.danube.http.cookie.CookieUtilities;

/**
 * Simple session manager implementation that uses cookies for storing
 * session id on the client side. Sessions are not expired.
 *
 * @author Daniel Sendula
 */
public class SimpleCookieSessionManager implements HTTPSessionManager {

    /** Default session cookie name */
    public static final String SESSION_COOKIE_NAME = "JSESSIONID";

    /** Connection attribute name for session to be temporarely stored at */
    public static final String SESSION_ATTRIBUTE = "org.abstracthorizon.danube.http.session.SimpleSessionManager";

    /** Default session timeout as 30 minutes */
    public static final int DEFAULT_SESSION_TIMEOUT = 30 * 60 * 1000; // 30 minutes

    /** Map of sessions */
    protected Map<Object, Session> sessions = new HashMap<Object, Session>();

    /** Name of session cookie. Defaulted to {@link #SESSION_COOKIE_NAME}. */
    protected String sessionCookieName = SESSION_COOKIE_NAME;

    /** Random number for creating session ids */
    protected static Random random = new Random();

    /**
     * Finds a session for supplied connection
     * @param connection connection
     * @param create flag defining should session be created it doesn't exist already
     */
    public Object findSession(HTTPConnection connection, boolean create) {
        Session session = (Session)connection.getAttributes().get(SESSION_ATTRIBUTE);
        if (session != null) {
            return session;
        }

        Map<String, Cookie> cookies = CookieUtilities.getRequestCookies(connection);
        Cookie cookie = cookies.get(sessionCookieName);
        if (cookie == null) {
            Map<String, Cookie> map = CookieUtilities.getResponseCookies(connection);
            if (map != null) {
                cookie = map.get(sessionCookieName);
            }
        }

        String sessionId = null;
        if (cookie != null) {
            sessionId = cookie.getValue();
        }
        if (sessionId != null) {
            sessionId = validateSessionId(sessionId);
        }
        if (create && (sessionId == null)) {
            sessionId = createSessionId();
            Cookie newSessionCookie = new Cookie();
            newSessionCookie.setName(sessionCookieName);
            newSessionCookie.setValue(sessionId);
            newSessionCookie.setPath(connection.getContextPath());
            CookieUtilities.addResponseCookies(connection, Arrays.asList(newSessionCookie));
        }
        if (sessionId != null) {
            session = (Session)findSession(sessionId, create);
            connection.getAttributes().put(SESSION_ATTRIBUTE, session);
            return session;
        } else {
            return null;
        }
    }

    /**
     * Searches for the session using givne session id
     * @param sessionId session id object
     * @param create flag defining should session be created it doesn't exist already
     */
    public Object findSession(Object sessionId, boolean create) {
        synchronized (sessions) {
            Session session = sessions.get(sessionId);
            if ((session == null) && create) {
                session = createSession(sessionId.toString());
                sessions.put(sessionId, session);
            }
            return session;
        }
    }


    /**
     * Removes session from connection
     * @param connection connection
     */
    public void removeSession(HTTPConnection connection) {
        Session session = (Session)findSession(connection, false);
        if (session != null) {
            removeSession(session);
            connection.getAttributes().remove(SESSION_ATTRIBUTE);
        }
    }

    /**
     * This object removes existing session
     * @param session session to be removed
     */
    public void removeSession(Object session) {
        synchronized (sessions) {
            sessions.remove(session);
        }
    }

    /**
     * Rewrites URL for given connection
     * @param url URL to be rewritten
     */
    public String rewriteURL(HTTPConnection connection, String url) {
        return url;
    }

    /**
     * Validates session id. This implementation does nothing
     * @param sessionId session id
     * @return session id
     */
    protected String validateSessionId(String sessionId) {
        return sessionId;
    }

    /**
     * Creates session id
     * @return new session id
     */
    protected String createSessionId() {
        long l = random.nextLong();
        if (l < 0) {
            l = -l;
        }
        String sessionId = Long.toString(l);
        return sessionId;
    }

    /**
     * Creates new session object with given session id
     * @param sessionId session id
     * @return new session object
     */
    protected Session createSession(String sessionId) {
        return new Session(sessionId);
    }

    /**
     * Returns session's cookie name
     * @return session's cookie name
     */
    public String getSessionCookieName() {
        return sessionCookieName;
    }

    /**
     * Sets session's cookie name
     * @param sessionCookieName session's cookie name
     */
    public void setSessionCookieName(String sessionCookieName) {
        this.sessionCookieName = sessionCookieName;
    }
}
