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

/**
 * Bean info for {@link MultiThreadServerService} class
 *
 * @author Daniel Sendula
 */
public class MultiThreadServerServiceBeanInfo extends ServerServiceBeanInfo {

    /**
     * Constructor
     */
    public MultiThreadServerServiceBeanInfo() {
        this(MultiThreadServerService.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected MultiThreadServerServiceBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("graceFinishPeriod", "Grace period for connections to finish after service state changes to STOPPING");
        addProperty("executor", "Executor (thread pool) to be used", true, false);
        addProperty("activeConnections", "Current, active connections");
        addProperty("numberOfActiveConnections", "Number of current, active connections");

    }

}
