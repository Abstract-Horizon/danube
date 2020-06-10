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
package org.abstracthorizon.danube.http.auth;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionException;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.HTTPContext;
import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.http.session.HTTPSessionManager;
import org.abstracthorizon.danube.http.session.Session;
import org.abstracthorizon.danube.http.session.SimpleSessionManager;
import org.abstracthorizon.danube.http.util.Base64;
import org.abstracthorizon.danube.support.RuntimeIOException;

import java.io.IOException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.ConfirmationCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class marks start of &quot;web application&quot; by setting
 * context path. All, potential, session handling will be done using
 * context path - path this component is defined on.
 *
 * @author Daniel Sendula
 */
public class JAASAuthenticatedHTTPContext extends HTTPContext {


    /** Logger */
    protected final Logger logger = LoggerFactory.getLogger(JAASAuthenticatedHTTPContext.class);


    /** Authorisation data session attribute */
    public static final String AUTHORIZATION_DATA_ATTRIBUTE = "org.abstracthorizon.danube.http.auth.Subject";

    /** Client request header for authorisation  */
    public static final String AUTHORIZATION_REQUEST_HEADER = "Authorization";

    /** Server response header for authorisation  */
    public static final String AUTHORIZATION_RESPONSE_HEADER = "WWW-Authenticate";

    /** Default cache timeout */
    public static final int DEFAULT_CACHE_TIMEOUT = 10 * 60 * 1000; // 10 minutes

    /** Default minimum scan period */
    public static final int DEFAULT_MINIMUM_SCAN_PERIOD = 10 * 1000; // 10 seconds

    /** Session manager */
    protected HTTPSessionManager sessionManager;

    /** Realm name */
    protected String realm;

    /** Login context name */
    protected String loginContextName;

    /** Login context */
    protected LoginContext loginContext;

    /** Cache to hold authorisation information for a while */
    protected Map<String, AuthData> cachedAuth = new HashMap<String, AuthData>();

    /** Cache timeout */
    protected int cacheTimeout = DEFAULT_CACHE_TIMEOUT;

    /** Minimum scan period */
    protected int minScanPeriod = DEFAULT_MINIMUM_SCAN_PERIOD;

    /** When was cache scanned last time for expired entries */
    protected long lastScan;

    /** Force authorisation */
    protected boolean forceAuthorisation = true;
    
    /**
     * Constructor
     */
    public JAASAuthenticatedHTTPContext() {
    }

    /**
     * This method creates sets context path to be same as context path
     * up to here plus this component's path. Component's path is reset
     * to &quot;<code>/<code>&quot;
     *
     * @param connection socket connection
     * @throws ConnectionException
     */
    public void handleConnection(final Connection connection) throws ConnectionException {
        Subject subject = null;
        Session session = null;
        boolean fromSession = false;
        HTTPSessionManager sessionManager = getSessionManager();
        if (sessionManager != null) {
            session = (Session)sessionManager.findSession(connection, false);
            if (session != null) {
                subject = (Subject)session.getAttributes().get(AUTHORIZATION_DATA_ATTRIBUTE);
                if (subject != null) {
                    fromSession = true;
                }
            }
        }

        HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
        if (subject == null) {

            String authHeader = httpConnection.getRequestHeaders().getOnly(AUTHORIZATION_REQUEST_HEADER);
            if (authHeader != null) {
                if (authHeader.startsWith("Basic ")) {
                    String base64 = authHeader.substring(6);
                    subject = authorise(base64);
                }
            }
        }

        if (subject != null) {
            if (!fromSession && (session != null)) {
                session.getAttributes().put(AUTHORIZATION_DATA_ATTRIBUTE, subject);
            }
            try {
                Subject.doAs(subject, new PrivilegedExceptionAction<Object>() {
                    public Object run() throws Exception {
                        superHandleConnection(connection);
                        return null;
                    }
                });
            } catch (PrivilegedActionException e) {
                if (e.getException() instanceof ConnectionException) {
                    throw (ConnectionException)e.getException();
                } else if (e.getException() instanceof IOException) {
                    throw new RuntimeIOException((IOException)e.getException());
                } else {
                    throw new ConnectionException(e);
                }
            }
        } else {
            if (forceAuthorisation) {
                String oldComponentPath = httpConnection.getComponentPath();
                httpConnection.addComponentPathToContextPath();
    //            httpConnection.addToContextPath(httpConnection.getComponentPath());
    //            httpConnection.setComponentPath("/");
    
                String realm = getRealm();
                if (realm == null) {
                    realm = oldComponentPath;
                }
                httpConnection.getResponseHeaders().putOnly(AUTHORIZATION_RESPONSE_HEADER, "Basic realm=\"" + realm + "\"");
                httpConnection.setResponseStatus(Status.UNAUTHORIZED);
            } else {
                try {
                    superHandleConnection(connection);
                } catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            }
        }
    }

    /**
     * Calls super class handle connection method.
     * @param connection connection
     * @throws IOException if thrown by super {@link #handleConnection(Connection)}
     */
    protected void superHandleConnection(Connection connection) throws IOException {
        super.handleConnection(connection);
    }

    /**
     * Obtains subject object from base 64 encoded username and password
     * @param base64 base 64 encoded username and password
     * @return subject or <code>null</code>
     */
    protected Subject authorise(String base64) {
        AuthData authData = null;
        // TODO Maybe we need to keep queried AuthData even if it is expired
        synchronized (cachedAuth) {
            long now = System.currentTimeMillis() - minScanPeriod;
            if (lastScan + minScanPeriod < now) {
                Iterator<AuthData> it = cachedAuth.values().iterator();
                while (it.hasNext()) {
                    authData = it.next();
                    if (authData.lastAccessed + cacheTimeout < now) {
                        it.remove();
                    }
                }
                lastScan = System.currentTimeMillis();
            }
            authData = cachedAuth.get(base64);
        }

        if (authData != null) {
            authData.lastAccessed = System.currentTimeMillis();
            return authData.subject;
        }

        String userPass = Base64.decode(base64);
        int i = userPass.indexOf(':');
        if (i < 0) {
            return null;
        }

        final String user = userPass.substring(0, i);
        final char[] pass = userPass.substring(i+1).toCharArray();

        LoginContext loginContext; // = getLoginContext();
        try {
            // if (loginContext == null) {
                loginContext = new LoginContext(getLoginContextName(), new CallbackHandler() {

                    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                        for (int i = 0; i < callbacks.length; i++) {
                            if (callbacks[i] instanceof TextOutputCallback) {
                            } else if (callbacks[i] instanceof ConfirmationCallback) {
                            } else if (callbacks[i] instanceof NameCallback) {
                                NameCallback nameCallback = (NameCallback)callbacks[i];
                                nameCallback.setName(user);
                            } else if (callbacks[i] instanceof PasswordCallback) {
                                PasswordCallback passwordCallback = (PasswordCallback)callbacks[i];
                                passwordCallback.setPassword(pass);
                            } else {
                                throw new UnsupportedCallbackException
                                 (callbacks[i], "Unrecognized Callback");
                            }
                          }
                    }
                });
                setLoginContext(loginContext);
            // }
            logger.debug("Trying to authenticate user " + user);
            loginContext.login();
            logger.debug("Successfully authenticated user " + user);
        } catch (LoginException e) {
            logger.debug("Exception trying to get LoginContext " + getLoginContextName(), e);
            return null;
        }
        Subject subject = loginContext.getSubject();
        synchronized (cachedAuth) {
            authData = new AuthData();
            authData.lastAccessed = System.currentTimeMillis();
            authData.subject = subject;
            cachedAuth.put(base64, authData);
        }
        return subject;
    }

    /**
     * Returns session manaager
     * @return http session manager
     */
    public HTTPSessionManager getSessionManager() {
        if (sessionManager == null) {
            sessionManager = new SimpleSessionManager();
        }
        return sessionManager;
    }

    /**
     * Sets session manager
     * @param sessionManager http session manager
     */
    public void setSessionManager(HTTPSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    /**
     * Returns realm to be used. If not set then component path will be used.
     * @return realm
     */
    public String getRealm() {
        return realm;
    }

    /**
     * Sets realm.
     *
     * @param realm realm
     */
    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * Returns login context name
     * @return login context name
     */
    public String getLoginContextName() {
        return loginContextName;
    }

    /**
     * Sets login context name
     * @param loginContextName login context name
     */
    public void setLoginContextName(String loginContextName) {
        this.loginContextName = loginContextName;
    }

    /**
     * Returns login context
     * @return login context
     */
    public LoginContext getLoginContext() {
        return loginContext;
    }

    /**
     * Sets login context
     * @param loginContext login context
     */
    public void setLoginContext(LoginContext loginContext) {
        this.loginContext = loginContext;
    }

    /**
     * Returns cache timeout
     * @return cache timeout
     */
    public int getCacheTimeout() {
        return cacheTimeout;
    }

    /**
     * Sets cache timeout
     * @param cacheTimeout cache timeout
     */
    public void setCacheTimeout(int cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    /**
     * Return minimum scan period
     * @return minimum scan period
     */
    public int getMinimumScanPeriod() {
        return minScanPeriod;
    }

    /**
     * Sets minimum scan period
     * @param minScanPeriod minimum scan period
     */
    public void setMinimumScanPeriod(int minScanPeriod) {
        this.minScanPeriod = minScanPeriod;
    }
    
    public boolean getForceAuthorisation() {
        return forceAuthorisation;
    }
    
    public void setForceAuthorisation(boolean forceAuthorisation) {
        this.forceAuthorisation = forceAuthorisation;
    }


    /**
     * Class holding cached authorisation data
     */
    protected class AuthData {

        public long lastAccessed;
        public Subject subject;

    }
}
