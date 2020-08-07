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
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * Output stream to a file based on {@link RandomAccessFile} with given length and initial offset.
 *
 * @author Daniel Sendula
 */
public class RandomAccessFileRangeOutputStream extends OutputStream {

    /** Random access file reference */
    protected RandomAccessFile raf;

    /** Length */
    protected long len;

    /**
     * Constructor
     * @param file a file
     * @param from initial offset
     * @param len length
     * @throws IOException thrown from {@link RandomAccessFile}
     */
    public RandomAccessFileRangeOutputStream(File file, long from, long len) throws IOException {
        raf = new RandomAccessFile(file, "rw");
        if (raf.length() < from + len) {
            raf.setLength(from + len);
        }
        raf.skipBytes((int)from);
        this.len = len;
    }

    @Override
    public void write(int i) throws IOException {
        if (len != 0) {
            raf.write(i);
            len = len - 1;
        }

    }

    @Override
    public void write(byte[] buf, int from, int length) throws IOException {
        if (length > len) {
            length = (int)len;
        }
        raf.write(buf, from, length);
        len = len - length;
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        raf.close();
    }

}
