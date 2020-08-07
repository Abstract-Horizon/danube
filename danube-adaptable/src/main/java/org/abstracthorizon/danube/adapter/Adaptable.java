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
package org.abstracthorizon.danube.adapter;

/**
 * This interface is inspired by similar from eclipse project
 *
 * @author Daniel Sendula
 */
public interface Adaptable {

    /**
     * Returns an object that is instance of asked class
     * @param cls class to be adapted to
     * @return an object that is instance of asked class
     */
    <T> T adapt(Class<T> cls);

}
