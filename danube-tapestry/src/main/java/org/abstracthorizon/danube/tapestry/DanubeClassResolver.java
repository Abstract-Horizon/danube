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
package org.abstracthorizon.danube.tapestry;

import java.net.URL;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;

/**
 * {@link ClassResolver} implementation that uses supplied class loader
 *
 *
 * @author Daniel Sendula
 */
public class DanubeClassResolver implements ClassResolver {

    /** Class loader */
    protected ClassLoader classLoader;

    /**
     * Returns resource as url. It calls {@link ClassLoader#getResource(String)}.
     * @return resource's url
     */
    public URL getResource(String name) {
        return classLoader.getResource(name);
    }

    /**
     * Finds a class from given name. Calls {@link ClassLoader#loadClass(String)}.
     * @param name class name
     * @return class
     * @throws ApplicationRuntimeException
     */
    public Class<?> findClass(String name) {
        try {
            return classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    /**
     * Checks if class is available for load. If it is class would be returned.
     * if not it returns <code>null</code>.
     * @param name class name
     * @return class or <code>null</code>
     */
    public Class<?> checkForClass(String name) {
        try {
            return findClass(name);
        } catch (ApplicationRuntimeException e) {
            return null;
        }
    }

    /**
     * Returns class loader
     * @return class loader
     */
    public ClassLoader getClassLoader() {
        return classLoader;
    }

}
