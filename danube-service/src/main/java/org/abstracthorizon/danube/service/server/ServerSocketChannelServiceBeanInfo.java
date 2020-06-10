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
 * Bean info for {@link ServerSocketChannelService} class
 *
 * @author Daniel Sendula
 */
public class ServerSocketChannelServiceBeanInfo extends ServiceBeanInfo {

    /**
     * Constructor
     */
    public ServerSocketChannelServiceBeanInfo() {
        this(ServerSocketChannelService.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected ServerSocketChannelServiceBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("port", "Port this service is going to listen on");
        addProperty("serverSocketTimeout", "Server socket timeout");
        addProperty("newSocketTimeout", "New socket timeout");
        addProperty("graceFinishPeriod", "Grace period for connections to finish after service state changes to STOPPING");
        addProperty("executor", "Executor (thread pool) to be used", true, false);
        addProperty("connectionHandler", "Connection handler new connection to be handed with", true, false);
        addProperty("activeConnections", "Set of active connections");
    }

}
