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
package org.abstracthorizon.danube.support.logging.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This interface defines simple log file (output stream) provider that
 * knows how to rotate &quot;logs&quot;
 *
 * @author Daniel Sendula
 */
public interface LogFileRotator {

    /**
     * Returns output stream. This method checks if
     * it is the time for logs to be rotated.
     *
     * @return output stream
     * @throws IOException io exception
     */
    OutputStream logFile() throws IOException;

    /**
     * This method forces log rotation.
     *
     * @throws IOException io exception
     */
    void rotate() throws IOException;
}
