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
package org.abstracthorizon.danube.http;

import org.abstracthorizon.danube.mvc.MVCConnectionHandlerBeanInfo;

/**
 * Bean info for {@link HTTPMVCConnectionHandler} class
 *
 * @author Daniel Sendula
 */
public class HTTPMVCConnectionHandlerBeanInfo extends MVCConnectionHandlerBeanInfo {

    /**
     * Constructor
     */
    public HTTPMVCConnectionHandlerBeanInfo() {
        this(HTTPMVCConnectionHandler.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected HTTPMVCConnectionHandlerBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();
    }

}
