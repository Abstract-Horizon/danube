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
package org.abstracthorizon.danube.support.logging;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link AccessLogConnectionHandler} class
 *
 * @author Daniel Sendula
 */
public class AccessLogConnectionHandlerBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public AccessLogConnectionHandlerBeanInfo() {
        this(AccessLogConnectionHandler.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected AccessLogConnectionHandlerBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        addProperty("logPattern", "Log pattern. Use following: %A-local IP, %a-remote IP, %p-local port, %h-remote host name, %T-time secs, %D-time millis, %t-datetime in commong log format");

        addProperty("connectionHandler", "Connection handler", true, false);
        addProperty("logFileRotator", "Log file rotator", true, false);
    }

}
