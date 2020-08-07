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
package org.abstracthorizon.danube.http;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.matcher.Prefix;
import org.abstracthorizon.danube.service.server.MultiThreadServerService;
import org.abstracthorizon.danube.test.util.HTTPServiceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Http connection test
 *
 * @author Daniel Sendula
 */
public class HTTPConnectionTestX {

    public static final String PATH = "path";

    public static final String COMPONENT = "component";

    public static final String SIMPLE_RESPONSE = "<html>\n<body>Hello</body>\n</html>\n";

    private static MultiThreadServerService service;
    private static HTTPServerConnectionHandler serverConnectionHandler;
    private static Selector serverSelector;

    @BeforeClass public static void setupService() throws IOException {
        service = HTTPServiceUtils.newMultiThreadSocketService(3);
        serverConnectionHandler = new HTTPServerConnectionHandler();
        serverSelector = new Selector();
        serverConnectionHandler.setConnectionHandler(serverSelector);
        service.setConnectionHandler(serverConnectionHandler);

        service.create();
        service.start();
    }

    @Test public void testSimpleRequest() throws Exception {
        ConnectionHandler testConnectionHandler = new ConnectionHandler() {
            public void handleConnection(Connection connection) {
                PrintWriter out = (PrintWriter)connection.adapt(PrintWriter.class);
                out.println(SIMPLE_RESPONSE);
            }
        };

        serverSelector.getComponents().clear();
        serverSelector.getComponents().add(
            new Prefix(testConnectionHandler, "/")
        );

        URL url = new URL("http://localhost:" + service.getPort() + "/");
        URLConnection urlConnection = url.openConnection();
        InputStream is = urlConnection.getInputStream();
        byte[] buf = new byte[SIMPLE_RESPONSE.length()];
        int read = is.read(buf);
        Assert.assertEquals(SIMPLE_RESPONSE, new String(buf, 0, read));
    }

    @Test public void testComponentPath() throws Exception {

        ConnectionHandler testConnectionHandler = new ConnectionHandler() {
            public void handleConnection(Connection connection) {
                HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
                Assert.assertEquals("/" + PATH, httpConnection.getComponentPath());
                Assert.assertEquals("/" + COMPONENT, httpConnection.getComponentResourcePath());
                PrintWriter out = (PrintWriter)connection.adapt(PrintWriter.class);
                out.println(SIMPLE_RESPONSE);
            }
        };

        serverSelector.getComponents().clear();
        serverSelector.getComponents().add(
            new Prefix(testConnectionHandler, "/" + PATH)
        );

        URL url = new URL("http://localhost:" + service.getPort() + "/" + PATH + "/" + COMPONENT);
        URLConnection urlConnection = url.openConnection();
        checkSimpleResponse(urlConnection);
    }

    @Test public void testGetParametersRequest() throws Exception {

    	serverSelector.getComponents().clear();
    	serverSelector.getComponents().add(
            new Prefix(new ParamTestConnectionHandler(), "/" + PATH)
        );

        URL url = new URL("http://localhost:" + service.getPort() + "/" + PATH + "/" + COMPONENT + "?param1=value1&param2=\"value2\"");
        URLConnection urlConnection = url.openConnection();
        checkSimpleResponse(urlConnection);
    }

    @Test public void testPostParametersRequest() throws Exception {
    	serverSelector.getComponents().clear();
    	serverSelector.getComponents().add(
            new Prefix(new ParamTestConnectionHandler(), "/" + PATH)
        );

        URL url = new URL("http://localhost:" + service.getPort() + "/" + PATH + "/" + COMPONENT + "?param1=value1&param2=\"value2\"");
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("POST");
        OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
        out.write("param1=value1\r\nparam2=\"value2\"\r\n");
        out.flush();
        out.close();
        checkSimpleResponse(urlConnection);
    }

    /**
     * Checks simple respose
     * @param urlConnection url connection
     * @throws Exception exception
     */
    protected void checkSimpleResponse(URLConnection urlConnection) throws Exception {
        InputStream is = urlConnection.getInputStream();
        byte[] buf = new byte[SIMPLE_RESPONSE.length()];
        int read = is.read(buf);
        Assert.assertEquals(SIMPLE_RESPONSE, new String(buf, 0, read));
    }

    @AfterClass public static void cleanUp() throws Exception {
        service.stop();
        service.destroy();
    }


    public static class ParamTestConnectionHandler implements ConnectionHandler {
        public void handleConnection(Connection connection) {
            HTTPConnection httpConnection = (HTTPConnection)connection.adapt(HTTPConnection.class);
            Assert.assertEquals("/" + PATH, httpConnection.getComponentPath());
            Assert.assertEquals("/" + COMPONENT, httpConnection.getComponentResourcePath());

            Assert.assertEquals("value1", httpConnection.getRequestParameters().getOnly("param1"));
            Assert.assertEquals("\"value2\"", httpConnection.getRequestParameters().getOnly("param2"));

            PrintWriter out = (PrintWriter)connection.adapt(PrintWriter.class);
            out.println(SIMPLE_RESPONSE);
        }
    }

    public static void main(String[] args) throws Exception {
        HTTPConnectionTestX test1 =  new HTTPConnectionTestX();
        // ServerSocketServiceTest test1 =  new ServerSocketChannelServiceTest();

        service = HTTPServiceUtils.newMultiThreadSocketService(3);
        //  service = ServiceUtils.newMultiThreadSocketChannelService(3);
        serverConnectionHandler = new HTTPServerConnectionHandler();
        service.setConnectionHandler(serverConnectionHandler);

        service.create();
        service.start();

        serverSelector.getComponents().clear();
        serverSelector.getComponents().add(
            new Prefix(new ParamTestConnectionHandler(), "/" + PATH)
        );

        System.out.println("Service started on the port: " + service.getPort());
        int count = 0;
        try {
            long start = System.currentTimeMillis();
            for (count = 0; count < 100; count++) {
                //System.out.println("Run: " + count);
                test1.testGetParametersRequest();
                //System.out.println("Run: " + count + " done.");
            }
            System.out.println("Lasted = " + (System.currentTimeMillis() - start));
        } finally {
            cleanUp();
            System.out.println("Counts: " + count);
        }
    }

}
