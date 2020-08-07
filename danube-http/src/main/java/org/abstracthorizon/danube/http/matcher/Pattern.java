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

import org.abstracthorizon.danube.http.HTTPConnection;

/**
 * Implementation of {@link org.abstracthorizon.danube.http.matcher.Matcher} interface
 * that uses {@link java.util.regex.Pattern} for matching.
 *
 * @author Daniel Sendula
 */
public class Pattern extends AbstractMatcher {

    /** Precompiled pattern */
    protected java.util.regex.Pattern pattern;

    /** Component path to be set when pattern is matched */
    protected String componentPath = "/";

    /** Matches as component's path */
    protected boolean matchAsComponentPath = false;

    /** Constructor */
    public Pattern() {
    }

    /**
     * Returns precompiled pattern
     * @return precompiled pattern
     */
    public java.util.regex.Pattern getCompiledPattern() {
        return pattern;
    }

    /**
     * Sets precompiled pattern
     * @param pattern precompiled pattern
     */
    public void setCompiledPattern(java.util.regex.Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Returns pattern as a String
     * @return pattern as a String
     */
    public String getPattern() {
        return pattern.toString();
    }

    /**
     * Sets pattern as a String
     * @param pattern pattern as a String
     */
    public void setPattern(String pattern) {
        this.pattern = java.util.regex.Pattern.compile(pattern);
    }

    /**
     * Returns component path
     * @return component path
     */
    public String getComponentPath() {
        return componentPath;
    }

    /**
     * Sets component path. If component path doesn't start with an &quot;/&quot; it
     * is automatically added.
     * TODO Should leading / be stripped at the same time?
     * @param componentPath component path
     */
    public void setComponentPath(String componentPath) {
        this.componentPath = componentPath;
        if (!this.componentPath.startsWith("/")) {
            this.componentPath = "/" + this.componentPath;
        }
    }

    /**
     * Uses precompiled pattern to match URI from http conneciton
     * @param httpConnection http connection
     */
    public boolean matches(HTTPConnection httpConnection) {
        String path = httpConnection.getComponentResourcePath();
        return pattern.matcher(path).matches();
    }

    /**
     * Adjusts connection for nested connection handler to be invoked
     * @param connection connection to be a used
     */
    public void adjustForInvocation(HTTPConnection httpConnection) {
        String path = httpConnection.getComponentResourcePath();
        if (matchAsComponentPath) {
            httpConnection.setComponentPath(path);
            httpConnection.setComponentResourcePath(null);
        } else {
            if (path.startsWith(componentPath)) {
                path = path.substring(componentPath.length());
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                httpConnection.setComponentResourcePath(path);
            }
            httpConnection.setComponentPath(componentPath);
        }
    }

    public boolean isMatchAsComponentPath() {
        return matchAsComponentPath;
    }

//    public boolean getMatchAsComponentPath() {
//        return matchAsComponentPath;
//    }

    public void setMatchAsComponentPath(boolean matchAsComponentPath) {
        this.matchAsComponentPath = matchAsComponentPath;
    }
}
