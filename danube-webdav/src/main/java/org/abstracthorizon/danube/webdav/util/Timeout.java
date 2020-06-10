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

/**
 * Object that represents a &quot;Timout&quot; header of WebDAV specification.
 *
 * @author Daniel Sendula
 */
public class Timeout {

    /** Infinite timeout */
    public static final Timeout INFINITE = new Timeout(-1);

    /** Number of seconds */
    protected int seconds;

    /**
     * Constructor
     * @param timeout timeout in seconds
     */
    public Timeout(int timeout) {
        this.seconds = timeout;
    }

    /**
     * Retunrs this object's representation as required by the header
     * @return this object's representation as required by the header
     */
    public String asString() {
        if (seconds == -1) {
            return "Infinite";
        } else {
            return "Second-" + seconds;
        }
    }

    /**
     * Returns string representation of the object
     * @return string representation of the object
     */
    public String toString() {
        if (seconds == -1) {
            return "Timeout[Infinite]";
        } else {
            return "Timeout[seconds-" + seconds + "]";
        }
    }

    public long calculateValidity() {
        long now = System.currentTimeMillis();
        now = now + seconds * 1000;
        return now;
    }

    public static Timeout parse(String s) {
        if ("Infinite".equals(s)) {
            return INFINITE;
        }
        if (s.startsWith("Second-") && (s.length() > 7)) {
            try {
                int timeout = Integer.parseInt(s.substring(7));
                return new Timeout(timeout);
            } catch (NumberFormatException ignore) {
            }
        }
        return null;
    }
}
