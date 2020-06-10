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

import org.abstracthorizon.danube.support.RuntimeIOException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This is buffered http output stream. It knows how to emit chuncked encoding.
 *
 * @author Daniel Sendula
 */
public class HTTPBufferedOutputStream extends OutputStream {

    /** CR, LF */
    public static final String CRLF = "\r\n";

    /** Buffer */
    protected byte[] buffer;

    /** Pointer in the buffer */
    protected int ptr;

    /** Connection */
    protected HTTPConnectionImpl connection;

    /** Wrapped output stream */
    protected OutputStream outputStream;

    /** Should output be suppressed */
    protected boolean supporessOutput;

    /** Buffer size */
    protected int bufferSize;

    /** Is closed */
    protected boolean closed = false;

    /** Content length limit */
    protected long limitedContentLength = -1;

    /** Number of sent bytes */
    protected int sentbytes = 0;

    /** Chunk encoding */
    protected boolean chunkEncoding = false;

    /**
     * Constructor
     *
     * @param connection connection
     * @param outputStream wrapped output stream
     * @param defaultBufferSize default buffer size
     */
    public HTTPBufferedOutputStream(HTTPConnectionImpl connection, OutputStream outputStream, int defaultBufferSize) {
        this.connection = connection;
        this.outputStream = outputStream;
        this.bufferSize = defaultBufferSize;
    }

    /**
     * Resets internals
     */
    public void resetInternals() {
        closed = false;
        supporessOutput = false;
        ptr = 0;
        limitedContentLength = -1;
        sentbytes = 0;
        chunkEncoding = false;
    }

    /**
     * Sets if output should be suppressed or not
     *
     * @param suppressOutput should output be suppressed
     */
    public void setSupporessOutput(boolean suppressOutput) {
        this.supporessOutput = suppressOutput;
    }

    /**
     * Returns <code>true</code> if output should be suppressed
     * @return <code>true</code> if output should be suppressed
     */
    public boolean isSupporessOutput() {
        return supporessOutput;
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
        if (buffer != null) {
            if (size > buffer.length) {
                byte[] newBuffer = new byte[size];
                System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
                buffer = newBuffer;
            } else if (size < buffer.length) {
                try {
                    flush();
                } catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
                buffer = new byte[size];
            }
        }
    }

    /**
     * Returns limited content length
     * @return limited content length
     */
    public long getLimitedContentLength() {
        return limitedContentLength;
    }

    /**
     * Sets limited content length
     * @param limitedContentLength limited content length
     */
    public void setLimitedContentLength(long limitedContentLength) {
        this.limitedContentLength = limitedContentLength;
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
     * Checks if buffer is created
     *
     * @throws IOException
     */
    protected void checkBuffer() throws IOException {
        if (buffer == null) {
            buffer = new byte[bufferSize];
            ptr = 0;
        }
    }

    /**
     * Implements flushing of the buffer
     * @throws IOException
     */
    public void flushImpl() throws IOException {
        if (closed) {
            throw new IOException("Already closed");
        }
        if (ptr > 0) {
            if (!connection.isCommited()) {
                connection.commitHeaders();
            }

            if ((limitedContentLength >= 0) && ((sentbytes + ptr) > limitedContentLength)) {
                if (chunkEncoding) {
                    outputStream.write((Long.toString(limitedContentLength - sentbytes, 16) + CRLF).getBytes());
                    outputStream.write(buffer, 0, (int)(limitedContentLength - sentbytes));
                    outputStream.write(CRLF.getBytes());
                    // TODO is flush needed here?
                } else {
                    outputStream.write(buffer, 0, (int)(limitedContentLength - sentbytes));
                    // TODO or flush needed here?
                }
                throw new IOException("Content length exceeded asked value; requested to be sent " + (sentbytes + ptr) + ", limit " + limitedContentLength);
            } else {
                if (chunkEncoding) {
                    outputStream.write((Integer.toString(ptr, 16) + "\r\n").getBytes());
                    outputStream.write(buffer, 0, ptr);
                    outputStream.write(CRLF.getBytes());
                    // TODO is flush needed here?
                } else {
                    outputStream.write(buffer, 0, ptr);
                    // TODO or flush needed here?
                }
            }
            ptr = 0;
        }
    }

    @Override
    public void close() throws IOException {
        if (!connection.isCommited()) {
            // Convert Transfer-Encoding: chunked to Content-Length since nothing is commited yet!
            connection.getResponseHeaders().removeAll("Transfer-Encoding");
            chunkEncoding = false;
            if (!supporessOutput) {
                connection.getResponseHeaders().putOnly("Content-Length", Integer.toString(ptr));
            }
        }
        flushImpl();
        if (!connection.isCommited()) {
            connection.commitHeaders();
        }
        if (chunkEncoding) {
            outputStream.write("0\r\n\r\n".getBytes());
        }
        outputStream.flush();
        closed = true;
    }

    @Override
    public void write(int b) throws IOException {
        if (closed) {
            throw new IOException("Already closed");
        }
        if (!supporessOutput) {
            if ((limitedContentLength >= 0) && ((sentbytes + 1) > limitedContentLength)) {
                throw new IOException("Content length exceeded asked value; requested to be sent " + (sentbytes + 1) + ", limit " + limitedContentLength);
            }

            checkBuffer();
            buffer[ptr] = (byte)b;
            if (ptr == buffer.length) {
                flushImpl();
            }
        }
    }

    @Override
    public void write(byte[] buf, int start, int len) throws IOException {
        if (closed) {
            throw new IOException("Already closed");
        }
        if (!supporessOutput) {
            if ((limitedContentLength >= 0) && ((sentbytes + len) > limitedContentLength)) {
                throw new IOException("Content length exceeded asked value; requested to be sent " + (sentbytes + len) + ", limit " + limitedContentLength);
            }

            checkBuffer();
            if (len > (buffer.length - ptr)) {
                flushImpl();
                if (!connection.isCommited()) {
                    connection.commitHeaders();
                }
                if (chunkEncoding) {
                    outputStream.write((Integer.toString(len, 16) + "\r\n").getBytes());
                    outputStream.write(buf, start, len);
                    outputStream.write(CRLF.getBytes());
                    // TODO is flush needed here?
                } else {
                    outputStream.write(buf, start, len);
                }
            } else {
                System.arraycopy(buf, start, buffer, ptr, len);
                ptr = ptr + len;
                if (ptr == buffer.length) {
                    flushImpl();
                }
            }
        }
    }

    @Override
    public void write(byte[] buf) throws IOException {
        if (closed) {
            throw new IOException("Already closed");
        }
        if (!supporessOutput) {
            write(buf, 0, buf.length);
        }
    }
}
