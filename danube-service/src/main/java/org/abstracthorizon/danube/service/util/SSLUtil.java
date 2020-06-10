/*
 * Copyright (c) 2004-2007 Creative Sphere Limited.
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
package org.abstracthorizon.danube.service.util;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * Utility class for SSL connections
 *
 * @author Daniel Sendula
 */
public class SSLUtil {

    /**
     * This method returns SSL socket factory based on passphrase from given keystore
     * @param passphrase passphrase of keystore
     * @param keystore keystore input stream
     * @return SSL socket factory
     */
    public static SSLSocketFactory getSocketFactory(char[] passphrase, InputStream keystore) {
        SSLSocketFactory ssf = null;
        try {
            // set up key manager to do server authentication
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;

            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");

            ks.load(keystore, passphrase);
            kmf.init(ks, passphrase);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            ctx.init(kmf.getKeyManagers(), new TrustManager[]{tm}, null);

            ssf = ctx.getSocketFactory();
            return ssf;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method returns SSL server socket factory based on passphrase from given keystore
     * @param passphrase passphrase of keystore
     * @param keystore keystore input stream
     * @return SSL socket factory
     */
    public static SSLServerSocketFactory getServerSocketFactory(char[] passphrase, InputStream keystore) {
        return getServerSocketFactory(passphrase, keystore, null, null);
    }

    public static SSLServerSocketFactory getServerSocketFactory(char[] passphrase, InputStream keystore, char[] trustPassphrase, InputStream truststore) {
        SSLServerSocketFactory ssf = null;
        try {

            SSLContext ctx = SSLContext.getInstance("TLS");

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(keystore, passphrase);
            kmf.init(ks, passphrase);

            KeyStore ts = null;
            if (trustPassphrase != null) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                ts = KeyStore.getInstance("JKS");
                ts.load(truststore, trustPassphrase);
                tmf.init(ts);

                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            } else {
                ctx.init(kmf.getKeyManagers(), null, null);
            }

            ssf = ctx.getServerSocketFactory();
            return ssf;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
