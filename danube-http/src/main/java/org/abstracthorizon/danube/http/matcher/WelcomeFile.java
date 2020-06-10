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

import org.abstracthorizon.danube.http.HTTPConnection;

/**
 * This matcher matches only paths that end with &quot;/&quot. If path is
 * matched then welcome file is added as component resource path.
 *
 * @author Daniel Sendula
 */
public class WelcomeFile extends AbstractMatcher {

    /** Welcome file */
    protected String welcomeFile;

    /** Constructor */
    public WelcomeFile() {
    }

    /**
     * Returns welcome file
     * @return welcome file
     */
    public String getWelcomeFile() {
        return welcomeFile;
    }

    /**
     * Sets welcome file
     * @param welcomeFile welcome file
     */
    public void setWelcomeFile(String welcomeFile) {
        this.welcomeFile = welcomeFile;
    }

    /**
     * This matcher checks prefix of requested path. If path starts with given prefix
     * and following character is &quot;/&quot; or path is exactly the same
     * length as prefix then it returns positive match
     * @param httpConnection http connection
     */
    public boolean matches(HTTPConnection httpConnection) {
        String path = httpConnection.getComponentResourcePath();
        return ((path == null) || "/".equals(path));
    }

    /**
     * Adjusts connection for nested connection handler to be invoked
     * @param connection connection to be a used
     */
    public void adjustForInvocation(HTTPConnection httpConnection) {
        String componentPath = httpConnection.getComponentPath();
        if (!componentPath.endsWith("/")) {
            httpConnection.setComponentPath(componentPath);
        }
        httpConnection.setComponentResourcePath(welcomeFile);
    }

}
