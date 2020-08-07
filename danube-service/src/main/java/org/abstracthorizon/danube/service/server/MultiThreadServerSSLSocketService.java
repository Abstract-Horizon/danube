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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.abstracthorizon.danube.connection.Connection;
import org.abstracthorizon.danube.service.ServiceException;
import org.abstracthorizon.danube.service.util.SSLUtil;

public class MultiThreadServerSSLSocketService extends MultiThreadServerSocketService {

    /** Keystore password */
    protected String keystorePassword;

    /** Keystore file name */
    protected URL keystoreURL;

    /** Truststore password */
    private String truststorePassword;

    /** Truststore file name */
    private URL truststoreURL;

    /**
     * Default constructor
     */
    public MultiThreadServerSSLSocketService() {
    }

    /**
     * Creates server socket
     * @return server socket
     * @throws ServiceException
     */
    protected void createServerSocket() throws ServiceException {
        try {
            SSLServerSocketFactory factory;
            if (truststoreURL == null) {
                factory = SSLUtil.getServerSocketFactory(getKeyStorePassword().toCharArray(), getKeyStoreInputStream());
            } else {
                factory = SSLUtil.getServerSocketFactory(getKeyStorePassword().toCharArray(), getKeyStoreInputStream(), getTrustStorePassword().toCharArray(), getTrustStoreInputStream());
            }

            serverSocket = factory.createServerSocket(getPort(), 0, getSocketAddress().getAddress());
            serverSocket.setSoTimeout(getServerSocketTimeout());
            SSLServerSocket sslServerSocket = ((SSLServerSocket)serverSocket);
            sslServerSocket.setNeedClientAuth(truststoreURL != null);
        } catch (IOException e) {
            throw new ServiceException("Problem creating server socket", e);
        }
    }

    /**
     * Creates new socket connection
     * @param socket socket
     * @return socket connection
     * @throws IOException
     * @throws Exception
     */
    protected Connection createSocketConnection(Socket socket) throws IOException {
        Connection serverConnection = new SSLSocketConnection(socket);
        SSLSocket sslSocket = (SSLSocket) socket;

        if (sslSocket.getNeedClientAuth()) {

        }
        return serverConnection;
    }

    /**
     * Stores keystore password
     * @param passPhrase keystore password
     */
    public void setKeyStorePassword(String passPhrase) {
        this.keystorePassword = passPhrase;
    }

    /**
     * Returns keystore password
     * @return keystore password
     */
    public String getKeyStorePassword() {
        return keystorePassword;
    }

    /**
     * Sets keystore URL
     * @param filename keystore URL
     */
    public void setKeyStoreURL(URL url) {
        this.keystoreURL = url;
    }

    /**
     * Returns keystore filename
     * @return keystore filename
     */
    public URL getKeyStoreURL() {
        return keystoreURL;
    }

    /**
     * Sets keystore file
     * @param filename keystore file
     */
    public void setKeyStoreFile(File file) throws IOException {
//        this.keystoreURL = new URL("file", null, file.getAbsolutePath());
        this.keystoreURL = file.toURI().toURL();
    }

    /**
     * Returns keystore file
     * @return keystore file
     */
    public File getKeyStoreFile() {
        if (keystoreURL.getProtocol().equals("file")) {
            return new File(keystoreURL.getFile());
        }
        return null;
    }

    /**
     * Returns keystore as input stream.
     * @return keystore as input stream
     * @throws IOException
     */
    protected InputStream getKeyStoreInputStream() throws IOException {
        return keystoreURL.openStream();
    }

    public String getTrustStorePassword() {
        return truststorePassword;
    }

    public void setTrustStorePassword(String truststorePassword) {
        this.truststorePassword = truststorePassword;
    }

    public URL getTrustStoreURL() {
        return truststoreURL;
    }

    public void setTrustStoreURL(URL truststoreURL) {
        this.truststoreURL = truststoreURL;
    }

    public void setTrustStoreFile(File file) throws MalformedURLException {
        setTrustStoreURL(file.toURI().toURL());
    }

    protected InputStream getTrustStoreInputStream() throws IOException {
        return truststoreURL.openStream();
    }
}
