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

import org.abstracthorizon.danube.service.server.ServerConnectionHandlerBeanInfo;

/**
 * Bean info for {@link HTTPServerConnectionHandler} class
 *
 * @author Daniel Sendula
 */
public class HTTPServerConnectionHandlerBeanInfo extends ServerConnectionHandlerBeanInfo {

    /**
     * Constructor
     */
    public HTTPServerConnectionHandlerBeanInfo() {
        this(HTTPServerConnectionHandler.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected HTTPServerConnectionHandlerBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("errorHandler", "Error response connection handler", true, false);
        addProperty("defaultBufferSize", "Default buffer size", true, false);
    }

}
