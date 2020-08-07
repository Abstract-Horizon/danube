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
package org.abstracthorizon.danube.http.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * String print writer implementation
 *
 * @author Daniel Sendula
 */
public class StringPrintWriter extends PrintWriter {

    /**
     * Constructor
     *
     */
    public StringPrintWriter() {
        super(new StringWriter());
    }

    /**
     * Retruns string buffer of a print writer
     * @return string buffer of a print writer
     */
    public StringBuffer getStringBuffer() {
        return ((StringWriter)out).getBuffer();
    }

    /**
     * Resets the string buffer by deleting its contents
     *
     */
    public void reset() {
        StringBuffer buf = getStringBuffer();
        buf.delete(0, buf.length());
    }

}
