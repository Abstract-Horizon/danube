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

package org.abstracthorizon.danube.tapestry;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.SSLSocket;

import org.abstracthorizon.danube.connection.ConnectionException;
import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.Status;
import org.abstracthorizon.danube.http.session.HTTPSessionManager;
import org.abstracthorizon.danube.http.session.Session;

import org.apache.tapestry.describe.DescriptionReceiver;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebSession;

/**
 * {@link WebRequest} interface implementation
 *
 * @author Daniel Sendula
 */
public class DanubeRequest implements WebRequest {

    /** Reference connection */
    protected HTTPConnection connection;

    /** Session manager */
    protected HTTPSessionManager sessionManager;

    /**
     * Constructor
     * @param connection connection
     * @param sessionManager session manager
     */
    public DanubeRequest(HTTPConnection connection, HTTPSessionManager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
    }

    /**
     * Returns parameter names as a list
     * @return parameter names as a list
     */
    public List<String> getParameterNames() {
        return new ArrayList<String>(connection.getRequestParameters().keySet());
    }

    /**
     * Returns parameter value
     * @param name parameter's name
     * @return parameter value
     */
    public String getParameterValue(String name) {
        String[] values = getParameterValues(name);
        if ((values != null) && (values.length > 0)) {
            return values[0];
        } else {
            return null;
        }
    }

    /**
     * Returns parameter's values
     * @param name parameter's name
     * @return array of attribute's values
     */
    public String[] getParameterValues(String name) {
        return connection.getRequestParameters().getAsArray(name);
    }

    /**
     * Returns context path
     * @return context path
     */
    public String getContextPath() {
        return connection.getContextPath();
    }

    /**
     * Returns session
     * @param create if <code>true</code> new session is going to be create if it doesn't exist already
     * @return web sesison
     */
    public WebSession getSession(boolean create) {
        Session session = (Session)sessionManager.findSession(connection, create);
        if (session != null) {
            DanubeSession danubeSession = (DanubeSession)session.getAttributes().get(DanubeSession.SESSION_ATTRIBUTE);
            if (danubeSession == null) {
                danubeSession = new DanubeSession(session);
            } else {
                danubeSession.clearNew();
            }
            return danubeSession;
        } else {
            return null;
        }
    }

    /**
     * Returns <code>HTTP</code>
     * @return scheme
     */
    public String getScheme() {
        String scheme = "HTTP";
        if ((Socket)connection.adapt(Socket.class) instanceof SSLSocket) {
            scheme = "HTTPS";
        }
        return scheme;
    }

    /**
     * Returns server's name
     * @return server's name
     */
    public String getServerName() {
//      TODO ???
        return ((Socket)connection.adapt(Socket.class)).getLocalAddress().getHostName();
    }

    /**
     * Returns port request is served from. That is server's port.
     * @return server's port
     */
    public int getServerPort() {
        return ((Socket)connection.adapt(Socket.class)).getLocalPort();
    }

    /**
     * Returns request URI
     * @return request URI
     */
    public String getRequestURI() {
        return connection.getRequestURI();
    }

    /**
     * Forwards internally to given URL
     * @param url URI to be forwarded to
     */
    public void forward(String url) {
        try {
            InetSocketAddress localAddressSocket = (InetSocketAddress)((Socket)connection.adapt(Socket.class)).getLocalSocketAddress();

            URL u = new URL(url);

            int port = u.getPort();
            if (port <= 0) {
                port = localAddressSocket.getPort();
            }

            SocketAddress redirectSocketAddress = new InetSocketAddress(u.getHost(), u.getPort());

            if (u.getProtocol().equals(connection.getRequestProtocol())
                    && localAddressSocket.equals(redirectSocketAddress)) {
                connection.forward(u.getFile());
            } else {
                if (connection.getRequestProtocol().equals("HTTP/1.1")) {
                    connection.setResponseStatus(Status.SEE_OTHER);
                } else {
                    connection.setResponseStatus(Status.FOUND);
                }
                connection.getResponseHeaders().add("Location", url.toString());
            }
        } catch (MalformedURLException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Returns component's path
     * @return component's path
     */
    public String getActivationPath() {
        return connection.getComponentPath();
    }

    /**
     * Returns path info
     * @return path info
     */
    public String getPathInfo() {
        String path = connection.getComponentResourcePath();
        if (path == null) {
            return path;
        } else {
            int i = path.indexOf('?');
            if (i >= 0) {
                path = path.substring(0, i);
            }
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        }
    }

    /**
     * Returns locale
     * @return locale
     */
    public Locale getLocale() {
        return Locale.getDefault(); // TODO
    }

    /**
     * Returns header's value
     * @param name headers name
     * @return header's value
     */
    public String getHeader(String name) {
        return (String)connection.getRequestHeaders().getOnly(name);
    }

    /**
     * Returns remote user
     * @return remote user
     */
    public String getRemoteUser() {
        return null; // TODO
    }

    /**
     * Returns user principal
     * @return user principal
     */
    public Principal getUserPrincipal() {
        return null; // TODO
    }

    /**
     * Checks if user is in role
     * @param role role
     * @return <code>true</code> if user is in role
     */
    public boolean isUserInRole(String role) {
        return false; // TODO
    }

    /**
     * Returns attribute names
     * @return attribute names as a list
     */
    public List<Object> getAttributeNames() {
        return new ArrayList<Object>(connection.getAttributes().keySet());
    }

    /**
     * Returns attribute value
     * @param name attribute's name
     * @return attribute's value
     */
    public Object getAttribute(String name) {
        return connection.getAttributes().get(name);
    }

    /**
     * Sets attribute
     * @param name attribute's name
     * @param value attribute's value
     */
    public void setAttribute(String name, Object value) {
        connection.getAttributes().put(name, value);
    }

    /**
     * Does nothing
     */
    public void describeTo(DescriptionReceiver descriptionReceiver) {
    }

}
