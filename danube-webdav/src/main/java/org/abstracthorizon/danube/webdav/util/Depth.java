/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.webdav.util;

import org.abstracthorizon.danube.http.HTTPConnection;

/**
 * Object that represents a &quot;Depth&quot; header of WebDAV specification.
 *
 * @author Daniel Sendula
 */
public class Depth {

    /** Depth not defined */
    public static final int NONE = -1;

    /** Depth of 0 */
    public static final int ZERO = 0;

    /** Depth of 1 */
    public static final int ONE = 1;

    /** Depth of infinity */
    public static final int INFINITY = 2;

    /**
     * Parses the header (if present) and returns depth object
     * @param connection connection to hold the header
     * @return one of static objects defined here
     */
    public static int collectDepth(HTTPConnection connection) {
        String depthHeader = connection.getRequestHeaders().getOnly("Depth");
        if (depthHeader == null) {
            return NONE;
        } else if ("0".equals(depthHeader)) {
            return ZERO;
        } else if ("1".equals(depthHeader)) {
            return ONE;
        } else if ("infinity".equals(depthHeader)) {
            return INFINITY;
        } else {
            return NONE;
        }
    }

    /**
     * String representation of a depth object
     * @param depth depth object
     * @return representation of a depth object
     */
    public static String toString(int depth) {
        if (depth == NONE) {
            return "NONE";
        } else if (depth == ZERO) {
            return "0";
        } else if (depth == ONE) {
            return "1";
        } else if (depth == INFINITY) {
            return "infinity";
        } else {
            return "NONE";
        }
    }
}
