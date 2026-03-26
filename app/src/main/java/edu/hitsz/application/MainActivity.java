package edu.hitsz.application;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageManager.init(this);
        AudioManager.getInstance().init(this);
        gameView = new GameView(this);
        setContentView(gameView);
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

    public class AppSettings {
        public int WINDOW_WIDTH;
        public int WINDOW_HEIGHT;

        public void setScreenSize(int width, int height) {
            WINDOW_WIDTH = width;
            WINDOW_HEIGHT = height;
        }
    }
}
