package edu.hitsz.multiplayer;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Singleton Socket Client for multiplayer
 */
public class GameClient {
    private static final String TAG = "GameClient";
    private static final int SERVER_PORT = 9999;
    private static GameClient instance = null;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final AtomicBoolean isConnecting = new AtomicBoolean(false);

    private String serverIp;
    private String localPlayerName;

    private GameClientListener listener;
    private boolean listenerCalled = false;

    public interface GameClientListener {
        void onConnected();
        void onDisconnected();
        void onConnectionError(String error);
        void onScoreUpdate(int score);
        void onHpUpdate(int hp);
        void onPlayerDead();
        void onGameOver(int finalScore);
        void onOpponentInfoReceived(String name, int score, int hp, boolean alive);
        void onGameStart();
        void onOpponentDisconnected();
    }

    public static synchronized GameClient getInstance() {
        if (instance == null) {
            instance = new GameClient();
        }
        return instance;
    }

    public static synchronized void resetInstance() {
        if (instance != null) {
            instance.disconnect();
            instance = null;
        }
    }

    public void setListener(GameClientListener listener) {
        this.listener = listener;
    }

    public void connect(String serverIp, String playerName) {
        if (isConnected.get() || isConnecting.get()) {
            Log.w(TAG, "Already connected or connecting");
            return;
        }

        this.serverIp = serverIp;
        this.localPlayerName = playerName;
        listenerCalled = false;

        isConnecting.set(true);

        executorService.execute(() -> {
            try {
                Log.i(TAG, "Connecting to " + serverIp + ":" + SERVER_PORT);
                socket = new Socket();
                socket.connect(new InetSocketAddress(serverIp, SERVER_PORT), 5000);
                socket.setKeepAlive(true);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8")), true);

                isConnecting.set(false);
                isConnected.set(true);
                Log.i(TAG, "Connected to server");

                // Wait for welcome message
                String welcome = in.readLine();
                Log.d(TAG, "Received: " + welcome);

                if ("WELCOME".equals(welcome)) {
                    sendMessage("INFO:" + localPlayerName + ",0,100,true");
                    if (listener != null && !listenerCalled) {
                        listenerCalled = true;
                        listener.onConnected();
                    }
                    readMessages();
                } else {
                    Log.e(TAG, "Unexpected welcome message: " + welcome);
                    throw new IOException("Unexpected server response");
                }

            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host: " + serverIp, e);
                isConnecting.set(false);
                if (listener != null) {
                    listener.onConnectionError("Unknown host: " + serverIp);
                }
            } catch (IOException e) {
                Log.e(TAG, "Connection failed", e);
                isConnecting.set(false);
                if (listener != null) {
                    listener.onConnectionError("Connection failed: " + e.getMessage());
                }
                cleanup();
            }
        });
    }

    private void readMessages() {
        try {
            String line;
            while (isConnected.get() && (line = in.readLine()) != null) {
                Log.d(TAG, "Received: " + line);
                handleMessage(line);
            }
        } catch (IOException e) {
            if (isConnected.get()) {
                Log.e(TAG, "Error reading message", e);
            }
        }
        cleanup();
    }

    private void handleMessage(String message) {
        if (message == null || listener == null) return;

        try {
            if (message.startsWith("SCORE:")) {
                listener.onScoreUpdate(Integer.parseInt(message.substring(6)));
            } else if (message.startsWith("HP:")) {
                listener.onHpUpdate(Integer.parseInt(message.substring(3)));
            } else if (message.equals("DEAD")) {
                listener.onPlayerDead();
            } else if (message.startsWith("GAMEOVER:")) {
                listener.onGameOver(Integer.parseInt(message.substring(9)));
            } else if (message.startsWith("INFO:")) {
                String[] parts = message.substring(5).split(",");
                if (parts.length >= 4) {
                    listener.onOpponentInfoReceived(parts[0],
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Boolean.parseBoolean(parts[3]));
                }
            } else if (message.equals("START")) {
                listener.onGameStart();
            } else if (message.equals("BYE") || message.equals("DISCONNECT")) {
                listener.onOpponentDisconnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling message: " + message, e);
        }
    }

    private void cleanup() {
        isConnected.set(false);
        isConnecting.set(false);

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Error cleaning up", e);
        }

        if (listener != null) {
            listener.onDisconnected();
        }
    }

    public void disconnect() {
        if (!isConnected.get() && !isConnecting.get()) {
            return;
        }
        try {
            sendMessage("BYE");
        } catch (Exception e) {
            // Ignore
        }
        cleanup();
    }

    private void sendMessage(String message) {
        if (out != null && isConnected.get()) {
            out.println(message);
            Log.d(TAG, "Sent: " + message);
        }
    }

    public void sendScoreUpdate(int score) {
        sendMessage("SCORE:" + score);
    }

    public void sendHpUpdate(int hp) {
        sendMessage("HP:" + hp);
    }

    public void sendPlayerDead() {
        sendMessage("DEAD");
    }

    public void sendGameOver(int finalScore) {
        sendMessage("GAMEOVER:" + finalScore);
    }

    public void sendOpponentInfo(String name, int score, int hp, boolean alive) {
        sendMessage("INFO:" + name + "," + score + "," + hp + "," + alive);
    }

    public boolean isConnected() {
        return isConnected.get();
    }

    public boolean isConnecting() {
        return isConnecting.get();
    }
}
