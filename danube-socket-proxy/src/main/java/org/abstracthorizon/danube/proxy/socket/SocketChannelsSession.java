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
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 
 * 
 * @author Daniel Sendula
 */
public class SocketChannelsSession {

    public static final boolean DO_DEBUG = false;

    public SelectionKey localKey;
    public SelectionKey remoteKey;
    public Direction in;
    public Direction out;
    public String otherIP;

    public SocketChannelsSession() {
    }

    protected class Direction {

        protected ByteBuffer buffer;

        protected SelectionKey fromKey;
        protected SelectionKey toKey;
        protected DirectionStates state = DirectionStates.IDLE;

        protected String address;
        protected String direction;

        public Direction(SelectionKey fromKey, SelectionKey toKey, String address, String direction) {
            this.fromKey = fromKey;
            this.toKey = toKey;
            this.address = address;
            this.direction = direction;
            buffer = ByteBuffer.allocateDirect(2048);
        }

        protected void processRead() throws IOException {
            log("--- processing read event ---");
            if (state == DirectionStates.IDLE) {
                passData();
            } else if (state == DirectionStates.WRITTING) {
                fromKey.interestOps(toKey.interestOps() & ~SelectionKey.OP_READ);
                log("-> " + DirectionStates.WRITTING_READING_IS_WAITING);
                state = DirectionStates.WRITTING_READING_IS_WAITING;
            } else {
                fromKey.interestOps(toKey.interestOps() & ~SelectionKey.OP_READ);
            }
        }

        protected void processWrite() throws IOException {
            log("--- processing write event ---");
            if (state == DirectionStates.WRITTING) {
                toKey.interestOps(toKey.interestOps() & ~SelectionKey.OP_WRITE);
                log("-> " + DirectionStates.IDLE);
                state = DirectionStates.IDLE;
            } else if (state == DirectionStates.WRITTING_READING_IS_WAITING) {
                passData();
                fromKey.interestOps(toKey.interestOps() | SelectionKey.OP_READ);
            } else {
                toKey.interestOps(toKey.interestOps() & ~SelectionKey.OP_WRITE);
            }
        }

        protected void passData() throws IOException {
            buffer.clear();
            try {
                int bytes = ((SocketChannel) fromKey.channel()).read(buffer);
                if (bytes > 0) {
                    byte[] strbytes = new byte[bytes];
                    buffer.flip();
                    buffer.mark();
                    buffer.get(strbytes);
                    buffer.reset();
                    log(new String(strbytes));
                    ((SocketChannel) toKey.channel()).write(buffer);
                    toKey.interestOps(toKey.interestOps() | SelectionKey.OP_WRITE);
                    log("-> " + DirectionStates.WRITTING);
                    state = DirectionStates.WRITTING;
                } else if (bytes < 0) {
                    close();
                }
            } catch (IOException e) {
                close();
            }
        }

        protected void log(String message) {
            SocketChannelsSession.this.log(address, state.toString(), direction, message);
        }
    }

    protected SocketChannelsSession(SelectionKey localKey, SelectionKey remoteKey) {
        this.localKey = localKey;
        this.remoteKey = remoteKey;
        localKey.interestOps(localKey.interestOps() | SelectionKey.OP_READ);

        localKey.attach(this);
        remoteKey.attach(this);

        otherIP = ((SocketChannel) localKey.channel()).socket().getRemoteSocketAddress().toString();
        in = new Direction(localKey, remoteKey, otherIP, "sent to ");
        out = new Direction(remoteKey, localKey, otherIP, "received");
        log(otherIP, null, null, " connected!");
    }

    protected void processKey(SelectionKey key) {
        try {
            if (key.isValid()) {
                if (key.isReadable()) {
                    if (key == localKey) {
                        in.processRead();
                    } else {
                        out.processRead();
                    }
                }
                if (key.isWritable()) {
                    if (key == remoteKey) {
                        in.processWrite();
                    } else {
                        out.processWrite();
                    }
                }
            } else {
                close();
            }
        } catch (CancelledKeyException e) {
            close();
        } catch (IOException e) {
            close();
        }
    }

    protected void close() {
        try {
            log(otherIP, null, null, " closing...");
            localKey.channel().close();
            remoteKey.channel().close();
            localKey.cancel();
            remoteKey.cancel();
            log(otherIP, null, null, " closed.");
        } catch (CancelledKeyException ignore) {
            System.err.println(ignore.getMessage());
        } catch (IOException ignore) {
            System.err.println(ignore.getMessage());
        }
    }

    protected void log(String address, String state, String direction, String message) {
        if (DO_DEBUG) {
            if (state != null) {
                System.out.println(address + "[" + state + "] " + direction + " " + message);
            } else {
                System.out.println(address + " " + message);
            }
        }
    }

}