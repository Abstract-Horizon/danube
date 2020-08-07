/*
 * Copyright (c) 2005-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.http;

/**
 * Bean info for {@link HTTPContext} class
 *
 * @author Daniel Sendula
 */
public class HTTPContextBeanInfo extends SelectorBeanInfo {

    /**
     * Constructor
     */
    public HTTPContextBeanInfo() {
        this(HTTPContext.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected HTTPContextBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();
   }

}
