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
package org.abstracthorizon.danube.http.matcher;

import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.HTTPConnection;

/**
 * This matcher checks prefix of http URI. If URI starts with given prefix
 * and follow character is &quot;/&quot; or &quot;?&quot; or URI is exactly the same
 * length as prefix then it returns positive match
 *
 * @author Daniel Sendula
 */
public class Prefix extends AbstractMatcher {

    /** Prefix */
    protected String prefix = "/"; // Match all until told differently

    /**
     * Constructor
     */
    public Prefix() {
    }

    /**
     * Constrcutor
     * @param connectionHandler connection handler
     * @param prefix prefix
     */
    public Prefix(ConnectionHandler connectionHandler, String prefix) {
        super(connectionHandler);
        this.prefix = prefix;
    }

    /**
     * Constructor
     * @param connectionHandler connection handler
     * @param stopOnMatch stop on match
     * @param prefix prefix
     */
    public Prefix(ConnectionHandler connectionHandler, boolean stopOnMatch, String prefix) {
        super(connectionHandler, stopOnMatch);
        this.prefix = prefix;
    }

    /**
     * Returns prefix
     * @return prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets prefix
     * @param prefix prefix
     */
    public void setPrefix(String prefix) {
        // Always match full path components. That means it has to start
        // with '/' and must end with '/' or to match exactly the end of the URI
        if (prefix.length() > 0) {
            if (!prefix.startsWith("/")) {
                prefix = "/" + prefix;
            }
        } else {
            prefix = "/";
        }
        this.prefix = prefix;
    }

    /**
     * This matcher checks prefix of requested path. If path starts with given prefix
     * and following character is &quot;/&quot; or path is exactly the same
     * length as prefix then it returns positive match
     * @param httpConnection http connection
     */
    public boolean matches(HTTPConnection httpConnection) {
        String path = httpConnection.getComponentResourcePath();
        int prefixLen = prefix.length();
        if (path.length() > prefixLen) {
            if (path.startsWith(prefix)) {
                if (prefixLen == 1) {
                    return true;
                } else {
                    char c = path.charAt(prefixLen);
                    if (c == '/') {
                        return true;
                    }
                }
            }
        } else {
            if (path.equals(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adjusts connection for nested connection handler to be invoked
     * @param connection connection to be a used
     */
    public void adjustForInvocation(HTTPConnection httpConnection) {
        String path = httpConnection.getComponentResourcePath();
        int prefixLen = prefix.length();
        if (path.length() > prefixLen) {
            if (path.startsWith(prefix)) {
                if (prefixLen == 1) {
                    return;
                } else {
                    char c = path.charAt(prefixLen);
                    if (c == '/') {
                        httpConnection.setComponentResourcePath(path.substring(prefixLen));
                        String componentPath = httpConnection.getComponentPath();
                        if (componentPath.endsWith("/")) {
                            componentPath = componentPath + prefix.substring(1);
                        } else {
                            componentPath = componentPath + prefix;
                        }
                        httpConnection.setComponentPath(componentPath);
                    }
                }
            }
        } else {
            if (path.equals(prefix)) {
                // Exact match - no more elements in the URI.
                // We will leave '/' in path so it can be matched
                // as many times as needed.
                // TODO Check if this is correct expected behaviour
                String componentPath = httpConnection.getComponentPath();
                if (componentPath.endsWith("/")) {
                    componentPath = componentPath + prefix.substring(1);
                } else {
                    componentPath = componentPath + prefix;
                }
                httpConnection.setComponentPath(componentPath);
                httpConnection.setComponentResourcePath("/");
            }
        }
    }

}
