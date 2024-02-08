package bg.sofia.uni.fmi.mjt.authenticationserver.connection.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private int serverPort;

    public Client(int serverPort) {
        this.serverPort = serverPort;
    }

    public void run() {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, "UTF-8"));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, "UTF-8"), true);
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, serverPort));

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();

                if ("quit".equals(message)) {
                    break;
                }

                writer.println(message);

                String reply = reader.readLine();
                System.out.println("The server replied <" + reply + ">");
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }

    public static void main(String[] args) {
        final int port = 7777;
        Client client = new Client(port);

        client.run();
    }
}
