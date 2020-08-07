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
package org.abstracthorizon.danube.mvc;

import org.abstracthorizon.danube.connection.Connection;

/**
 * This interface defines view renderer.
 *
 * @author Daniel Sendula
 */
public interface View {

    /**
     * This metohd renders view on given connection based on supplied view name and model
     * @param connection connection
     * @param modelAndView view name and model map
     */
    public void render(Connection connection, ModelAndView modelAndView);
}
