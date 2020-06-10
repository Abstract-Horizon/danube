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
package org.abstracthorizon.danube.service.server;

import org.abstracthorizon.danube.service.ServiceBeanInfo;

/**
 * Bean info for {@link ServerService} class
 *
 * @author Daniel Sendula
 */
public class ServerServiceBeanInfo extends ServiceBeanInfo {

    /**
     * Constructor
     */
    public ServerServiceBeanInfo() {
        this(ServerService.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected ServerServiceBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("socketAddress", "Socket address of the service", true, false);
        addProperty("connectionHandler", "Connection handler to be invoked", true, false);

        addProperty("port", "Port to listen");
        addProperty("address", "Address of interface to listen on or \"*\" if all interfaces");
    }

}
