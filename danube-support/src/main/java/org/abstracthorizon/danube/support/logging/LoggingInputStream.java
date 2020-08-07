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
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A class that duplicates what is read from InputStream to OutputStream
 *
 * @author Daniel Sendula
 */
public class LoggingInputStream extends InputStream {

    /** Original input stream */
    protected InputStream inputStream;

    /** Log output stream */
    protected OutputStream logOutputStream;

    /** Mark poitner */
    protected int marked = -1;

    /** Pointer in the stream */
    protected int ptr = 0;

    /** Is logging switched on flag */
    protected boolean logging = true;

    /**
     * Constructor
     * @param inputStream input stream to be logged
     * @param logOutputStream log output stream
     */
    public LoggingInputStream(InputStream inputStream, OutputStream logOutputStream) {
        this.inputStream = inputStream;
        this.logOutputStream = logOutputStream;
    }

    /**
     * Should read bytes from input stream be copied to the output stream
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
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public void mark(int readlimit) {
        inputStream.mark(readlimit);
        marked = marked - ptr;
        ptr = 0;
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public int read() throws IOException {
        int i = inputStream.read();
        if (i >= 0) {
            if (ptr >= marked) {
                if (logging) { logOutputStream.write(i); }
                marked = marked + 1;
            }
            ptr = ptr + 1;
        }
        return i;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = inputStream.read(b, off, len);
        if (i > 0) {
            if (ptr >= marked) {
                if (logging) { logOutputStream.write(b, off, i); }
                marked = marked + i;
            } else if (ptr + i >= marked) {
                int skip = marked - ptr;
                if (i-skip > 0) {
                    if (logging) { logOutputStream.write(b, off+skip, i-skip); }
                }
                marked = marked + i-skip;
            }
            ptr = ptr + i;
        }
        return i;
    }

    @Override
    public void reset() throws IOException {
        inputStream.reset();
        ptr = 0;
    }

    @Override
    public long skip(long n) throws IOException {
        ptr = ptr + (int)n; // TODO
        return inputStream.skip(n);
    }
}
