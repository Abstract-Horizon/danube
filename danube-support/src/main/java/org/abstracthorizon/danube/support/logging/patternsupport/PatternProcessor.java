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
package org.abstracthorizon.danube.support.logging.patternsupport;

import org.abstracthorizon.danube.connection.Connection;

/**
 * This interface defines pattern processor.
 *
 * @author Daniel Sendula
 */
public interface PatternProcessor {

    /**
     * Checks if parameters are present and if so replaces it and caches their indexes
     * @param index next index to be used
     * @param message message to be altered
     */
    int init(int index, StringBuffer message);

    /**
     * Adds parameter values to cached index positions
     * @param connection connection
     * @param array array
     */
    void process(Connection connection, Object[] array);

}
