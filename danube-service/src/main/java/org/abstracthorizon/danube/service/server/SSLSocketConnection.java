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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.cert.Certificate;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.abstracthorizon.danube.adapter.AdapterFactory;
import org.abstracthorizon.danube.adapter.AdapterManager;

/**
 * This is socket connection implementation.
 *
 * @see org.abstracthorizon.danube.connection.socket.SocketConnection
 * @see org.abstracthorizon.danube.connection.Connection
 *
 * @author Daniel Sendula
 */
public class SSLSocketConnection extends SocketConnection {

    /** Classes that are available through this object as an {@link AdapterFactory} */
    protected static final Class<?>[] ADAPTING_CLASSES = new Class[]{
            SSLSocketConnection.class,
            SocketConnection.class,
            InputStream.class,
            OutputStream.class,
            Socket.class,
            SSLSocket.class,
            SSLSession.class,
            Certificate[].class};

    /** Socket */
    protected SSLSocket sslSocket;

    /**
     * Constructor. It creates buffered input and output streams.
     * @param socket socket
     */
    public SSLSocketConnection(Socket socket) {
        super(socket);
        this.sslSocket = (SSLSocket) socket;
    }

    /**
     * Constructor. It creates buffered input and output streams.
     * @param socket socket
     */
    public SSLSocketConnection(AdapterManager adapterManager, Socket socket) {
        super(adapterManager, socket);
        this.sslSocket = (SSLSocket) socket;
    }

    /**
     * Returns socket
     * @return socket
     */
    public SSLSocket getSSLSocket() {
        return sslSocket;
    }

    /**
     * Returns connection as a string
     * @return connection as a string
     */
    public String toString() {
        return "SSLSocketConnection[" + socket + "]";
    }

    @SuppressWarnings("unchecked")
    public <T> T adapt(Class<T> cls) {
        if (cls == SSLSocketConnection.class) {
            return (T)this;
        } else if (cls == SSLSocket.class) {
            return (T)this.sslSocket;
        } else if (cls == SSLSession.class) {
            return (T)this.sslSocket.getSession();
        } else if (cls == Certificate[].class) {
            try {
                return (T)this.sslSocket.getSession().getPeerCertificates();
            } catch (SSLPeerUnverifiedException e) {
                throw new RuntimeException(e);
            }
        }
        return super.adapt(cls);
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
