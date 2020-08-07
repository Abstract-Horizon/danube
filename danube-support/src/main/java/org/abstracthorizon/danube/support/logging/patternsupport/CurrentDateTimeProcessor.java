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
import org.abstracthorizon.danube.support.logging.util.StringUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This processor adds <code>%t</code> parameter handling replacing it with the
 * current date and time in Common Log Format (see Apache HTTP server)
 *
 * @author Daniel Sendula
 */
public class CurrentDateTimeProcessor implements PatternProcessor {

    /** Common log format */
    public static final DateFormat COMMON_LOG_FORMAT = new SimpleDateFormat("[dd/MMM/yyyy:hh:mm:ss Z]");

    /** Common log format */
    public static final DateFormat DATETIME_LOG_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /** Index of date field */
    protected int dateIndex = -1;

    /** Index of date time fields */
    protected int dateTimeIndex = -1;

    /**
     * Constructor
     */
    public CurrentDateTimeProcessor() {
    }

    /**
     * Checks if parameter is present and if so replaces it and caches its index
     * @param index next index to be used
     * @param message message to be altered
     */
    public int init(int index, StringBuffer message) {
        dateIndex = -1;

        if (message.indexOf("%t") >= 0) {
            StringUtil.replaceAll(message, "%t", "{" + index + "}");
            dateIndex = index;
            index = index + 1;
        } else if (message.indexOf("%y") >= 0) {
            StringUtil.replaceAll(message, "%y", "{" + index + "}");
            dateTimeIndex = index;
            index = index + 1;
        }
        return index;
    }

    /**
     * Adds current time and date in common log format at cached index into the array
     * @param connection connection
     * @param array array
     */
    public void process(Connection connection, Object[] array) {
        if (dateIndex >= 0) {
            String nowString = COMMON_LOG_FORMAT.format(new Date());
            array[dateIndex] = nowString;
        } else if (dateTimeIndex >= 0) {
            String nowString = DATETIME_LOG_FORMAT.format(new Date());
            array[dateTimeIndex] = nowString;
        }
    }

}
