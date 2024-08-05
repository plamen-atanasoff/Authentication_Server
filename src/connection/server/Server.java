package connection.server;

import businesslogicnew.controller.Controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Server {
    private static final int BUFFER_SIZE = 1024;
    private static final String HOST = "localhost";
    private Selector selector;
    private ByteBuffer buffer;
    private final int port;
    private boolean isWorking;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            configure(serverSocketChannel);

            while (isWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }

                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if (key.isReadable()) {
                            SocketChannel clientChannel = (SocketChannel) key.channel();

                            String clientInput = getClientInput(clientChannel);

                            if (clientInput == null) {
                                continue;
                            }

                            String result;
                            try {
                                result = Controller.getInstance().execute(clientInput);
                            } catch (Exception e) {
                                result = e.toString();
                            }

                            System.out.println(result);

                            writeClientOutput(clientChannel, result + System.lineSeparator());
                        } else if (key.isAcceptable()) {
                            accept(key);
                        }

                        keyIterator.remove();
                    }
                } catch (SocketException e) {
                    System.out.println("Client has disconnected forcefully");
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to start server", e);
        }
    }

    private void configure(ServerSocketChannel serverSocketChannel) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(HOST, port));
        serverSocketChannel.configureBlocking(false);
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        buffer = ByteBuffer.allocate(BUFFER_SIZE);

        isWorking = true;
    }

    public void stop() {
        isWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) {
        final int port = 7777;
        Server server = new Server(port);

        server.start();
    }
}

