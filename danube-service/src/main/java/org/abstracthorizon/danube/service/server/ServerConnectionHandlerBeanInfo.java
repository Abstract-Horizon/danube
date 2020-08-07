/*
 * Copyright (c) 2007-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.service.server;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link ServerConnectionHandler} class
 *
 * @author Daniel Sendula
 */
public class ServerConnectionHandlerBeanInfo extends PasuljInfo {

    /**
     * Constructor
     * @param cls class
     */
    protected ServerConnectionHandlerBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Constructor
     */
    public ServerConnectionHandlerBeanInfo() {
        this(ServerConnectionHandler.class);
    }

    /**
     * Init method
     */
    public void init() {
        addProperty("connectionHandler", "Connection handler to be invoked");

    }

}
