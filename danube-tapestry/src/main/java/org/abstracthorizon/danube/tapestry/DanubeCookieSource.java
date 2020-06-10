/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.tapestry;

import org.apache.tapestry.services.CookieSource;

/**
 * Danube cookie source.
 *
 *
 * @author Daniel Sendula
 */
public class DanubeCookieSource implements CookieSource {


    /**
     * Reads cookie value
     * @param name cookie name
     * @return cookie value
     */
    public String readCookieValue(String name) {
        return null;
    }

    /**
     * Stores cookie value
     * @param name cookie name
     * @param value value
     */
    public void writeCookieValue(String name, String value) {
        System.err.println("DanubeCookieSource.writeCookieValue: " + name + "=" + value);
    }

    /**
     * Stores cookie value
     * @param name cookie name
     * @param value value
     * @param maxAge age of the cookie
     */
    public void writeCookieValue(String name, String value, int maxAge) {
        System.err.println("DanubeCookieSource.writeCookieValue: " + name + "=" + value + "; age=" + maxAge);
    }

    /**
     * Removes stored cookie
     * @param name cookie name
     */
    public void removeCookieValue(String name) {
    }

}
