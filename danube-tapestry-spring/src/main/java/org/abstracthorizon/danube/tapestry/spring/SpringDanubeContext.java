/*
 * Copyright (c) 2005-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.tapestry.spring;

import java.io.IOException;
import java.net.URL;

import org.abstracthorizon.danube.support.RuntimeIOException;
import org.abstracthorizon.danube.tapestry.DanubeContext;
import org.apache.tapestry.web.WebContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * {@link WebContext} interface implementation.
 * This implmenetation uses application context everything is created in for
 * obtaning resources.
 *
 * @author Daniel Sendula
 */
public class SpringDanubeContext extends DanubeContext {

    /** Application context to be used for obtaining resources */
    protected ApplicationContext applicationContext;

    /**
     * Constructor
     * @param handler handler
     */
    public SpringDanubeContext(TapestryConnectionHandler handler, ApplicationContext applicationContext) {
        super(handler);
        this.applicationContext = applicationContext;
    }

    /**
     * Returns resource as URL. It uses this classes class loader.
     * @param path resource path. If starts with &quot;/&quot; then it is removed.
     * @return resource as URL.
     * @throws RuntimeIOException
     */
    public URL getResource(String path) throws RuntimeIOException {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        Resource resource = applicationContext.getResource(path);
        try {
            return resource.getURL();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
