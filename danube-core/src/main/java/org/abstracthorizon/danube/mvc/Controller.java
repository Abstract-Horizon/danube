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
package org.abstracthorizon.danube.mvc;

import org.abstracthorizon.danube.connection.Connection;

/**
 * This interface defines an action.
 *
 * @author Daniel Sendula
 */
public interface Controller {

    /**
     * This method processes action request.
     * @param connection connection action was invoked over
     * @return model and view response
     */
    ModelAndView handleRequest(Connection connection);
}
