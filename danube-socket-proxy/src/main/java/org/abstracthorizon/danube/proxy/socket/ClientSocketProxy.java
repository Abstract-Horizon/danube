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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 
 * 
 * @author Daniel Sendula
 */
public class ClientSocketProxy implements Runnable {

    private SocketAddress remoteServerSocketAddress;
    private SocketAddress inServerSocketAddress;
    private SocketChannel inSocketChannel;
    private ByteBuffer inBuffer = ByteBuffer.allocate(1);

    private boolean running = true;

    public ClientSocketProxy(SocketAddress inServerSocketAddress, SocketAddress remoteServerSocketAddress) {
        this.inServerSocketAddress = inServerSocketAddress;
        this.remoteServerSocketAddress = remoteServerSocketAddress;
    }

    public void init() throws IOException {
        // Open command socket...

        inSocketChannel = SocketChannel.open();
        inSocketChannel.socket().connect(inServerSocketAddress);
        inSocketChannel.configureBlocking(false);
    }

    public void run() {
        try {
            System.out.println("Started Client Reverse Socket Proxy");
            Selector selector = Selector.open();
            SelectionKey inServerKey = inSocketChannel.register(selector, SelectionKey.OP_READ);

            while (running) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key == inServerKey) {

                        inBuffer.clear();
                        int read = ((SocketChannel) key.channel()).read(inBuffer);
                        inBuffer.flip();
                        inBuffer.mark();
                        byte[] buf = new byte[read];
                        inBuffer.get(buf);
                        System.out.println("Got request for call back " + new String(buf));
                        inBuffer.reset();

                        SocketChannel localSocketChannel = SocketChannel.open();
                        localSocketChannel.socket().connect(inServerSocketAddress);
                        localSocketChannel.configureBlocking(false);
                        SelectionKey localKey = localSocketChannel.register(selector, SelectionKey.OP_READ);

                        SocketChannel remoteSocketChannel = SocketChannel.open();
                        remoteSocketChannel.socket().connect(remoteServerSocketAddress);
                        remoteSocketChannel.configureBlocking(false);
                        SelectionKey remoteKey = remoteSocketChannel.register(selector, SelectionKey.OP_READ);

                        /* SocketChannelsSession session = */new SocketChannelsSession(localKey, remoteKey);

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
        ClientSocketProxy socketProxy = new ClientSocketProxy(new InetSocketAddress("10.15.19.146", 8842), new InetSocketAddress("127.0.0.1", 8041));
        socketProxy.init();
        socketProxy.run();
    }
}
