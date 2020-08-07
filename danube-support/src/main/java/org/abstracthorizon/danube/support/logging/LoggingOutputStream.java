/*
 * Copyright (c) 2004-2020 Creative Sphere Limited.
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
package org.abstracthorizon.danube.support.logging;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream for logging. An given stream is duplicated to log stream as well.
 *
 * @author Daniel Sendula
 */
public class LoggingOutputStream extends OutputStream {

    /** Original output stream */
    protected OutputStream outputStream;

    /** Log output stream */
    protected OutputStream logOutputStream;

    /** Is logging switched flag */
    protected boolean logging = true;

    /**
     * Constructor
     * @param outputStream output stream
     * @param logOutputStream log output stream
     */
    public LoggingOutputStream(OutputStream outputStream, OutputStream logOutputStream) {
        this.outputStream = outputStream;
        this.logOutputStream = logOutputStream;
    }

    /**
     * Should written bytes from output stream be copied to the output stream
     * @param logging <code>true</code> if logging is on
     */
    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    /**
     * Returns <code>true</code> if logging is on
     * @return <code>true</code> if logging is on
     */
    public boolean isLogging() {
        return logging;
    }

    @Override
    public void close() throws IOException {
         outputStream.close();
    } // close

    @Override
    public void flush() throws IOException {
         outputStream.flush();
    } // flush

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
        if (logging) { logOutputStream.write(b); }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        if (logging) { logOutputStream.write(b, off, len); }
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        if (logging) { logOutputStream.write(b); }
    }
}
