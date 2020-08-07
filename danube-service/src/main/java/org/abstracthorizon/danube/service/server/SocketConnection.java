/*
 * Copyright (c) 2005-2020 Creative Sphere Limited.
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
import org.abstracthorizon.danube.service.ServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * This is socket connection implementation.
 *
 * @see org.abstracthorizon.danube.connection.socket.SocketConnection
 * @see org.abstracthorizon.danube.connection.Connection
 *
 * @author Daniel Sendula
 */
public class SocketConnection implements Connection, AdapterFactory {

    /** Classes that are available through this object as an {@link AdapterFactory} */
    protected static final Class<?>[] ADAPTING_CLASSES = new Class[]{
            SocketConnection.class,
            InputStream.class,
            OutputStream.class,
            Socket.class};

    /** Socket */
    protected Socket socket;

    /** Cached input stream */
    protected InputStream cachedInputStream;

    /** Cached output stream */
    protected OutputStream cachedOutputStream;

    /**
     * Constructor. It creates buffered input and output streams.
     * @param socket socket
     */
    public SocketConnection(Socket socket) {
        this.socket = socket;
    }

    /**
     * Constructor. It creates buffered input and output streams.
     * @param socket socket
     */
    public SocketConnection(AdapterManager adapterManager, Socket socket) {
        this.socket = socket;
    }

    /**
     * Returns socket
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Returns input stream
     * @return input stream
     */
    public InputStream getInputStream() {
        if (cachedInputStream == null) {
            try {
                cachedInputStream = socket.getInputStream();
            } catch (IOException e) {
                throw new ServiceException(e);
            }
        }
        return cachedInputStream;
    }

    /**
     * Returns output stream
     * @return output stream
     */
    public OutputStream getOutputStream() {
        if (cachedOutputStream == null) {
            try {
                cachedOutputStream = socket.getOutputStream();
            } catch (IOException e) {
                throw new ServiceException(e);
            }
        }
        return cachedOutputStream;
    }

    /**
     * Closes the connection - closes the underlying socket.
     */
    public void close() {
        if (!socket.isClosed()) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.setSoTimeout(10);
                synchronized (socket) { // TODO
                    socket.notifyAll();
                }
            } catch (Exception ignore) {
            } finally {
                try {
                    socket.close();
                } catch (Exception ignore) {
                    ignore.printStackTrace();
                }
            }
        }
    }

    /**
     * Checks if socket is closed
     *
     * @return <code>true</code> when socket is closed
     */
    public boolean isClosed() {
        return socket.isClosed();
    }

    /**
     * Returns connection as a string
     * @return connection as a string
     */
    public String toString() {
        return "SocketConnection[" + socket + "]";
    }

    /**
     * Adopts given object to the instance of the asked class
     * @param object object to he adopted
     * @param cls asked class
     * @return adopted given object to the instance of the asked class
     */
    public <T> T adapt(T object, Class<T> cls) {
        if (object == this) {
            return adapt(cls);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T adapt(Class<T> cls) {
        if (cls == SocketConnection.class) {
            return (T)this;
        } else if (cls == Socket.class) {
            return (T)getSocket();
        } else if (cls == OutputStream.class) {
            return (T)getOutputStream();
        } else if (cls == InputStream.class) {
            return (T)getInputStream();
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
