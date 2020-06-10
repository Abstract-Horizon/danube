/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.http.spring;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;

import org.springframework.context.ApplicationContext;

/**
 * <p>This class is attended for categorisation of top level components.
 * In other words it, currently, represents configuration for &quot;application&quot;
 * within server.
 * </p>
 * <p>This class passes request to another {@link org.abstracthorizon.danube.connection.ConnectionHandler}
 * which is retrieved from another spring framework &quot;application context&quot;.
 * </p>
 *
 * @author Daniel Sendula
 */
public class ApplicationSeparator implements ConnectionHandler {

    /** Name of {@link ConnectionHandler} bean name in given application context */
    protected String beanName;

    /** Application context asked bean must reside it */
    protected ApplicationContext applicationContext;

    /** Cached application's connection handler */
    protected ConnectionHandler applicationsConnectionHandler;

    /** Constructor */
    public ApplicationSeparator() {
    }

    /**
     * Constructor
     * @param applicationContext application context {@link ConnectionHandler} bean is retrieve from
     * @param beanName bean name
     */
    public ApplicationSeparator(ApplicationContext applicationContext, String beanName) {
        this.applicationContext = applicationContext;
        this.beanName = beanName;
        init();
    }

    /**
     * Passes execution flow to obtained {@link ConnectionHandler}
     */
    public void handleConnection(Connection connection) {
        applicationsConnectionHandler.handleConnection(connection);
    }

    /**
     * Obtains {@link ConnectionHandler} from given application context as a bean with given name.
     */
    public void init() {
        applicationsConnectionHandler = (ConnectionHandler)applicationContext.getBean(beanName);
    }

    /**
     * Returns bean name
     * @return bean name
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * Sets bean name
     * @param beanName bean name
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * Returns sub application context bean is to be retrieved from
     * @return sub application context bean is to be retrieved from
     */
    public ApplicationContext getChildContext() {
        return applicationContext;
    }

    /**
     * Sets sub application context bean is to be retrieved from
     * @param sub applicationContext application context bean is to be retrieved from
     */
    public void setChildContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

}
