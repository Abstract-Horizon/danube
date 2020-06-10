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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * This application context extension allows resources to be created using supplied location (a resource).
 *
 * @author Daniel Sendula
 */
public class ResourceXmlApplicationContext extends FileSystemXmlApplicationContext implements ApplicationContextAware {

    /** Logger */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /** Location to be used */
    protected Resource location;

    /** Resource loader */
    protected ResourceLoader resourceLoader;

    /**
     * Consturctor
     * @param location location to be used for resources
     * @param configLocation config locations
     * @throws BeansException
     */
    public ResourceXmlApplicationContext(String location, String configLocation) throws BeansException {
        this(location, new String[]{configLocation}, false);
        refresh();
    }

    /**
     * Constructor
     * @param location location to be used for resources
     * @param configLocations config locations
     * @throws BeansException
     */
    public ResourceXmlApplicationContext(String location, String configLocations[]) throws BeansException {
        this(location, configLocations, false);
        refresh();
    }

    /**
     * Constructor
     * @param location location to be used for resources
     * @param configLocations config locations
     * @throws BeansException
     */
    public ResourceXmlApplicationContext(String location, String configLocations[], boolean refresh) throws BeansException {
        super(configLocations, false);
        this.location = normaliseResource(getResource(location));
        if (refresh) {
            refresh();
        }
    }

    /**
     * Constructor
     * @param location location to be used for resources
     * @param configLocations config locations
     * @param parent parent application context
     * @throws BeansException
     */
    public ResourceXmlApplicationContext(String location, String configLocations[], ApplicationContext parent) throws BeansException {
        this(location, configLocations, false, parent);
        refresh();
    }

    /**
     * Constructor
     * @param location location to be used for resources
     * @param configLocations config locations
     * @param refresh to refresh in constructor
     * @param parent parent context
     * @throws BeansException
     */
    public ResourceXmlApplicationContext(String location, String configLocations[], boolean refresh, ApplicationContext parent) throws BeansException {
        super(configLocations, false, parent);
        this.location = normaliseResource(getResource(location));
        if (refresh) {
            refresh();
        }
    }

    /**
     * Getting resource by given path
     *
     * @param path path
     *
     * @return new resource created of given location (if supplied)
     */
    protected Resource getResourceByPath(String path) {
        if (location == null) {
            return super.getResourceByPath(path);
        } else {
            try {
                Resource r = location.createRelative(path);
                return r;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * This method normalises the resource adding final &quot;/&quot;
     * to paths (not files)
     * @param r resource
     * @return new resource
     */
    protected Resource normaliseResource(Resource r) {
        if (r instanceof FileSystemResource) {
            try {
                String path = r.getFile().getCanonicalPath();
                if (!path.endsWith("/")) {
                    path = path + "/";
                }
                r = new FileSystemResource(path);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return r;
    }


    /**
     * Sets current application context bean is to be retrieved from
     * @param applicationContext application context bean is to be retrieved from
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        ConfigurableBeanFactory configurableBeanFactory = getBeanFactory();
        configurableBeanFactory.registerSingleton("parent-context", applicationContext);
    }

}
