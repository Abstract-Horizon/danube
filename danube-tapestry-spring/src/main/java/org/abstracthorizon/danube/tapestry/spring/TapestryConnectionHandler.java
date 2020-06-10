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
package org.abstracthorizon.danube.tapestry.spring;

import org.abstracthorizon.danube.support.RuntimeIOException;
import org.apache.hivemind.Resource;
import org.apache.tapestry.web.WebContext;
import org.apache.tapestry.web.WebContextResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Tapestry connection handler.
 *
 *
 * @author Daniel Sendula
 */
public class TapestryConnectionHandler extends org.abstracthorizon.danube.tapestry.TapestryConnectionHandler
    implements BeanNameAware, ApplicationContextAware {

    /** Application context this view adapter belongs to */
    protected ApplicationContext applicationContext;

    /**
     * Obtains application resource. It first tries with the name from
     * {@link org.abstracthorizon.danube.tapestry.TapestryConnectionHandler#getApplicationSpecificationResourceName()},
     * then with {@link org.abstracthorizon.danube.tapestry.TapestryConnectionHandler#TAPESTRY_DEFAULT_APPLICATION_NAME} and
     * at the end with value of {@link org.abstracthorizon.danube.tapestry.TapestryConnectionHandler#getApplicationSpecificationResourceName()},
     * prefixed with &quot;WEB-INF/&quot;.
     *
     * @return application resource
     */
    protected Resource obtainApplicationResource() {
        String name = getApplicationSpecificationResourceName();

        org.springframework.core.io.Resource resource = applicationContext.getResource(name);
        if (resource.exists()) {
            return new WebContextResource(getWebContext(), name);
        }

        name = TAPESTRY_DEFAULT_APPLICATION_NAME;
        resource = applicationContext.getResource(name);
        if (resource.exists()) {
            return new WebContextResource(getWebContext(), name);
        }

        name = "WEB-INF/" + getApplicationSpecificationResourceName();
        resource = applicationContext.getResource(name);
        if (resource.exists()) {
            return new WebContextResource(getWebContext(), name);
        }
        throw new RuntimeIOException("Cannot find: "
                + "\"" + getApplicationSpecificationResourceName() + "\", "
                + "\"" + TAPESTRY_DEFAULT_APPLICATION_NAME + "\" or "
                + "\"WEB-INF/" + getApplicationSpecificationResourceName() + "\""
                );
    }

    /**
     * Returns web context assigned to this handler. If not already set then {@link SpringDanubeContext}
     * will be instantiated.
     * @return web context
     */
    public WebContext getWebContext() {
        if (webContext == null) {
            webContext = new SpringDanubeContext(this, applicationContext);
        }
        return webContext;
    }

    /**
     * Stores bean name
     * @param beanName bean name
     */
    public void setBeanName(String beanName) {
        applicationSpecificationResourceName = beanName + TAPESTRY_SUFFIX;

    }

    /**
     * Stores current application context
     * @param context application context
     * @throws BeansException never
     */
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.applicationContext = context;
    }

}
