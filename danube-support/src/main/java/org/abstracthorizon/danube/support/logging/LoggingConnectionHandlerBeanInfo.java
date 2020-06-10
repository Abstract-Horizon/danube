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
package org.abstracthorizon.danube.support.logging;

import org.abstracthorizon.pasulj.PasuljInfo;


/**
 * Bean info for {@link LoggingConnectionHandler} class
 *
 * @author Daniel Sendula
 */
public class LoggingConnectionHandlerBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public LoggingConnectionHandlerBeanInfo() {
        this(LoggingConnectionHandler.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected LoggingConnectionHandlerBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {

        addProperty("logging", "Is logging switched on or off");
        addProperty("directional", "Is it direction/readable text logging or not");
        addProperty("tempLogging", "Should all input be logged on the temporary basis");
        addProperty("addressPatternString", "Client socket address pattern string");
        addProperty("logsPath", "Path of log files");
        addProperty("logFileNamePattern", "Log file name pattern string: %c-time millis, %D-date, %T-time, %A-local addr, %P-local port, %a-remote addr, %p-remote port ");

        addProperty("connectionHandler", "Connection handler", true, false);
    }

}
