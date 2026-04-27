package edu.hitsz.application;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.dao.ScoreDao;
import edu.hitsz.dao.ScoreDaoImpl;
import edu.hitsz.application.GameConfig;
import edu.hitsz.model.ScoreRecord;
import edu.hitsz.util.TimeUtils;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private boolean gameOverDialogShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageManager.init(this);
        AudioManager.getInstance().init(this);

        boolean multiplayerMode = getIntent().getBooleanExtra("MULTIPLAYER_MODE", false);

        if (multiplayerMode) {
            String playerName = getIntent().getStringExtra("PLAYER_NAME");
            String serverIp = getIntent().getStringExtra("SERVER_IP");

            gameView = new GameView(this, multiplayerMode, playerName, serverIp);
        } else {
            gameView = new GameView(this);
            gameView.setOnGameOverListener(this::showSaveScoreDialog);
        }

        setContentView(gameView);
    }

    private void showSaveScoreDialog(int finalScore) {
        if (gameOverDialogShown) {
            return;
        }
        gameOverDialogShown = true;

        EditText editText = new EditText(this);
        editText.setHint("请输入玩家姓名");

        new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage("你的分数是：" + finalScore + "\n请输入姓名后保存到排行榜")
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton("保存并查看排行榜", (dialog, which) -> {
                    String playerName = editText.getText().toString().trim();
                    if (TextUtils.isEmpty(playerName)) {
                        playerName = "匿名玩家";
                    }

                    ScoreDao scoreDao = new ScoreDaoImpl(this);
                    ScoreRecord record = new ScoreRecord(
                            playerName,
                            finalScore,
                            GameConfig.difficulty,
                            TimeUtils.now()
                    );
                    scoreDao.insert(record);

                    Toast.makeText(this, "成绩已保存", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, LeaderboardActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("仅退出", (dialog, which) -> {
                    finish();
                })
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AudioManager.getInstance().pauseBgm();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AudioManager.getInstance().resumeBgm();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioManager.getInstance().release();
    }
}
