package connection.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {

    private static final String CLIENT_ENTER_MESSAGE_PROMPT = "Enter message: ";

    private static final String QUIT_STRING = "quit";

    private static final String SERVER_REPLY_MESSAGE_FORMAT = "The server replied <%s>";

    private static final String SERVER_COMMUNICATION_ERROR_MESSAGE = "There is a problem with the network communication";
    private static final String SERVER_HOST = "localhost";
    private final int serverPort;

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }

    public void run() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, serverPort));

            while (true) {
                System.out.print(CLIENT_ENTER_MESSAGE_PROMPT);
                String message = scanner.nextLine();

                if (QUIT_STRING.equals(message)) {
                    break;
                }

                writer.println(message);

                String reply = reader.readLine();
                System.out.printf(SERVER_REPLY_MESSAGE_FORMAT, reply);
                System.out.println();
            }
        } catch (IOException e) {
            throw new RuntimeException(SERVER_COMMUNICATION_ERROR_MESSAGE, e);
        }
    }

    public static void main(String[] args) {
        final int port = 7777;
        Client client = new Client(port);

        client.run();
    }
}
