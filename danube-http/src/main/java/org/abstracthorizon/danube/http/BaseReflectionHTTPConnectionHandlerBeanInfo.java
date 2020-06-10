/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
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

import org.abstracthorizon.pasulj.PasuljInfo;

/**
 * Bean info for {@link BaseReflectionHTTPConnectionHandler} class
 *
 * @author Daniel Sendula
 */
public class BaseReflectionHTTPConnectionHandlerBeanInfo extends PasuljInfo {

    /**
     * Constructor
     */
    public BaseReflectionHTTPConnectionHandlerBeanInfo() {
        this(BaseReflectionHTTPConnectionHandler.class);
    }

    /**
     * Constructor
     * @param cls class
     */
    protected BaseReflectionHTTPConnectionHandlerBeanInfo(Class<?> cls) {
        super(cls);
    }

    /**
     * Init method
     */
    public void init() {
        addProperty("errorResponse", "Error response connection handler", true, false);
        addProperty("noDefaultHead", "Flag that shows should default HEAD handling method be included or not", true, false);
        addProperty("noDefaultTrace", "Flag that shows should default TRACE handling method be included or not", true, false);
    }

}
