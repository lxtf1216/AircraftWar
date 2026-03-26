package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import edu.hitsz.R;

public class DifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        findViewById(R.id.btn_easy).setOnClickListener(v -> startGame("EASY"));
        findViewById(R.id.btn_normal).setOnClickListener(v -> startGame("NORMAL"));
        findViewById(R.id.btn_hard).setOnClickListener(v -> startGame("HARD"));
    }

    private void startGame(String difficulty) {
        GameConfig.difficulty = difficulty;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
