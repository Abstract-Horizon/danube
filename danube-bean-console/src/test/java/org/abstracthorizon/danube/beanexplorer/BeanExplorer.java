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
package org.abstracthorizon.danube.beanexplorer;

import org.abstracthorizon.danube.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This class starts beean explorer test suite. It works of xml configuration files
 * starting with server.xml from the root of the class path.
 *
 * @author Daniel Sendula
 */
public class BeanExplorer {

    /** Logger */
    protected static Logger logger = LoggerFactory.getLogger(BeanExplorer.class);

    /**
     * Main class to be started
     * @param args not used
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        logger.debug("Starting...");

        ApplicationContext rootContext = new ClassPathXmlApplicationContext("server.xml");
        Service service = (Service)rootContext.getBean("server");

        service.create();
        service.start();

        rootContext.getBean("testBean");
    }



}
