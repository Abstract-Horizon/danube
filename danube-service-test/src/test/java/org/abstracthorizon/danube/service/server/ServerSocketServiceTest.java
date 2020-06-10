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
package org.abstracthorizon.danube.service.server;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.support.RuntimeIOException;
import org.abstracthorizon.danube.test.util.HTTPServiceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import junit.framework.TestCase;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server socket service test case
 *
 * @author Daniel Sendula
 */
public class ServerSocketServiceTest extends TestCase {
    
    public static Logger logger = LoggerFactory.getLogger(ServerSocketServiceTest.class);

    public static final String REQUEST_STRING = "request";
    public static final String RESPONSE_STRING = "response";
    public static final String CRLF = "\r\n";
    public static final int TIMEOUT = 10000; // 10 secs
    
    
    protected ServerService service;
    protected boolean testHandlerInvoked = false;

    public void setUp() throws IOException {
        service = HTTPServiceUtils.newMultiThreadSocketService();
        service.create();
        service.start();
    }

    public void testServiceInvoked() throws UnknownHostException, IOException, InterruptedException {
        service.setConnectionHandler(new ConnectionHandler() {
            public void handleConnection(Connection connection) {
                try {
                    testHandlerInvoked = true;
                    synchronized (this) {
                        notifyAll();
                    }
                    InputStream is = (InputStream)connection.adapt(InputStream.class);
                    String requestString = readString(is, REQUEST_STRING.length() + CRLF.length());
                    
                    Assert.assertEquals(REQUEST_STRING + CRLF, requestString);
                    OutputStream os = (OutputStream)connection.adapt(OutputStream.class);
                    os.write(RESPONSE_STRING.getBytes());
                    os.write(CRLF.getBytes());
                    os.flush();
                } catch (IOException e) {
                    throw new RuntimeIOException(e);
                }
            }
        });

        Socket socket = new Socket("localhost", service.getPort());
        OutputStream os = socket.getOutputStream();
        os.write(REQUEST_STRING.getBytes());
        os.write(CRLF.getBytes());
        os.flush();

        InputStream is = socket.getInputStream();
        String responseString = readString(is, RESPONSE_STRING.length() + CRLF.length());
        
        Assert.assertEquals(RESPONSE_STRING + CRLF, responseString);
        socket.close();
        Assert.assertTrue(testHandlerInvoked);
    }

    public void tearDown() {
        service.stop();
        service.destroy();
        service = null;
    }

    public static void main(String[] args) throws Exception {
        // ServerSocketServiceTest test1 =  new ServerSocketServiceTest();
        ServerSocketServiceTest test1 =  new ServerSocketChannelServiceTest();
        int count = 0;
        test1.setUp();
        try {
            long start = System.currentTimeMillis();
            for (count = 0; count < 10; count++) {
                //System.out.println("Run: " + count);
                test1.testServiceInvoked();
                //System.out.println("Run: " + count + " done.");
            }
            System.out.println("Lasted = " + (System.currentTimeMillis() - start));
        } finally {
            test1.tearDown();
            System.out.println("Counts: " + count);
        }
    }
    
    public static String readString(InputStream is, int len) throws IOException {
        byte[] buf = new byte[len];
        int timeout = 0;
        int ptr = 0;
        int r = is.read(buf, ptr, len - ptr);
        while ((ptr < len) && (timeout < TIMEOUT)) {
            if (r > 0) {
                ptr = ptr + r;
            } else {
                try {
                    Thread.sleep(100);
                    timeout = timeout + 100;
                } catch (InterruptedException ignore) {
                }
            }
            if (ptr < len) {
                r = is.read(buf, ptr, len - ptr);
            }
        }
        if (ptr > 0) {
            return new String(buf, 0, ptr);
        } else {
            return null;
        }
        
    }

}
