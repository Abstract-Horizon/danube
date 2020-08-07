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
package org.abstracthorizon.danube.http.test;

import org.abstracthorizon.danube.connection.ConnectionHandler;
import org.abstracthorizon.danube.http.HTTPServerConnectionHandler;
import org.abstracthorizon.danube.http.Selector;
import org.abstracthorizon.danube.http.matcher.Prefix;
import org.abstracthorizon.danube.http.util.ReadOnlyFileConnectionHandler;
import org.abstracthorizon.danube.http.util.ReadWriteFileConnectionHandler;
import org.abstracthorizon.danube.service.server.MultiThreadServerService;
import org.abstracthorizon.danube.test.util.HTTPServiceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class SetupTestServer {

    protected MultiThreadServerService service;

    protected HTTPServerConnectionHandler serverConnectionHandler;

    protected Selector serverSelector;

    protected File path;

    public SetupTestServer() {
    }

    public void setup() throws Exception {
        service = createService();

        //  service = ServiceUtils.newMultiThreadSocketChannelService(3);
        serverConnectionHandler = new HTTPServerConnectionHandler();
        serverSelector = new Selector();
        serverConnectionHandler.setConnectionHandler(serverSelector);
        service.setConnectionHandler(serverConnectionHandler);

        path = getTemporaryPath();
        createTestFiles(path);

        ConnectionHandler connectionHandler = createFileHandler(path);

        serverSelector.getComponents().clear();
        serverSelector.getComponents().add(
                new Prefix(connectionHandler, "/server")
        );

        service.create();
        service.start();

        System.out.println("Service started on the port: " + service.getPort());
        System.out.println("Temporary server file path: " + path.getAbsolutePath());
    }

    protected MultiThreadServerService createService() {
        return HTTPServiceUtils.newMultiThreadSocketService(10);
    }

    protected ConnectionHandler createFileHandler(File path) {
        ReadOnlyFileConnectionHandler fileConnectionHandler = new ReadWriteFileConnectionHandler();
        // ReadOnlyFileConnectionHandler fileConnectionHandler = new ReadOnlyFileConnectionHandler();
        fileConnectionHandler.setFilePath(path);
        return fileConnectionHandler;
    }

    public void createTestFiles(File path) throws IOException {
        File first = new File(path, "first.bin");
        createFile(first, 1024); // 1 Kb

        File second = new File(path, "second.bin");
        createFile(second, 15); // 10 Kb

        File third = new File(path, "third.bin");
        createFile(third, 128); // 1 Mb
    }

    public static void createFile(File file, int size) throws IOException {
        int bufSize = 1024;
        if (size > 1024 * 1024) {
            bufSize = 1024 * 1024;
        }
        byte[] buffer = new byte[bufSize];
        Random random = new Random();
        for (int i = 0; i < bufSize; i++) {
            buffer[i] = (byte)(random.nextInt(26) + 'A');
        }
        FileOutputStream out = new FileOutputStream(file);
        try {
            int written = size;
            while (written > 0) {
                if (written < bufSize) {
                    out.write(buffer, 0, written);
                    written = 0;
                } else {
                    out.write(buffer);
                    written = written - bufSize;
                }
            }
        } finally {
            out.close();
        }
        System.out.println("Created file " + file.getAbsolutePath() + " of size " + size);
    }

    public static File getTemporaryPath() throws IOException {
        File f = File.createTempFile("test", "");
        f.delete();
        f.mkdir();
        return f;
    }

    public static void main(String[] args) throws Exception {

        SetupTestServer test = new SetupTestServer();

        test.setup();
    }

}
