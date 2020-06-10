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
package org.abstracthorizon.danube.support.logging.util;

/**
 * This is simple string utility class
 *
 * @author Daniel Sendula
 */
public class StringUtil {

    /**
     * This method replaces all instances of given string with another within string buffer
     *
     * @param buffer string buffer
     * @param what what is to be replaces
     * @param with what to be replaced for
     */
    public static void replaceAll(StringBuffer buffer, String what, String with) {
        int i = buffer.indexOf(what);
        while (i >= 0) {
            buffer.replace(i, i + what.length(), with);
            i = buffer.indexOf(what, i + with.length());
        }
    }

}
