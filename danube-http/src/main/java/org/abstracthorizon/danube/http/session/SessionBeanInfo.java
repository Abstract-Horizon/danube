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
package org.abstracthorizon.danube.http.session;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link Session} class
 *
 * @author Daniel Sendula
 */
public class SessionBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public SessionBeanInfo() {
        this(Session.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected SessionBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        addProperty("sessionId", "Session id");
        addProperty("attributes", "Attributes");
        addProperty("lastAccessed", "Last accessed system time in milliseconds");
    }

}
