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

import org.abstracthorizon.danube.service.server.MultiThreadServerSocketService;
import org.abstracthorizon.danube.test.util.HTTPServiceUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;

public class HTTPServerBaseTestClass extends TestCase {

    protected File path;

    protected MultiThreadServerSocketService service;

    protected HTTPServerConnectionHandler server;

    protected Selector selector;

    protected URL serviceURL;

    protected void removeDirectory(File path) throws IOException {
        File files[] = path.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        path.delete();
    }

    protected void createServer() throws MalformedURLException {
        service = HTTPServiceUtils.newMultiThreadSocketService();
        server = new HTTPServerConnectionHandler();
        selector = new Selector();
        server.setConnectionHandler(selector);
        service.setConnectionHandler(server);

        serviceURL = new URL("http://localhost:" + service.getPort() + "/");
    }

    public void setUp() throws Exception {
        createServer();

        service.create();
        service.start();
    }

    public void tearDown() throws Exception {
        service.stop();
        service.destroy();
    }



}
