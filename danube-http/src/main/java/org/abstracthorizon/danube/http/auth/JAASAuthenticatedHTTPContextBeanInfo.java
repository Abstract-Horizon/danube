/*
 * Copyright (c) 2007 Creative Sphere Limited.
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

import org.abstracthorizon.danube.http.HTTPContextBeanInfo;

/**
 * Bean info for {@link JAASAuthenticatedHTTPContext} class
 *
 * @author Daniel Sendula
 */
public class JAASAuthenticatedHTTPContextBeanInfo extends HTTPContextBeanInfo {

    /**
     * Constructor
     */
    public JAASAuthenticatedHTTPContextBeanInfo() {
        this(JAASAuthenticatedHTTPContext.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    public JAASAuthenticatedHTTPContextBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("sessionManager", "HTTP session manager");

        addProperty("realm", "HTTP digest realm");

        addProperty("loginContextName", "Login context name to be used");

        addProperty("loginContext", "Login context", true, false);

        addProperty("cacheTimeout", "Subject to auth strings cache timeout in milliseconds", true, false);

        addProperty("minimumScanPeriod", "Minimum scan period for purging cache in milliseconds", true, false);

        addProperty("forceAuthorisation", "Will it force authorisation if it isn't required", false, false);
    }

}
