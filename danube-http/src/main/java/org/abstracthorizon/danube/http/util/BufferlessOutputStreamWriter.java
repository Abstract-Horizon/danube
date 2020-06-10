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
package org.abstracthorizon.danube.http.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

/**
 *
 *
 * @author Daniel Sendula
 */
public class BufferlessOutputStreamWriter extends Writer {

    protected OutputStream stream;

    protected Charset charSet;

    public BufferlessOutputStreamWriter(OutputStream stream) {
        this.stream = stream;
    }

    public BufferlessOutputStreamWriter(OutputStream stream, String encoding) throws UnsupportedEncodingException, IllegalCharsetNameException {
        this.stream = stream;
        setEncoding(encoding);
    }

    public void setEncoding(String encoding) throws UnsupportedEncodingException, IllegalCharsetNameException {
        // TODO wrap this method with proper exception
        if (encoding != null) {
            if ((charSet == null) || !encoding.equals(charSet.name())) {
                charSet = Charset.forName(encoding);
            }
        } else {
            encoding = null;
        }
    }

    public String getEncoding() {
        if (charSet != null) {
            return charSet.name();
        } else {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public void flush() throws IOException {
        stream.close();
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        String s = new String(cbuf, off, len);
        if (charSet == null) {
            stream.write(s.getBytes());
        } else {
            stream.write(charSet.encode(s).array());
        }
    }


}
