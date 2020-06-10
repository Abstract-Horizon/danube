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
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * 
 * 
 * @author Daniel Sendula
 */
public class ServerReverseSocketProxy implements Runnable {

    public static final boolean DO_DEBUG = false;

    private SocketAddress remoteInServerSocketAddress;
    private SocketAddress localServerSocketAddress;
    private ServerSocketChannel serverSocketChannel;
    private ServerSocketChannel remoteInServerSocketChannel;
    private SelectionKey commandChannelKey;
    private Queue<Character> sendQueue;
    private ByteBuffer sendBuffer = ByteBuffer.allocate(1);

    private boolean running = true;

    public ServerReverseSocketProxy(SocketAddress localServerSocketAddress, SocketAddress remoteInServerSocketAddress) {
        this.localServerSocketAddress = localServerSocketAddress;
        this.remoteInServerSocketAddress = remoteInServerSocketAddress;
    }

    public void init() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(localServerSocketAddress);
        serverSocketChannel.configureBlocking(false);

        remoteInServerSocketChannel = ServerSocketChannel.open();
        remoteInServerSocketChannel.socket().bind(remoteInServerSocketAddress);
        remoteInServerSocketChannel.configureBlocking(false);
        sendQueue = new LinkedList<Character>();
    }

    public void run() {
        try {
            Queue<SelectionKey> queue = new LinkedList<SelectionKey>();

            System.out.println("Started Server Reverse Socket Proxy");
            Selector selector = Selector.open();
            SelectionKey serverKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            SelectionKey remoteInServerKey = remoteInServerSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

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
                        SelectionKey localKey = localSocketChannel.register(selector, 0, "Newly received local request");

                        queue.add(localKey);

                        sendRequestForConnection();
                        System.out.println("Received request for remote connection.");
                    } else if (key == remoteInServerKey) {

                        if (queue.size() == 0) {
                            System.out.println("Remote Proxy connected.");
                            if (commandChannelKey == null) {
                                SocketChannel remoteSocketChannel = remoteInServerSocketChannel.accept();
                                remoteSocketChannel.configureBlocking(false);
                                commandChannelKey = remoteSocketChannel.register(selector, SelectionKey.OP_READ);
                            } else {
                                System.out.println("ERROR: Remote proxy connected one more time without reason. Rejecting!");
                                SocketChannel remoteSocketChannel = remoteInServerSocketChannel.accept();
                                remoteSocketChannel.close();
                            }
                        } else {
                            System.out.println("Received response from remote proxy.");
                            SelectionKey localKey = queue.poll();
                            SocketChannel remoteSocketChannel = remoteInServerSocketChannel.accept();
                            remoteSocketChannel.configureBlocking(false);
                            SelectionKey remoteKey = remoteSocketChannel.register(selector, SelectionKey.OP_READ);

                            /* SocketChannelsSession session = */new SocketChannelsSession(localKey, remoteKey);
                        }

                    } else if (key == commandChannelKey) {
                        if (key.isReadable()) {
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                        } else if (key.isWritable()) {
                            if (sendQueue.size() > 0) {
                                sendEntry();
                            } else {
                                key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                            }
                        }
                    } else {
                        Object object = key.attachment();
                        if (object instanceof SocketChannelsSession) {
                            SocketChannelsSession session = (SocketChannelsSession) object;
                            session.processKey(key);
                        } else {
                            System.out.println("Cannot process " + object + " in key " + key.interestOps() + " " + key);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendRequestForConnection() throws IOException {
        sendQueue.add('C');
        sendEntry();
    }

    public void sendEntry() throws IOException {
        Character c = sendQueue.poll();
        sendBuffer.clear();
        sendBuffer.put((byte) c.charValue());
        sendBuffer.flip();
        ((SocketChannel) commandChannelKey.channel()).write(sendBuffer);
        commandChannelKey.interestOps(commandChannelKey.interestOps() | SelectionKey.OP_WRITE);
    }

    public static void main(String[] args) throws Exception {
        ServerReverseSocketProxy socketProxy = new ServerReverseSocketProxy(new InetSocketAddress(8841), new InetSocketAddress(8842));
        socketProxy.init();
        socketProxy.run();
    }
}
