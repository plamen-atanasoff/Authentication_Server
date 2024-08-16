package connection.server;

import businesslogicnew.controller.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class Server {
    private static final String CLIENT_DISCONNECTED_MESSAGE = "Client %s has disconnected forcefully";

    private static final int BUFFER_SIZE = 1024;

    private static final String HOST = "localhost";

    private Selector selector;

    private ByteBuffer buffer;

    private final int port;

    private boolean isWorking;

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            configure(serverSocketChannel);

            while (isWorking) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                processKeys(keyIterator);
            }
        } catch (IOException e) {
            selector.close();

            throw e;
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

    private void processKeys(Iterator<SelectionKey> it) throws IOException {
        while (it.hasNext()) {
            SelectionKey key = it.next();
            if (key.isReadable()) {
                read(key);
            } else if (key.isAcceptable()) {
                accept(key);
            }

            it.remove();
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();

        try {
            String clientInput = getClientInput(clientChannel);

            if (clientInput == null) {
                return;
            }

            String result;
            try {
                result = Controller.getInstance().execute(clientInput, key);
            } catch (RuntimeException e) {
                result = e.toString();
            }

            writeClientOutput(clientChannel, result + System.lineSeparator());
        } catch (SocketException e) {
            handleSocketException(clientChannel, key);
        }
    }

    private void handleSocketException(SocketChannel clientChannel, SelectionKey key) throws IOException {
        SocketAddress disconnectedClientAddress = clientChannel.getRemoteAddress();
        System.out.printf((CLIENT_DISCONNECTED_MESSAGE) + "%n", disconnectedClientAddress);

        key.cancel();
        clientChannel.close();
    }

    public static void main(String[] args) throws IOException {
        final int port = 7777;
        Server server = new Server(port);

        server.start();
    }
}

