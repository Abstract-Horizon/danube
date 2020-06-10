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
package org.abstracthorizon.danube.http.cookie;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link Cookie} class
 *
 * @author Daniel Sendula
 */
public class CookieBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public CookieBeanInfo() {
        this(Cookie.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected CookieBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     * @param cls class
     */
    public void init() {
        addProperty("name", "Cookie name");
        addProperty("value", "Cookie value");
        addProperty("date", "Date cookie is going expire");
        addProperty("domain", "Cookie domain");
        addProperty("path", "domain");
        addProperty("path", "Cookie path");
        addProperty("secure", "Is cookie secure");
        addProperty("", "");
    }
}
