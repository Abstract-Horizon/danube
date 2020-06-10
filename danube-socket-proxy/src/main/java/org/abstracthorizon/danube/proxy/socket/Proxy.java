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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 * 
 * @author Daniel Sendula
 */
public class Proxy {
    public static void main(String args[]) throws IOException {

        // parse arguments from command line

        int localport = 8841;
        int remoteport = 8041;
        String remotehost = "127.0.0.1";

        Socket incoming, outgoing = null;
        ServerSocket server = null;

        server = new ServerSocket(localport);

        // Loop to listen for incoming connection, and accept if there is one

        while (true) {
            try {
                incoming = server.accept();
                // Create the 2 threads for the incoming and outgoing traffic of proxy server
                outgoing = new Socket(remotehost, remoteport);

                ProxyThread thread1 = new ProxyThread(incoming, outgoing);
                thread1.start();

                ProxyThread thread2 = new ProxyThread(outgoing, incoming);
                thread2.start();
            } catch (UnknownHostException e) {
                // Test and make connection to remote host
                System.err.println("Error: Unknown Host " + remotehost);
                System.exit(-1);
            } catch (IOException e) {
                System.exit(-2);// continue;
            }
        }
    }

}
