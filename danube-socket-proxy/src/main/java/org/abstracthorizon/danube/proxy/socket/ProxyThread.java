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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 
 * @author Daniel Sendula
 */
public class ProxyThread extends Thread {
    Socket incoming, outgoing;

    // Thread constructor

    ProxyThread(Socket in, Socket out) {
        this.incoming = in;
        this.outgoing = out;
    }

    // Overwritten run() method of thread -- does the data transfers

    public void run() {
        byte[] buffer = new byte[60];
        int numberRead = 0;
        OutputStream ToClient;
        InputStream FromClient;

        try {
            ToClient = this.outgoing.getOutputStream();
            FromClient = this.incoming.getInputStream();
            while (true) {
                numberRead = FromClient.read(buffer, 0, 50);
                System.out.println("read " + numberRead);
                // buffer[numberRead] = buffer[0] = (byte)'+';

                if (numberRead == -1) {
                    this.incoming.close();
                    this.outgoing.close();
                }

                ToClient.write(buffer, 0, numberRead);

            }

        } catch (IOException e) {
            // Empty
        } catch (ArrayIndexOutOfBoundsException e) {
            // Empty
        }

    }

}
