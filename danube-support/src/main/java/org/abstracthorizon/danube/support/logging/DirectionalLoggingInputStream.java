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
 * Outputs reading from the input stream to log output stream showing directional marks &quot;&gt;&quot;.
 * Given input stream read bytes are duplicated to log stream.
 *
 * @author Daniel Sendula
 */
public class DirectionalLoggingInputStream extends LoggingInputStream {

    /** Internal flag to show when to output directional char */
    protected boolean flag = true;

    /**
     * Constructor
     * @param inputStream input stream to be logged
     * @param logOutputStream log output stream
     */
    public DirectionalLoggingInputStream(InputStream inputStream, OutputStream logOutputStream) {
        super(inputStream, logOutputStream);
    }

    @Override
    public int read() throws IOException {
        int i = inputStream.read();
        if (i >= 0) {
            if (ptr >= marked) {
                if (logging) { flag = output(flag, logOutputStream, '>', i); }
                marked = marked + 1;
            }
            ptr = ptr + 1;
        }
        return i;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int i = inputStream.read(b, off, len);
        if (i > 0) {
            if (ptr >= marked) {
                if (logging) { flag = output(flag, logOutputStream, '>', b, off, i); }
                marked = marked + i;
            } else if (ptr + i >= marked) {
                int skip = marked - ptr;
                if (i-skip > 0) {
                    if (logging) { flag = output(flag, logOutputStream, '>', b, off+skip, i-skip); }
                }
                marked = marked + i-skip;
            }
            ptr = ptr + i;
        }
        return i;
    }

    /**
     * Outputs a byte making sure that direction character is printed too
     * @param flag flag
     * @param out output stream
     * @param direction direction char
     * @param i byte to be sent to output stream
     * @return flag
     * @throws IOException io exception
     */
    public static boolean output(boolean flag, OutputStream out, char direction, int i) throws IOException {
        if (flag) {
            out.write(direction);
            out.write(' ');
        }
        flag = false;
        out.write(i);
        if (i == '\n') {
            flag = true;
        }
        return flag;
    }

    /**
     * Outputs part of an array scanning it for LF and sending direction chars accodingly
     * @param flag flag
     * @param out output stream
     * @param direction direction
     * @param buf buffer to be sent to output stream
     * @param off offset in the buffer
     * @param len length of data to be sent
     * @return flag
     * @throws IOException io exception
     */
    public static boolean output(boolean flag, OutputStream out, char direction, byte[] buf, int off, int len) throws IOException {
        if (len == 0) {
            return flag;
        }

        int start = off;
        for (int i = off; i < off + len; i++) {
            if (buf[i] == '\n') {
                if (flag) {
                    out.write(direction);
                    out.write(' ');
                }
                out.write(buf, start, i - start + 1);
                start = i + 1;
                flag = true;
            }
        }
        if (start < off + len) {
            if (flag) {
                out.write(direction);
                out.write(' ');
            }
            out.write(buf, start, off + len - start);
            flag = false;
        }
        return flag;
    }

}
