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
package org.abstracthorizon.danube.support.logging.util;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link LogFileRotatorImpl} class
 *
 * @author Daniel Sendula
 */
public class LogFileRotatorImplBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public LogFileRotatorImplBeanInfo() {
        this(LogFileRotatorImpl.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected LogFileRotatorImplBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        addProperty("logFile", "Log file. Set this or logDirectory + logFileName");
        addProperty("logDirectory", "Path to log file. Set together with logFileName");
        addProperty("logFileName", "Name part of the log file. Set together with logDirectory");
        addProperty("numberOfGenerations", "Number of generations log file will be maintained for, -1 if unlimited or 0 for none");
        addProperty("maxSize", "Max log file size before new generation is made or -1 if unlimited");
        addProperty("maxAge", "Max age in milliseconds before new generation is made or -1 if unlimited");
        addProperty("timeOfDay", "Time of the day in the form of a calendar when new generation is going to be created. May be null.");

        addProperty("bufferLen", "Buffer len for output file", true, false);
        addProperty("checkDelayMillis", "Delay before checks are made", true, false);
    }

}
