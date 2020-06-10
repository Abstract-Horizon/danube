/*
 * Copyright (c) 2006-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.http;


import java.io.IOException;
import java.io.InputStream;

/**
 * This is buffered http input stream. It knows how to read chuncked encoding.
 *
 * @author Daniel Sendula
 */
public class HTTPBufferedInputStream extends InputStream {

    /** Default buffer size to be used when operating in not buffered mode (close and skip methods, for instance) */
    public static final int DEFAULT_BUFFER_SIZE = 2048;

    /** Buffer */
    protected byte[] buffer;

    /** Pointer in the buffer */
    protected int ptr;

    /** Amount of bytes in the buffer */
    protected int len;

    /** Mark */
    protected int mark = -1;

    /** Input stream */
    protected InputStream inputStream;

    /** Buffer size */
    protected int bufferSize;

    /** Is closed */
    protected boolean closed = false;

    /** Content length limit */
    protected long contentLength = 0;

    /** Number of sent bytes */
    protected int readbytes = 0;

    /** Chunk encoding */
    protected boolean chunkEncoding = false;

    /** Current chunk size */
    protected int chunkSize = 0;

    /**
     * Constructor
     *
     * @param intputStream wrapped input stream
     * @param defaultBufferSize default buffer size
     */
    public HTTPBufferedInputStream(InputStream inputStream, int defaultBufferSize) {
        this.inputStream = inputStream;
        this.bufferSize = defaultBufferSize;
    }

    /**
     * Resets internals
     */
    public void resetInternals() {
        closed = false;
        ptr = 0;
        contentLength = 0;
        readbytes = 0;
        chunkEncoding = false;
        len = 0;
        mark = -1;
    }

    /**
     * Returns buffer size
     *
     * @return buffer size
     */
    public int getBufferSize() {
        return bufferSize;
    }

    /**
     * Sets buffer size
     *
     * @param size buffer size
     * @throws IOException io exception
     */
    public void setBufferSize(int size) {
        bufferSize = size;
    }

    /**
     * Returns limited content length
     * @return limited content length
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * Sets content length
     * @param contentLength content length
     */
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Returns if chunk encoding
     * @return if chunk encoding
     */
    public boolean isChunkEncoding() {
        return chunkEncoding;
    }

    /**
     * Sets chunk encoding
     * @param chunkEncoding is chunk encoding
     */
    public void setChunkEncoding(boolean chunkEncoding) {
        this.chunkEncoding = chunkEncoding;
    }

    /**
     * Read chunk size
     * @return chunk size
     * @throws IOException
     */
    protected int readChunkSize() throws IOException {
        int size = 0;
        int i = inputStream.read();
        while ((i > 0)
                && ((i >= '0') && (i <= '9')
                        || ((i >= 'a') && (i <= 'f'))
                        || ((i >= 'A') && (i <= 'F'))))
        {
            if ((i >= '0') && (i <= '9')) {
                size = size * 16 + (i - '0');
            } else if ((i >= 'a') && (i <= 'f')) {
                size = size * 16 + (i - 'a' + 10);
            } else if ((i >= 'A') && (i <= 'F')) {
                size = size * 16 + (i - 'A' + 10);
            }
            i = inputStream.read();
        }
        if (i == 13) {
            // CR has been read - read LF too!
            i = inputStream.read();
        }
        return size;
    }

    /**
     * Checks if buffer is empty and fills it in if needed
     *
     * @throws IOException
     */
    protected void checkBuffer() throws IOException {
        if (chunkEncoding) {
            if (chunkSize >= 0) {
                if (ptr == len) {
                    ptr = 0;
                    len = 0;
                    mark = -1;
                    if ((buffer == null) || (bufferSize != buffer.length)) {
                        buffer = new byte[bufferSize];
                    }
                    int l = 0;
                    while ((l >= 0) && (len < buffer.length)) {
                        if (chunkSize == 0) {
                            chunkSize = readChunkSize();
                            if (chunkSize == 0) {
                                chunkSize = -1;
                                l = -1;
                            }
                        }
                        if (chunkSize > 0) {
                            l = chunkSize;
                            if (l > (buffer.length - len)) {
                                l = buffer.length - len;
                            }
                            l = inputStream.read(buffer, len, l);
                            if (l > 0) {
                                chunkSize = chunkSize - l;
                                len = len + l;
                            } else {
                                chunkSize = -1;
                            }
                        }
                    }
                }
            } else {
                if (ptr == len) {
                    ptr = 0;
                    len = 0;
                    mark = -1;
                    if ((buffer != null) && (bufferSize != buffer.length)) {
                        buffer = null;
                    }
                }
            }
        } else {
            if (contentLength > 0) {
                if (ptr == len) {
                    ptr = 0;
                    len = 0;
                    mark = -1;
                    if ((buffer == null) || (bufferSize != buffer.length)) {
                        buffer = new byte[bufferSize];
                    }
                    int l = buffer.length;
                    if (l > contentLength) {
                        l = (int)contentLength;
                    }
                    l = inputStream.read(buffer, 0, l);
                    len = len + l;
                    contentLength = contentLength - l;
                }
            } else {
                if (ptr == len) {
                    ptr = 0;
                    len = 0;
                    mark = -1;
                    if ((buffer != null) && (bufferSize != buffer.length)) {
                        buffer = null;
                    }
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            if (chunkEncoding) {
                if (bufferSize <= 0) {
                    byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
                    while (chunkSize >= 0) {
                        if (chunkSize == 0) {
                            chunkSize = readChunkSize();
                            if (chunkSize == 0) {
                                chunkSize = -1;
                            }
                        }
                        if (chunkSize > 0) {
                            int r = DEFAULT_BUFFER_SIZE;
                            if (r > chunkSize) {
                                r = chunkSize;
                            }
                            r = inputStream.read(buf, 0, r);
                            chunkSize = chunkSize - r;
                        }
                    }
                } else {
                    while (chunkSize >= 0) {
                        ptr = len;
                        checkBuffer();
                    }
                }
            } else {
                if (bufferSize <= 0) {
                    if (contentLength > 0) {
                        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
                        while (contentLength > 0) {
                            int r = DEFAULT_BUFFER_SIZE;
                            if (r > contentLength) {
                                r = (int)contentLength;
                            }
                            r = inputStream.read(buf, 0, r);
                            contentLength = contentLength - r;
                        }
                    }
                } else {
                    while (contentLength > 0) {
                        contentLength = contentLength - (len - ptr);
                        checkBuffer();
                    }
                }
            }
        }
    }


    @Override
    public int read() throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        if (bufferSize <= 0) {
            if (chunkEncoding) {
                if (chunkSize == 0) {
                    chunkSize = readChunkSize();
                    if (chunkSize == 0) {
                        chunkSize = -1;
                    }
                }
                if (chunkSize > 0) {
                    int r = inputStream.read();
                    if (r >= 0) {
                        chunkSize = chunkSize - 1;
                    } else {
                        chunkSize = -1;
                    }
                    return r;
                } else {
                    chunkSize = -1;
                    return -1;
                }
            } else {
                if (contentLength > 0) {
                    int r = inputStream.read();
                    if (r >= 0) {
                        contentLength = contentLength - 1;
                    } else {
                        contentLength = 0;
                    }
                    return r;
                } else {
                    return -1;
                }
            }
        } else {
            checkBuffer();
            if (ptr < len) {
                int b = buffer[ptr];
                ptr = ptr + 1;
                return b;
            } else {
                return -1;
            }
        }
    }

    @Override
    public int read(byte[] buf) throws IOException {
        return read(buf, 0, buf.length);
    }

    @Override
    public int read(byte[] buf, int off, int l) throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        if (l == 0) {
            return 0;
        }
        if (bufferSize <= 0) {
            if (chunkEncoding) {
                int r = 0;
                while ((l > 0) && (chunkSize >= 0)) {
                    if (chunkSize == 0) {
                        chunkSize = readChunkSize();
                        if (chunkSize == 0) {
                            chunkSize = -1;
                        }
                    }
                    int ll = l;
                    if (ll > chunkSize) {
                        ll = chunkSize;
                    }
                    ll = inputStream.read(buf, off, ll);
                    if (ll >= 0) {
                        r = r + ll;
                        off = off + ll;
                        l = l - ll;
                        chunkSize = chunkSize - ll;
                    } else {
                        if (r > 0) {
                            return r;
                        } else {
                            return -1;
                        }
                    }
                }
                return r;
            } else {
                if (contentLength > 0) {
                    if (l > contentLength) {
                        l = (int)contentLength;
                    }
                    l = inputStream.read(buf, off, l);
                    if (l >= 0) {
                        contentLength = contentLength - l;
                    }
                    return l;
                } else {
                    return -1;
                }
            }
        } else {
            checkBuffer();
            int r = 0;
            int ll = len - ptr; // remainder in the buffer
            while ((ll > 0) && (ll <= l)) {
                System.arraycopy(buffer, ptr, buf, off, ll);
                l = l - ll; // that less we need to add
                r = r + ll; // that much we have already read
                off = off + ll; // we need to move in the result buffer
                ptr = len; // internal buffer is empty now
                checkBuffer(); // fill in some more
                ll = len - ptr; // that much we have in the buffer
            }
            if (ll > 0) {
                // internal buffer is not empty and has more then we need
                if (((ptr + l) > buffer.length)
                        || ((off + l) > buf.length)) {
                    System.out.println("here");
                }
                System.arraycopy(buffer, ptr, buf, off, l);
                ptr = ptr + l;
                r = r + l;
            }

            if (r == 0) {
                // We haven't read anything - so return -1 - as end of the stream!
                return -1;
            } else {
                return r;
            }
        }
    }

    @Override
    public long skip(long l) throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        if (l == 0) {
            return 0;
        }
        checkBuffer();
        long r = 0;
        int ll = len - ptr; // remainder in the buffer
        while ((ll > 0) && (ll <= l)) {
            l = l - ll; // that less we need to add
            r = r + ll; // that much we have already read
            ptr = len; // internal buffer is empty now
            checkBuffer(); // fill in some more
            ll = len - ptr; // that much we have in the buffer
        }
        if (ll > 0) {
            // internal buffer is not empty and has more then we need
            ptr = ptr + (int)l;
            r = r + l;
        }

        return r;
    }

    @Override
    public int available() throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        return ptr - len;
    }

    @Override
    public synchronized void mark(int i) {
        if (!closed) {
            if (chunkEncoding) {
                try {
                    checkBuffer();
                } catch (IOException e) {
                    return;
                }
                if ((chunkSize < 0) && (i > (ptr - len))) {
                    i = ptr - len;
                    // trim it down since we now there isn't that much
                    // bytes in the buffer since we have already read last chunk
                }
            } else {
                if (i > contentLength) {
                    i = (int)contentLength;
                }

            }
            if (i > bufferSize) {
                // extend the buffer for a required number of bytes
                byte[] newBuffer = new byte[i];
                System.arraycopy(buffer, ptr, newBuffer, 0, (ptr - len));
                buffer = newBuffer;
                len = ptr - len;
                ptr = 0;
                mark = 0;
            } else {
                mark = ptr;
            }
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        if (closed) {
            throw new IOException("Stream is closed");
        }
        if (mark < 0) {
            throw new IOException("Cannot reset - stream is part asked number of bytes after the mark.");
        }
        ptr = mark;
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}
