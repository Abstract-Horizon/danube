/*
 * Copyright (c) 2007-2020 Creative Sphere Limited.
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


/**
 * Bean info for {@link SimpleSessionManager} class
 *
 * @author Daniel Sendula
 */
public class SimpleSessionManagerBeanInfo extends SimpleCookieSessionManagerBeanInfo {

    /**
     * Constructor
     */
    public SimpleSessionManagerBeanInfo() {
        this(SimpleSessionManager.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected SimpleSessionManagerBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("sessionTimeout", "SessionTtimeout");
        addProperty("minScanInterval", "Minimal interval in milliseconds for pruning sessions from the session cache", true, false);
    }

}

