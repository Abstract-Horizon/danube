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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;

import junit.framework.Assert;

import org.abstracthorizon.danube.http.HTTPServerBaseTestClass;
import org.abstracthorizon.danube.http.matcher.Prefix;
import org.abstracthorizon.danube.support.logging.LoggingConnectionHandler;

public class FileConnectionHandlerTest extends HTTPServerBaseTestClass {

    public static final String FILENAME = "file.txt";

    protected File path;

    protected ReadWriteFileConnectionHandler fileHandler;

    protected URI uploadURI;

    public void setUp() throws Exception {
        path = UTestUtils.getTempDirectory();
        super.setUp();

        fileHandler = new ReadWriteFileConnectionHandler();
        fileHandler.setFilePath(path);

        selector.getComponents().add(new Prefix(fileHandler, "/test"));

        uploadURI = UTestUtils.addPath(serviceURL.toURI(), "/test/");
    }

    public void testFileUpload() throws Exception {
        server.setDefaultBufferSize(100);

        URI fileURI = UTestUtils.addPath(uploadURI, FILENAME);
        byte[] buf = new byte[2049];
        int b = 'A';
        for (int i = 0 ; i < buf.length; i++) {
            buf[i] = (byte)b;
            b = b + 1;
            if (b == 'Z' + 1) {
                b = 'A';
            }
        }
        HttpURLConnection httpURLConnection = (HttpURLConnection)fileURI.toURL().openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("PUT");
        httpURLConnection.getOutputStream().write(buf);
        int responseCode = httpURLConnection.getResponseCode();
        Assert.assertEquals(201, responseCode);

        Thread.sleep(1000);
        byte[] fileBuf = new byte[buf.length];
        File file = new File(path, FILENAME);
        FileInputStream fis = new FileInputStream(file);
        try {
            fis.read(fileBuf);
        } finally {
            fis.close();
        }

        String res = UTestUtils.compareInAndOut(buf, fileBuf);
        if (res != null) {
            Assert.assertTrue(res, false);
        }
    }

    public void tearDown() throws Exception {
        removeDirectory(path);
        super.tearDown();
    }

    /**
     * Manual testing
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        FileConnectionHandlerTest testcase = new FileConnectionHandlerTest() {
            protected void createServer() throws MalformedURLException {
                super.createServer();
                service.setPort(8088);
            }

        };
        testcase.setUp();

        LoggingConnectionHandler logging = new LoggingConnectionHandler();
//        Prefix test = (Prefix)testcase.server.getComponents().get(0);
//        logging.setConnectionHandler(test.getConnectionHandler());
//        test.setConnectionHandler(logging);
        logging.setConnectionHandler(testcase.service.getConnectionHandler());
        testcase.service.setConnectionHandler(logging);

        logging.setDirectional(false);
        logging.setLogsPath(new File(System.getProperty("java.io.tmpdir")));


        System.out.println("Service set on port: " + testcase.service.getPort());

        writeFile(new File(testcase.path, "file1.txt"), "Test file\none\n");
        writeFile(new File(testcase.path, "file2.txt"), "Test file\ntwo -- 2\n");
        File dir = new File(testcase.path, "dir");
        dir.mkdir();
        writeFile(new File(testcase.path, "file2.txt"), "Third test file\n3 -- three\n");
//        while (true) {
//            Thread.sleep(10);
//        }
    }

    protected static void writeFile(File file, String content) throws IOException {
        PrintWriter out = new PrintWriter(file);
        try {
            out.print(content.getBytes());
        } finally {
            out.close();
        }
    }

}
