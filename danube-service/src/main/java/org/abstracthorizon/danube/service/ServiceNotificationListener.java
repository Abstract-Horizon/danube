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
package org.abstracthorizon.danube.service;

/**
 * Service notification listener interface.
 *
 * @author Daniel Sendula
 */
public interface ServiceNotificationListener {

    /**
     * This method is called when service is about to change state
     * @param service service
     * @param newState new state
     */
    void serviceAboutToChangeState(Service service, int newState);

    /**
     * This method is called when service state is changed.
     * @param service service
     * @param oldState previous state
     */
    void serviceChangedState(Service service, int oldState);

}
