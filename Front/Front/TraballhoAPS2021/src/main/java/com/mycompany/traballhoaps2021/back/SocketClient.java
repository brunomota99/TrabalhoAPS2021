/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.traballhoaps2021.back;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;
import java.util.function.Function;

/**
 *
 * @author bruno
 */
public class SocketClient {
    
    private static final String DOWNLOADS_FOLDER = "C:\\Users\\bruno\\Downloads\\ChatFiles";
    private String connectedUser;
    private String[] onlineUsers;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageReceiveCallback messageReceiveCallback;
    private UserConnectionCallback userConnectionCallback;
    private FileReceiveCallback fileReceiveCallback;

    public void startConnection(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void login(String user, String password) throws IOException {
        if ("123".equals(password)) {
            sendMessage("login:" + user);
            receiveMessage();
            this.connectedUser = user;
        } else {
            throw new RuntimeException("Wrong password");
        }
    }

    public void sendMessageTo(String userToSend, String message) throws IOException {
        sendMessage(userToSend + ":message:" + this.connectedUser + ":" + message);
    }

    public void sendFileTo(String userToSend, String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] bytes = Files.readAllBytes(path);
        String base64 = Base64.getEncoder().encodeToString(bytes);

        sendMessage(userToSend + ":file:" + this.connectedUser + ":" + path.getFileName().toString() + ";" + base64);
    }

    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    public void receiveMessage() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = in.readLine();

                    if (message.contains(":")) {
                        String[] commands = message.split(":");

                        if (commands.length > 1) {
                            switch (commands[0]) {
                                case "message":
                                    receiveMessage(commands);
                                    break;
                                case "users":
                                    updateOnlineUsers(commands[1]);
                                    break;
                                case "file":
                                    saveFile(commands);
                                    break;
                            }
                        }
                    } else {      
                        System.out.println(message);                        
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void receiveMessage(String[] message) {
        System.out.println(message);
        
        if (messageReceiveCallback != null) {
            try {
                if (message.length == 3) {
                    messageReceiveCallback.onMessage(message[1], message[2]);
                } else {
                    messageReceiveCallback.onMessage("anonymous", message[1]);
                }
            } catch (Throwable e) {
                System.out.println("Nao foi possivel executar funcao de callback para recebimento de mensagem");
                e.printStackTrace();
            }
        }
    }

    private void updateOnlineUsers(String users) {
        this.onlineUsers = users.split(";");
        // TODO: Momento que retorna os usuarios logados para exibir na lista do chat
        for (int i = 0; i < this.onlineUsers.length; i++) {
            System.out.println("Usuario logado: " + this.onlineUsers[i]);
        }
        
        if (userConnectionCallback != null) {
            try {
                userConnectionCallback.onUserConnection(this.onlineUsers);
            } catch (Throwable e) {
                System.out.println("Nao foi possivel executar funcao de callback para conexao de usuarios");
                e.printStackTrace();
            }
        }
    }

    private void saveFile(String[] fileCommand) throws IOException {
        String[] fileMessage = fileCommand[2].split(";");
        String fileName = fileMessage[0];
        String fileContent = fileMessage[1];
        byte[] bytes = Base64.getDecoder().decode(fileContent);
        OutputStream writer = Files.newOutputStream(Path.of(DOWNLOADS_FOLDER, fileName));
        writer.write(bytes);
        writer.close();
        
        if (fileReceiveCallback != null) {
            fileReceiveCallback.onFileReceive(fileCommand[1], fileName);
        }
    }
    
    public SocketClient onUserConnection(UserConnectionCallback callback) {
        this.userConnectionCallback = callback;
        
        return this;
    }
    
    public SocketClient onMessageReceive(MessageReceiveCallback callback) {
        this.messageReceiveCallback = callback;
        
        return this;
    }
    
    public SocketClient onFileReceive(FileReceiveCallback callback) {
        this.fileReceiveCallback = callback;
        
        return this;
    }

    public String getConnectedUser() {
        return connectedUser;
    }

    public String[] getOnlineUsers() {
        if (onlineUsers == null) {
            onlineUsers = new String[0];
        }

        return onlineUsers;
    }
    
}
