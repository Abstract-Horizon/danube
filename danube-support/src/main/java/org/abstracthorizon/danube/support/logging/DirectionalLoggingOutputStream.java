/*
 * Copyright (c) 2004-2007 Creative Sphere Limited.
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
 * Output stream for logging showing directional marks &quot;&lt;&quot;.
 * Given output stream is duplicated to log stream.
 *
 * @author Daniel Sendula
 */
public class DirectionalLoggingOutputStream extends LoggingOutputStream {

    /** Internal flag to show when to output directional char */
    protected boolean flag = true;

    /**
     * Constructor
     * @param outputStream output stream
     * @param logOutputStream log output stream
     */
    public DirectionalLoggingOutputStream(OutputStream outputStream, OutputStream logOutputStream) {
        super(outputStream, logOutputStream);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        if (logging) { flag = DirectionalLoggingInputStream.output(flag, logOutputStream, '<', b, off, len); }
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        if (logging) { flag = DirectionalLoggingInputStream.output(flag, logOutputStream, '<', b);  }
    }

}
