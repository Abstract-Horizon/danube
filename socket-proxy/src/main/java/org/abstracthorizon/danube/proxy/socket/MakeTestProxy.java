package org.abstracthorizon.danube.proxy.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class MakeTestProxy {

    public static int SERVER_PORT = 8123;
    
    public static int PROXY_PORT = 8124;
    
    public static int CLIENT_PORT = 8124;
    
    public static void main(String[] args) throws Exception {
        
        Thread proxy = new Thread(new Runnable() {
            public void run() {
                try {
                    Selector selector = SelectorProvider.provider().openSelector();
                    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                    serverSocketChannel.configureBlocking(false);

                    InetSocketAddress inetSocketAddress = new InetSocketAddress(PROXY_PORT);
                    serverSocketChannel.socket().bind(inetSocketAddress);

                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

                    int keysAdded = selector.select();
                    while (true) {
                        Set<SelectionKey> selectedKeys = selector.selectedKeys();
                        if (selectedKeys.size() == 0) {
                            System.out.println("PROXY : selecting keys..." + keysAdded + "/" + selectedKeys.size() + "(" + Thread.currentThread() + ")");
                        }
                        Iterator<SelectionKey> iterator = selectedKeys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            iterator.remove();
                            Connection c = (Connection)key.attachment();
                            
                            if (!key.isValid()) {
                                System.out.println("PROXY : Key is not valid " + c + " closing channel");
                                c.in.close();
                            } else if (key.isAcceptable()) {
                                System.out.println("PROXY : Key is acceptable " + c);
                                ServerSocketChannel channel = (ServerSocketChannel)key.channel();

                                SocketChannel outboundChannel = SocketChannel.open();
                                outboundChannel.configureBlocking(false);
                                InetSocketAddress serverSocketAddress = new InetSocketAddress("localhost", SERVER_PORT);

                                Socket inbound = channel.accept().socket();
                                SocketChannel inboundChannel = inbound.getChannel();
                                inboundChannel.configureBlocking(false);
                                
                                Connection outConnection = new Connection(outboundChannel, inboundChannel, false);
                                Connection inConnection = new Connection(inboundChannel, outboundChannel, true);
                                
                                outboundChannel.register(selector, SelectionKey.OP_CONNECT, outConnection);
                                outboundChannel.connect(serverSocketAddress);

                                inboundChannel.register(selector, SelectionKey.OP_READ, inConnection);

                            } else if (key.isReadable()) {
                                System.out.println("PROXY : Key is readable " + c);
                                c.buffer.clear();
                                int size = c.in.read(c.buffer);
                                if (size == -1) {
                                    System.out.println("PROXY : Closing channel " + c);
                                    key.channel().close();
                                    key.cancel();
                                    c.out.close();
                                    c.out.socket().close();
                                } else if (size == 0) {
                                    System.out.println("PROXY : read " + c + " "+ c.buffer.position() + "? What now?");
                                } else {
                                    System.out.println("PROXY : read " + c + " " + c.buffer.position());
                                    c.buffer.flip();
                                    c.out.write(c.buffer);
                                }
                            } else if (key.isWritable()) {
                                System.out.println("PROXY : Key is writable: " + c);
                            } else if (key.isConnectable()) {
                                System.out.println("PROXY : Key is connectable: " + c);
                                SocketChannel socketChannel = (SocketChannel)key.channel();
                                socketChannel.finishConnect();
                                key.interestOps(SelectionKey.OP_READ);
                            } else {
                                System.out.println("PROXY : No idea what! " + c);
                            }
                        }
                        
                        keysAdded = selector.select();
                    }
//                    System.out.println("KeysAdded = " + keysAdded);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        proxy.start();
        
        
        Thread server = new Thread(new Runnable() {
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                    
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        server.start();
        
        
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
        
    }
    
    public static class Connection {
        private static int counter = 1;
        public ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        public SocketChannel in;
        public SocketChannel out;
        public boolean inbound;
        private int count;
        public Connection(SocketChannel in, SocketChannel out, boolean inbound) {
            this.in = in;
            this.out = out;
            this.inbound = inbound;
            count = counter;
            if (inbound) {
                counter++;
            }
        }
        
        public String toString() {
            if (inbound) {
                return "Inbound(" + count + ")";
            } else {
                return "Outbound(" + count + ")";
            }
        }
    }
    
}
