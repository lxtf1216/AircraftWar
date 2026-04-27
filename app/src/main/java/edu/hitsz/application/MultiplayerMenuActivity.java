package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.multiplayer.GameClient;
import edu.hitsz.R;

public class MultiplayerMenuActivity extends AppCompatActivity {

    private EditText etServerIp;
    private EditText etPlayerName;
    private TextView tvStatus;
    private Button btnJoinGame;
    private Button btnBack;

    private String playerName = "";
    private boolean gameStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_menu);

        etServerIp = findViewById(R.id.et_server_ip);
        etPlayerName = findViewById(R.id.et_player_name);
        tvStatus = findViewById(R.id.tv_status);
        btnJoinGame = findViewById(R.id.btn_join_game);
        btnBack = findViewById(R.id.btn_back);

        etPlayerName.setText("Player1");
        etServerIp.setText("10.0.2.2");

        btnJoinGame.setOnClickListener(v -> joinGame());
        btnBack.setOnClickListener(v -> finish());
    }

    private void joinGame() {
        String ip = etServerIp.getText().toString().trim();
        playerName = etPlayerName.getText().toString().trim();

        if (ip.isEmpty()) {
            Toast.makeText(this, "Enter server IP", Toast.LENGTH_SHORT).show();
            return;
        }
        if (playerName.isEmpty()) {
            playerName = "Player1";
        }

        updateStatus("Connecting...");
        btnJoinGame.setEnabled(false);

        // Reset singleton to get fresh connection
        GameClient.resetInstance();
        GameClient client = GameClient.getInstance();

        client.setListener(new GameClient.GameClientListener() {
            @Override
            public void onConnected() {
                runOnUiThread(() -> updateStatus("Connected! Waiting for opponent..."));
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(() -> {
                    if (!gameStarted) {
                        updateStatus("Disconnected");
                        btnJoinGame.setEnabled(true);
                    }
                });
            }

            @Override
            public void onConnectionError(String error) {
                runOnUiThread(() -> {
                    updateStatus("Error: " + error);
                    btnJoinGame.setEnabled(true);
                });
            }

            @Override
            public void onScoreUpdate(int score) {}

            @Override
            public void onHpUpdate(int hp) {}

            @Override
            public void onPlayerDead() {}

            @Override
            public void onGameOver(int finalScore) {}

            @Override
            public void onOpponentInfoReceived(String name, int score, int hp, boolean alive) {}

            @Override
            public void onGameStart() {
                gameStarted = true;
                runOnUiThread(() -> {
                    Intent intent = new Intent(MultiplayerMenuActivity.this, MainActivity.class);
                    intent.putExtra("MULTIPLAYER_MODE", true);
                    intent.putExtra("PLAYER_NAME", playerName);
                    intent.putExtra("SERVER_IP", etServerIp.getText().toString().trim());
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onOpponentDisconnected() {
                runOnUiThread(() -> updateStatus("Opponent left"));
            }
        });

        client.connect(ip, playerName);
    }

    private void updateStatus(String status) {
        tvStatus.setText(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!gameStarted) {
            GameClient.resetInstance();
        }
    }

    @Override
    public void onBackPressed() {
        if (!gameStarted) {
            GameClient.resetInstance();
        }
        super.onBackPressed();
    }
}
