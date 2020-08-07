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

/**
 * Bean info for {@link MultiThreadServerSocketService} class
 *
 * @author Daniel Sendula
 */
public class MultiThreadServerSocketServiceBeanInfo extends MultiThreadServerServiceBeanInfo {

    /**
     * Constructor
     */
    public MultiThreadServerSocketServiceBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected MultiThreadServerSocketServiceBeanInfo() {
        this(MultiThreadServerSocketService.class);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("serverSocketTimeout", "Server socket timeout");
        addProperty("newSocketTimeout", "New socket timeout");
    }

}
