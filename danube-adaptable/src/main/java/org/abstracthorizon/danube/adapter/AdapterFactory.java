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
package org.abstracthorizon.danube.adapter;

/**
 * This interface is insipired by the similar from eclipse project
 *
 * @author Daniel Sendula
 */
public interface AdapterFactory {

    /**
     * Adopts given object to the instance of the asked class
     * @param object object to he adopted
     * @param cls asked class
     * @return adopted given object to the instance of the asked class
     */
    <T> T adapt(T object, Class<T> cls);

    /**
     * Returns list of classes to which given object can be adopted to by this adopter factory
     * @return list of classes to which given object can be adopted to
     */
    <T> Class<T>[] getAdaptingClasses(T object);

}
