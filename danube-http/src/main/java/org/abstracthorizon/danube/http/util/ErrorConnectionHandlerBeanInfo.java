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
package org.abstracthorizon.danube.http.util;

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link ErrorConnectionHandler} class
 *
 * @author Daniel Sendula
 */
public class ErrorConnectionHandlerBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public ErrorConnectionHandlerBeanInfo() {
        this(ErrorConnectionHandler.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected ErrorConnectionHandlerBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {

    }

}
