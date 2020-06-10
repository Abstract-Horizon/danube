/*
 * Copyright (c) 2005-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.service.server;

import org.abstracthorizon.danube.adapter.AdapterFactory;
import org.abstracthorizon.danube.adapter.AdapterManager;
import org.abstracthorizon.danube.connection.Connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;

/**
 * This is socket connection implementation.
 *
 * @see org.abstracthorizon.danube.connection.socket.SocketConnection
 * @see org.abstracthorizon.danube.connection.Connection
 *
 * @author Daniel Sendula
 */
public class SocketChannelConnection implements Connection, AdapterFactory {

    /** Classes that are available through this object as an {@link AdapterFactory} */
    protected static final Class<?>[] ADAPTING_CLASSES =
        new Class[]{
        SocketChannelConnection.class,
        SocketChannel.class,
        Channel.class,
        InputStream.class,
        OutputStream.class,
        Socket.class};

    /** Socket */
    protected SocketChannel socketChannel;

    /** Cached input stream */
    protected InputStream cachedInputStream;

    /** Cached output stream */
    protected OutputStream cachedOutputStream;

    /**
     * Constructor. It creates buffered input and output streams.
     * @param socket
     */
    public SocketChannelConnection(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * Constructor. It creates buffered input and output streams.
     * @param socket
     */
    public SocketChannelConnection(AdapterManager adapterManager, SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    /**
     * Returns socket channel
     * @return socket channel
     */
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * Returns socket
     * @return socket
     */
    public Socket getSocket() {
        return socketChannel.socket();
    }

    /**
     * Returns input stream
     * @return input stream
     */
    public InputStream getInputStream() {
        if (cachedInputStream == null) {
            cachedInputStream = Channels.newInputStream(socketChannel);
        }
        return cachedInputStream;
    }

    /**
     * Returns output stream
     * @return output stream
     */
    public OutputStream getOutputStream() {
        if (cachedOutputStream == null) {
            cachedOutputStream = Channels.newOutputStream(socketChannel);
        }
        return cachedOutputStream;
    }

    /**
     * Closes the socket channel
     */
    public void close() {
        try {
            socketChannel.close();

            Socket socket = socketChannel.socket();
//            socket.shutdownInput();
//            socket.shutdownOutput();
//            socket.close();
            socket.setSoTimeout(10);
            synchronized (socket) { // TODO
                socket.notifyAll();
            }
        } catch (Exception ignore) {
            // TODO remove
            ignore.printStackTrace();
        }

    }

    /**
     * Checks if socket is closed
     * @return <code>true</code> if socket is closed
     */
    public boolean isClosed() {
        // TODO check if this is equivalent to socketChannel.socket().isClosed()
        return !socketChannel.isOpen();
    }


    /**
     * Returns connection as a string
     * @return connection as a string
     */
    public String toString() {
        return "SocketConnectionImpl[" + socketChannel + "]";
    }

    /**
     * Adopts given object to the instance of the asked class
     * @param object object to he adopted
     * @param cls asked class
     * @return adopted given object to the instance of the asked class
     */
    public <T> T adapt(T object, Class<T> cls) {
        if (object == this) {
            return (T)adapt(cls);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T adapt(Class<T> cls) {
        if (cls == SocketChannelConnection.class) {
            return (T)this;
        } else if (cls == Socket.class) {
            return (T)getSocket();
        } else if (cls == OutputStream.class) {
            return (T)getOutputStream();
        } else if (cls == InputStream.class) {
            return (T)getInputStream();
        } else if (cls == SocketChannel.class) {
            return (T)getSocketChannel();
        } else if (cls == Channel.class) {
            return (T)getSocketChannel();
        }
        return null;
    }

    /**
     * Returns list of classes to which given object can be adopted to by this adopter factory
     * @return list of classes to which given object can be adopted to
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T>[] getAdaptingClasses(T object) {
        return (Class<T>[])ADAPTING_CLASSES;
    }
}
