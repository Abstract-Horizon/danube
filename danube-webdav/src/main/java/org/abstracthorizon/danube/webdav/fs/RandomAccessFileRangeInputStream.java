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
package org.abstracthorizon.danube.webdav.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Input stream that is based on {@link RandomAccessFile} and has given
 * offset and length.
 *
 * @author Daniel Sendula
 */
public class RandomAccessFileRangeInputStream extends InputStream {

    /** Random access file reference */
    protected RandomAccessFile raf;

    /** Number of bytes that can be read */
    protected long len;

    /** Initial offset */
    protected long initialOffset;

    /** Mark pointer */
    protected long mark;

    /** Mark length */
    protected long markLen;

    /**
     * Constructor
     * @param file a file
     * @param from initial offset
     * @param length length
     * @throws IOException thrown from {@link RandomAccessFile}
     */
    public RandomAccessFileRangeInputStream(File file, long from, long length) throws IOException {
        raf = new RandomAccessFile(file, "r");
        raf.skipBytes((int)from);
        this.initialOffset = from;
        this.len = length;
    }

    @Override
    public int read() throws IOException {
        if (len == 0) {
            return -1;
        } else {
            len = len - 1;
            return raf.read();
        }
    }

    @Override
    public int read(byte[] buf, int from, int length) throws IOException {
        if (length > len) {
            length = (int)len;
        }
        int r = raf.read(buf, from, length);
        if (r >= 0) {
            len = len - r;
        }
        return r;
    }

    @Override
    public long skip(long l) throws IOException {
        if (l > len) {
            l = (int)len;
        }
        long s = raf.skipBytes((int)l);
        if (s >= 0) {
            len = len - s;
        }
        return s;
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

    @Override
    public void mark(int i) {
        try {
            mark = raf.getFilePointer();
            markLen = len;
        } catch (IOException ignore) {
        }
    }

    @Override
    public void reset() throws IOException {
        raf.seek(mark);
        len = markLen;
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}
