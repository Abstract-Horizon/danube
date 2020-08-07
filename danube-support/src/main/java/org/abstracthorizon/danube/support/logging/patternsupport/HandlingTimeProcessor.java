/*
 * Copyright (c) 2006-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.support.logging.patternsupport;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.support.logging.AccessLogConnectionHandler.DateWrapper;
import org.abstracthorizon.danube.support.logging.util.StringUtil;

/**
 * This processor adds <code>%D</code> and <code>%T</code> parameters
 * handling. <code>%D</code> is amount of milliseconds connection handling lasted
 * while <code>&T</code> shows the same time in seconds
 *
 * @author Daniel Sendula
 */
public class HandlingTimeProcessor implements PatternProcessor {

    /** Cached index of milliseconds parameter */
    protected int millisIndex = -1;

    /** Cached index of seconds parameter */
    protected int secsIndex = -1;

    /**
     * Constructor
     */
    public HandlingTimeProcessor() {
    }

    /**
     * Checks if parameters are present and if so replaces it and caches their indexes
     * @param index next index to be used
     * @param message message to be altered
     */
    public int init(int index, StringBuffer message) {
        millisIndex = -1;
        secsIndex = -1;

        if (message.indexOf("%D") >= 0) {
            StringUtil.replaceAll(message, "%D", "{" + index + "}");
            millisIndex = index;
            index = index + 1;
        }
        if (message.indexOf("%T") >= 0) {
            StringUtil.replaceAll(message, "%T", "{" + index + "}");
            secsIndex = index;
            index = index + 1;
        }
        return index;
    }

    /**
     * Adds parameter values to cached index positions
     * @param connection connection
     * @param array array
     */
    public void process(Connection connection, Object[] array) {
        DateWrapper dateWrapper = (DateWrapper)connection.adapt(DateWrapper.class);
        long time = System.currentTimeMillis() - dateWrapper.getHandlingStarted();
        if (millisIndex >= 0) {
            array[millisIndex] = Long.toString(time);
        }
        if (secsIndex >= 0) {
            array[secsIndex] = Long.toString(time / 1000);
        }
    }

}
