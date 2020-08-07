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
package org.abstracthorizon.danube.test.util;

import org.abstracthorizon.danube.service.server.MultiThreadServerSocketChannelService;
import org.abstracthorizon.danube.service.server.MultiThreadServerSocketService;

import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HTTPServiceUtils {

    public static int getRandomPort() {
        try {
            Socket socket = new Socket();
            // socket.setReuseAddress(true);
            socket.bind(null);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    
    public static MultiThreadServerSocketService newMultiThreadSocketService(int fixedNumberOfThreads) {
        return newMultiThreadSocketService(Executors.newFixedThreadPool(fixedNumberOfThreads));
    }
    
    public static MultiThreadServerSocketService newMultiThreadSocketService(Executor executor) {
        MultiThreadServerSocketService service = new MultiThreadServerSocketService();
        service.setPort(getRandomPort());
        service.setExecutor(executor);
        return service;
    }
    
    public static MultiThreadServerSocketService newMultiThreadSocketService() {
        MultiThreadServerSocketService service = new MultiThreadServerSocketService();
        service.setPort(getRandomPort());
        return service;
    }
    
    public static MultiThreadServerSocketChannelService newMultiThreadSocketChannelService(int fixedNumberOfThreads) {
        return newMultiThreadSocketChannelService(Executors.newFixedThreadPool(fixedNumberOfThreads));
    }
    
    public static MultiThreadServerSocketChannelService newMultiThreadSocketChannelService(Executor executor) {
        MultiThreadServerSocketChannelService service = new MultiThreadServerSocketChannelService();
        service.setPort(getRandomPort());
        service.setExecutor(executor);
        return service;
    }
    
    public static MultiThreadServerSocketChannelService newMultiThreadSocketChannelService() {
        MultiThreadServerSocketChannelService service = new MultiThreadServerSocketChannelService();
        service.setPort(getRandomPort());
        return service;
    }
}
