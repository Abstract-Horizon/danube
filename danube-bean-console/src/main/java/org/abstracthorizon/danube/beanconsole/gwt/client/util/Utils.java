/*
 * Copyright (c) 2009 Creative Sphere Limited.
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
package org.abstracthorizon.danube.beanconsole.gwt.client.util;

/**
 *
 * @author Daniel Sendula
 */
public class Utils {

    public static String typeFromPrefix(String prefix) {
        if (prefix.startsWith("b_")) {
            return "beans";
        } else if (prefix.startsWith("i_")) {
            return "collection";
        } else if (prefix.startsWith("k_")) {
            return "map";
        } else if (prefix.startsWith("p_")) {
            return "properties";
        } else if (prefix.startsWith("m_")) {
            return "methods";
        } else if (prefix.startsWith("s_")) {
            return "saved";
        }
        return prefix;
    }
    
    public static String concatenatePaths(String path1, String path2) {
        if (path1.endsWith("/")) {
            if (path2.startsWith("/")) {
                return path1 + path2.substring(1);
            } else {
                return path1 + path2;
            }
        } else {
            if (path2.startsWith("/")) {
                return path1 + path2;
            } else {
                return path1 + "/" + path2;
            }
        }
    }
    
}
