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
package org.abstracthorizon.danube.http.spring.util;

import java.io.File;
import java.io.IOException;

import org.abstracthorizon.danube.support.RuntimeIOException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

/**
 * Connection handler that handles static files (and directories)
 *
 * @author Daniel Sendula
 */
public class FileConnectionHandler extends org.abstracthorizon.danube.http.util.ReadOnlyFileConnectionHandler implements ApplicationContextAware{

    /** Constructor */
    public FileConnectionHandler() {
    }

    /**
     * Sets path where files are stored
     * @param filePath file path
     */
    public void setFilePath(File filePath) {
        this.filePath = filePath;
    }

    /**
     * Sets path where files are stored
     * @return file path
     */
    public File getFilePath() {
        return filePath;
    }

    /**
     * This method sets file path to file path obtained from application context
     * (through {@link ApplicationContext#getResource(String)})
     * @param context application context
     * @throws BeansException
     * @throws RuntimeIOException
     */
    public void setApplicationContext(ApplicationContext context) throws BeansException, RuntimeIOException {
        Resource resource = context.getResource(filePath.getPath());
        if (resource.exists()) {
            try {
                super.setFilePath(resource.getFile());
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
    }


}
