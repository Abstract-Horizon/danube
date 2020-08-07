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
 * Bean info for {@link MultiThreadServerSSLSocketService} class
 *
 * @author Daniel Sendula
 */
public class MultiThreadServerSSLSocketServiceBeanInfo extends MultiThreadServerSocketServiceBeanInfo {

    /**
     * Constructor
     */
    public MultiThreadServerSSLSocketServiceBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected MultiThreadServerSSLSocketServiceBeanInfo() {
        this(MultiThreadServerSSLSocketService.class);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("keyStoreURL", "Defined URL keystore is read from");
        addProperty("keyStoreFile", "Defines file (as read from URL) keystore is to be read from and writen to");
        addProperty("keyStorePassword", "Keystore password");
    }

}
