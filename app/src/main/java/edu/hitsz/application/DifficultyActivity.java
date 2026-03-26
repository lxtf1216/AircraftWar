package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import edu.hitsz.R;

public class DifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        Switch switchMusic = findViewById(R.id.switch_music);
        switchMusic.setChecked(GameConfig.musicEnabled);
        switchMusic.setOnCheckedChangeListener((btn, isChecked) ->
                GameConfig.musicEnabled = isChecked);

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
