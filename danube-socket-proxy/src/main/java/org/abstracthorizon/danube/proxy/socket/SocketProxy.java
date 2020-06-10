/*
 * Copyright (c) 2010 Creative Sphere Limited.
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
package org.abstracthorizon.danube.proxy.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * 
 * @author Daniel Sendula
 */
public class SocketProxy implements Runnable {

    public static final boolean DO_DEBUG = false;

    private SocketAddress remoteServerSocketAddress;
    private SocketAddress localServerSocketAddress;
    private ServerSocketChannel serverSocketChannel;

    private boolean running = true;

    public SocketProxy(SocketAddress localServerSocketAddress, SocketAddress remoteServerSocketAddress) {
        this.localServerSocketAddress = localServerSocketAddress;
        this.remoteServerSocketAddress = remoteServerSocketAddress;
    }

    public void init() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(localServerSocketAddress);
        serverSocketChannel.configureBlocking(false);
    }

    public void run() {
        try {
            System.out.println("Started Socket Proxy");
            Selector selector = Selector.open();
            SelectionKey serverKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (running) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key == serverKey) {
                        SocketChannel localSocketChannel = serverSocketChannel.accept();
                        localSocketChannel.configureBlocking(false);
                        SelectionKey localKey = localSocketChannel.register(selector, SelectionKey.OP_READ);

                        SocketChannel remoteSocketChannel = SocketChannel.open();
                        remoteSocketChannel.socket().connect(remoteServerSocketAddress);
                        remoteSocketChannel.configureBlocking(false);
                        SelectionKey remoteKey = remoteSocketChannel.register(selector, SelectionKey.OP_READ);

                        SocketChannelsSession session = new SocketChannelsSession(localKey, remoteKey);

                        remoteKey.attach(session);
                        localKey.attach(session);

                    } else {
                        SocketChannelsSession session = (SocketChannelsSession) key.attachment();
                        session.processKey(key);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        SocketProxy socketProxy = new SocketProxy(new InetSocketAddress(8841), new InetSocketAddress("127.0.0.1", 8041));
        socketProxy.init();
        socketProxy.run();
    }
}
