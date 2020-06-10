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

import org.abstracthorizon.danube.http.util.UTestUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import junit.framework.Assert;
import junit.framework.TestCase;

public class HTTPBufferedInputStreamTest extends TestCase {

    protected byte[] inBuffer = new byte[10240];
    protected byte[] outBuffer = new byte[10240];
    
    public void setUp() {
        int b = 0;
        for (int i = 0; i < inBuffer.length; i++) {
            inBuffer[i] = (byte)b;
            b = b + 1;
            if (b == 256) {
                b = 0;
            }
        }
    }

    protected void clearOutput() {
        for (int i = 0; i < outBuffer.length; i ++) {
            outBuffer[i] = 0;
        }
    }
    
    public void testNonBufferedNonChunkedRead() throws IOException {
        for (int s = 100; s < 400; s = s + 102) {
            ByteArrayInputStream in = new ByteArrayInputStream(inBuffer);
            HTTPBufferedInputStream httpIn = new HTTPBufferedInputStream(in, 0);
            httpIn.setContentLength(inBuffer.length);
            oneIteration(httpIn, s);
        }
    }
    
    public void testBufferedNonChunkedRead() throws IOException {
        for (int s = 100; s < 400; s = s + 102) {
            ByteArrayInputStream in = new ByteArrayInputStream(inBuffer);
            HTTPBufferedInputStream httpIn = new HTTPBufferedInputStream(in, 0);
            httpIn.setContentLength(inBuffer.length);
            httpIn.setBufferSize(321);
            oneIteration(httpIn, s);
        }
    }

    public void testNonBufferedChunkedRead() throws IOException {
        for (int s = 100; s < 400; s = s + 102) {
            byte[] chunked = makeChunked(inBuffer, s);
            ByteArrayInputStream in = new ByteArrayInputStream(chunked);
            HTTPBufferedInputStream httpIn = new HTTPBufferedInputStream(in, 0);
            httpIn.setChunkEncoding(true);
            oneIteration(httpIn, s);
        }
    }
    
    public void testBufferedChunkedRead() throws IOException {
        for (int s = 100; s < 400; s = s + 102) {
            byte[] chunked = makeChunked(inBuffer, s);
            ByteArrayInputStream in = new ByteArrayInputStream(chunked);
            HTTPBufferedInputStream httpIn = new HTTPBufferedInputStream(in, 0);
            httpIn.setChunkEncoding(true);
            httpIn.setBufferSize(321);
            oneIteration(httpIn, s);
        }
    }

    protected void oneIteration(HTTPBufferedInputStream httpIn, int s) throws IOException {
        clearOutput();
        for (int i = 0; i < outBuffer.length; i = i + s) {
            int l = s;
            if (i + l > outBuffer.length) {
                l = outBuffer.length - i;
            }
            httpIn.read(outBuffer, i, l);
        }
        String res = UTestUtils.compareInAndOut(inBuffer, outBuffer);
        if (res != null) {
            Assert.assertTrue(res, false);
        }
    }
    
    protected byte[] makeChunked(byte[] in, int seed) throws IOException {
        ByteArrayOutputStream res = new ByteArrayOutputStream();
        Random r = new Random(seed);
        int l = in.length;
        int o = 0;
        while (l > 0) {
            int cs = r.nextInt(1000);
            if (cs > l) {
                cs = l;
            }
            String chunk = Integer.toString(cs, 16);
            res.write(chunk.getBytes());
            res.write(13);
            res.write(10);
            res.write(in, o, cs);
            l = l - cs;
            o = o + cs;
        }
        res.write(0);
        res.write(13);
        res.write(10);
        
        return res.toByteArray();
    }
}
