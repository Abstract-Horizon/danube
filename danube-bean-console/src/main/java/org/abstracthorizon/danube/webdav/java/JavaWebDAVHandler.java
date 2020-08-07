/*
 * Copyright (c) 2006-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.webdav.java;

import org.abstracthorizon.danube.http.HTTPConnection;
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.webdav.BaseWebDAVResourceConnectionHandler;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This is convenient extension of {@link BaseWebDAVResourceConnectionHandler} that sets
 * java resource adapter.
 *
 * @author Daniel Sendula
 */
public class JavaWebDAVHandler extends BaseWebDAVResourceConnectionHandler implements ApplicationContextAware {

    /** Java resource adapter */
    protected JavaWebDAVResourceAdapter javaAdapter;

    /**
     * Constructor
     */
    public JavaWebDAVHandler() {
        javaAdapter = new JavaWebDAVResourceAdapter();
        setWebDAVResourceAdapter(javaAdapter);
    }

    /**
     * Sets application context of the object. It uses application context to set.
     * If this class is used outside of spring framework then use {@link #setRootObject(Object) method.
     *
     * java adapter's root object
     * @param context application context this object is created in.
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (javaAdapter.getRootObject() == null) {
            while (context.getParent() != null) {
                context = context.getParent();
            }
            javaAdapter.setRootObject(context);
        }
    }

    /**
     * Returns root object used for the handler
     * @return root object
     */
    public Object getRootObject() {
        return javaAdapter.getRootObject();
    }

    /**
     * Sets the root object to be used with java resource adapter
     * @param root root object
     */
    public void setRootObject(Object root) {
        javaAdapter.setRootObject(root);
    }

    /**
     * Redirects POST HTTP method to GET
     * @param connection connection
     */
    public void methodPOST(HTTPConnection connection) {
        String path = connection.getComponentResourcePath();
        if (path.endsWith("/")) {
            connection.setComponentResourcePath(IOUtils.addPaths(path, "index.html"));
        }
        methodGET(connection);
    }
}
