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
package org.abstracthorizon.danube.service;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link Service} class
 *
 * @author Daniel Sendula
 */
public class ServiceBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public ServiceBeanInfo() {
        this(Service.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected ServiceBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        addProperty("name", "Name of the service");
        addProperty("state", "State of the service", true, false);
        addProperty("stateName", "State of the service");

        addMethod("create", "Creates the service", true, false);
        addMethod("start", "Starts the service", true, false);
        addMethod("stop", "Stops the service", true, false);
        addMethod("destroy", "Destroys the service", true, false);

    }
}
