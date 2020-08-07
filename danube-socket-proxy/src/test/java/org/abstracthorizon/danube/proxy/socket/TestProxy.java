/*
 * Copyright (c) 2009 Creative Sphere Limited.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import org.junit.Test;

/**
 *
 * @author Daniel Sendula
 */
public class TestProxy {

    public static int SERVER_PORT = 8888;

    public static int PROXY_PORT = 8124;

    public static int PROXY_CONTROL_PORT = 8125;

    public static int CLIENT_PORT = 8124;

    @Test
    public void emptyTest() {

    }

    public static void main(String[] args) throws Exception {
        ProxyOld proxy = new ProxyOld(PROXY_CONTROL_PORT);
        proxy.start();
        try (Socket setupSocket = new Socket("localhost", PROXY_CONTROL_PORT)) {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(setupSocket.getOutputStream()));
            out.println("localhost:" + PROXY_PORT);
            out.println("localhost:" + SERVER_PORT);
            out.flush();

            while (true) {
                Thread.sleep(1000);
            }
        }
    }

    public static void main2(String[] args) throws Exception {
        ProxyOld proxy = new ProxyOld(PROXY_CONTROL_PORT);
        proxy.start();

        Thread server = new Thread(new Runnable() {
            public void run() {
                try {
                    try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {

                        while (true) {
                            final Socket socket = serverSocket.accept();
                            final Random r = new Random();

                            Thread worker = new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                                        out.println("SERVER HELLO");
                                        out.flush();
                                        System.out.println("SERVER: >SERVER HELLO");
                                        String line = in.readLine();
                                        while (line != null) {
                                            System.out.println("SERVER: <" + line);
                                            if (r.nextInt(10) > 7) {
                                                System.out.println("SERVER: Shutting this side!");
                                                line = null;
                                            } else {
                                                out.println("RESPONSE TO \"" + line + "\"");
                                                out.flush();
                                                System.out.println("SERVER: >RESPONSE TO \"" + line + "\"");
                                                line = in.readLine();
                                            }
                                            System.out.println("SERVER: <" + line);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            socket.close();
                                            System.out.println("SERVER: Closed server socket.");
                                        } catch (IOException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            });
                            worker.start();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        server.start();

        try (Socket setupSocket = new Socket("localhost", PROXY_CONTROL_PORT)) {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(setupSocket.getOutputStream()));
            out.println("localhost:" + PROXY_PORT);
            out.println("localhost:" + SERVER_PORT);
            out.flush();


            Thread client = new Thread(new Runnable() {
                public void run() {
                    try {
                        int count = 1;
                        while (true) {
                            System.out.println("Client run count " + count);
                            System.out.println();
                            Thread.sleep(2000);

                            Socket socket = new Socket("localhost", CLIENT_PORT);
                            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String line = in.readLine();
                            System.out.println("CLIENT: <" + line);
                            out.println("Request number " + count);
                            out.flush();
                            System.out.println("CLIENT: >Request number " + count);
                            line = in.readLine();
                            System.out.println("CLIENT: <" + line);
                            if (!("RESPONSE TO \"Request number " + count + "\"").equals(line)) {
                                System.out.println("Client: ERROR - Received line is not correct!");
                            }
                            socket.close();
                            System.out.println("CLIENT: Closed client socket.");
                            count++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.start();
            while (true) {
                Thread.sleep(10);
            }
        }
    }
}
