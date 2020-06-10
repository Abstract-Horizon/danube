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
package org.abstracthorizon.danube.http.matcher;

/**
 * Bean info for {@link WelcomeFile} class
 *
 * @author Daniel Sendula
 */
public class WelcomeFileBeanInfo extends AbstractMatcherBeanInfo {

    /**
     * Constructor
     */
    public WelcomeFileBeanInfo() {
        this(WelcomeFile.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected WelcomeFileBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        super.init();

        addProperty("welcomeFile", "Welcome file path");
    }

}
