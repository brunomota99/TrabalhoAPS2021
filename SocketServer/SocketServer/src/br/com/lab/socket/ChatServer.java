package br.com.lab.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    public static Map<String, SocketListener> USERS = new HashMap<>();

    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        while (true) {
            Socket newConnection = serverSocket.accept();

            System.out.println("New Connection Established. " + newConnection.getInetAddress().getHostAddress());

            SocketListener listener = new SocketListener(newConnection);
            new Thread(listener).start();
        }
    }

    public static void main(String[] args) throws IOException {
        ChatServer server = new ChatServer();
        server.start(4444);
    }
}
