package edu.hitsz.multiplayer;

import java.io.*;
import java.net.*;

public class GameServer {
    private static final int PORT = 9999;
    private static final int MAX_CLIENTS = 2;

    private ClientHandler[] clients = new ClientHandler[MAX_CLIENTS];
    private int clientCount = 0;
    private int readyCount = 0;

    public static void main(String[] args) {
        new GameServer().start();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("========================================");
            System.out.println("  Aircraft War Server Started!");
            System.out.println("  Port: " + PORT);
            System.out.println("  Waiting for players...");
            System.out.println("========================================");

            while (clientCount < MAX_CLIENTS) {
                Socket socket = serverSocket.accept();
                System.out.println("Player " + (clientCount + 1) + " connected");

                ClientHandler handler = new ClientHandler(socket, clientCount);
                clients[clientCount] = handler;
                new Thread(handler).start();

                clientCount++;
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private synchronized void onPlayerReady() {
        readyCount++;
        System.out.println("Ready players: " + readyCount);
        if (readyCount == MAX_CLIENTS) {
            System.out.println("Both players ready! Sending START...");
            for (ClientHandler client : clients) {
                if (client != null) {
                    client.sendMessage("START");
                }
            }
        }
    }

    class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private int playerIndex;

        public ClientHandler(Socket socket, int playerIndex) {
            this.socket = socket;
            this.playerIndex = playerIndex;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")), true);

                // Send WELCOME
                out.println("WELCOME");
                out.flush();
                System.out.println("Sent WELCOME to player " + (playerIndex + 1));

                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println("[Player " + (playerIndex + 1) + "] " + line);
                    handleMessage(line);
                }
            } catch (IOException e) {
                System.out.println("Player " + (playerIndex + 1) + " disconnected");
            }
        }

        private void handleMessage(String message) {
            // Client is ready after sending INFO
            if (message.startsWith("INFO:")) {
                String[] parts = message.split(",");
                if (parts.length >= 1) {
                    System.out.println("Player " + (playerIndex + 1) + " name: " + parts[0]);
                }
                // Forward INFO to other player
                for (int i = 0; i < MAX_CLIENTS; i++) {
                    if (i != playerIndex && clients[i] != null) {
                        clients[i].sendMessage("INFO" + message.substring(4));
                    }
                }
                // Mark this player as ready
                onPlayerReady();
            }
            // Broadcast other messages
            else if (message.startsWith("SCORE:")) {
                broadcast(message);
            }
            else if (message.startsWith("HP:")) {
                broadcast(message);
            }
            else if (message.equals("DEAD")) {
                broadcast(message);
            }
            else if (message.startsWith("GAMEOVER:")) {
                broadcast(message);
            }
            else if (message.equals("BYE")) {
                broadcast(message);
            }
        }

        private void broadcast(String message) {
            for (int i = 0; i < MAX_CLIENTS; i++) {
                if (i != playerIndex && clients[i] != null) {
                    clients[i].sendMessage(message);
                }
            }
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
                out.flush();
            }
        }
    }
}
