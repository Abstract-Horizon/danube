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
/**
 *
 */
package org.abstracthorizon.danube.webdav;

import org.abstracthorizon.danube.http.HTTPServerConnectionHandler;
import org.abstracthorizon.danube.http.Selector;
import org.abstracthorizon.danube.http.matcher.Prefix;
import org.abstracthorizon.danube.http.util.IOUtils;
import org.abstracthorizon.danube.service.server.MultiThreadServerSocketService;
import org.abstracthorizon.danube.support.logging.debug.DebugConsoleLoggingConnectionHandler;
import org.abstracthorizon.danube.webdav.fs.FileSystemWebDAVResourceAdapter;
import org.abstracthorizon.danube.webdav.protocols.webdav.Handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;

import junit.framework.Assert;

/**
 * An environment for tests
 *
 * @author Daniel Sendula
 */
public class Environment {

    public MultiThreadServerSocketService service;
    public File dir;
    public HTTPServerConnectionHandler server;
    public Selector selector;
    public DebugConsoleLoggingConnectionHandler logger;
    public BaseWebDAVResourceConnectionHandler handler;
    public FileSystemWebDAVResourceAdapter fileSystemWebDAVAdapter;
    public int port;
    public String componentPath = "/test";
    public boolean debug = false;

    static {

        URLStreamHandlerFactory factory = new URLStreamHandlerFactory() {

            public URLStreamHandler createURLStreamHandler(String protocol) {
                if ("webdav".equals(protocol)) {
                    return new Handler();
                }
                return null;
            }

        };
        URL.setURLStreamHandlerFactory(factory);
    }

    public void setUp() throws IOException {
 //       System.setProperty("java.protocol.handler.pkgs", getClass().getPackage().getName() + ".protocols");

        service = new MultiThreadServerSocketService();
        if (port > 0) {
            service.setPort(port);
        }
        service.setName("WebDAVFileConnectionHandlerTest");
        service.create();
        service.start();

        port = service.getPort();

        dir = File.createTempFile("temp", "dir");
        dir.delete();
        dir.mkdir();

        if (debug) {
            logger = new DebugConsoleLoggingConnectionHandler();
            service.setConnectionHandler(logger);

            server = new HTTPServerConnectionHandler();
            logger.setConnectionHandler(server);
        } else {
            server = new HTTPServerConnectionHandler();
            service.setConnectionHandler(server);
        }
        selector = new Selector();
        server.setConnectionHandler(selector);

        handler = new BaseWebDAVResourceConnectionHandler();
        selector.getComponents().add(new Prefix(handler, componentPath));

        fileSystemWebDAVAdapter = new FileSystemWebDAVResourceAdapter();
        handler.setWebDAVResourceAdapter(fileSystemWebDAVAdapter);

        fileSystemWebDAVAdapter.setFilePath(dir);
    }

    public void tearDown() {
        service.stop();
        service.destroy();
    }


    public URL getURL() throws MalformedURLException {
        return new URL("webdav://localhost:" + port + componentPath);
    }

    public URL getURL(String path) throws MalformedURLException {
        if (!path.startsWith("/") && !componentPath.endsWith("/")) {
            path = "/" + path;
        } else if (path.startsWith("/") &&  componentPath.endsWith("/")) {
            if (path.length() > 1) {
                path = path.substring(1);
            } else {
                path = "";
            }
        }
        return new URL("webdav://localhost:" + port + componentPath + path);
    }

    public HttpURLConnection doMethod(String method, String resourceName, InputStream sourceStream, int expectedCode, InputStream compareWith, HashMap<String, String> headers) throws IOException {
        URL url = getURL(resourceName);
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setUseCaches(false);
        urlConnection.setDefaultUseCaches(false);
        urlConnection.setRequestMethod(method);
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(sourceStream != null);
        if (headers != null) {
            for (String name : headers.keySet()) {
                urlConnection.setRequestProperty(name, headers.get(name));
            }
        }
        if (sourceStream != null) {
            IOUtils.copyStreams(sourceStream, urlConnection.getOutputStream());
        }

        Assert.assertEquals(expectedCode, urlConnection.getResponseCode());
        if (compareWith != null) {
            InputStream inputStream = urlConnection.getInputStream();
            String res = IOUtils.compareStreams(inputStream, compareWith);
            if (res != null) {
                Assert.assertTrue(res, false);
            }
        }
        return urlConnection;
    }

    public HttpURLConnection doMethod(String method, String resourceName, int expectedCode, InputStream compareWith, HashMap<String, String> headers) throws IOException {
        return doMethod(method, resourceName, (InputStream)null, expectedCode, compareWith, headers);
    }

    public HttpURLConnection doMethod(String method, String resourceName, InputStream sourceStream, int expectedCode, File compareWith, HashMap<String, String> headers) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(compareWith);
        try {
            return doMethod(method, resourceName, sourceStream, expectedCode, fileInputStream, headers);
        } finally {
            fileInputStream.close();
        }
    }

    public HttpURLConnection doMethod(String method, String resourceName, File sourceFile, int expectedCode, InputStream compareWith, HashMap<String, String> headers) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        try {
            return doMethod(method, resourceName, fileInputStream, expectedCode, compareWith, headers);
        } finally {
            fileInputStream.close();
        }
    }

    public HttpURLConnection doMethod(String method, String resourceName, File sourceFile, int expectedCode, File compareWith, HashMap<String, String> headers) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        try {
            return doMethod(method, resourceName, fileInputStream, expectedCode, compareWith, headers);
        } finally {
            fileInputStream.close();
        }
    }

    public HttpURLConnection doMethod(String method, String resourceName, String source, int expectedCode, String compareWith, HashMap<String, String> headers) throws IOException {
        ByteArrayInputStream sourceStream = new ByteArrayInputStream(source.getBytes());
        ByteArrayInputStream compareStream = new ByteArrayInputStream(compareWith.getBytes());
        return doMethod(method, resourceName, sourceStream, expectedCode, compareStream, headers);
    }


    public void doMethod(String method, String resourceName, int expectedCode, File compareWith, HashMap<String, String> headers) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(compareWith);
        try {
            doMethod(method, resourceName, (InputStream)null, expectedCode, fileInputStream, headers);
        } finally {
            fileInputStream.close();
        }
    }

    public static void compareFiles(File file1, File file2) throws IOException {
        FileInputStream fileInputStream1 = new FileInputStream(file1);
        try {
            FileInputStream fileInputStream2 = new FileInputStream(file2);
            try {
                String res = IOUtils.compareStreams(fileInputStream1, fileInputStream2);
                if (res != null) {
                    Assert.assertTrue(res, false);
                }
            } finally {
                fileInputStream2.close();
            }
        } finally {
            fileInputStream1.close();
        }

    }

}
