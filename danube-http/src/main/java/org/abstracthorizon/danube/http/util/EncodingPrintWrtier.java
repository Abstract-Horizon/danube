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
package org.abstracthorizon.danube.http.util;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.IllegalCharsetNameException;

public class EncodingPrintWrtier extends PrintWriter {

    protected BufferlessOutputStreamWriter cachedOut;

    public EncodingPrintWrtier(OutputStream outputStream, String encoding) throws IllegalCharsetNameException, UnsupportedEncodingException {
        super(new BufferlessOutputStreamWriter(outputStream, encoding));
        cachedOut = (BufferlessOutputStreamWriter)out;
    }

    public void close() {
        super.close();
    }

    public void resetInternals() {
        out = cachedOut;
    }

    public void setEncoding(String encoding) throws UnsupportedEncodingException, IllegalCharsetNameException {
        cachedOut.setEncoding(encoding);
    }

    public String getEcoding() {
        return cachedOut.getEncoding();
    }
}
