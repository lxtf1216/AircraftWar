package edu.hitsz.application;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageManager.init(this);
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class AppSettings {
        public int WINDOW_WIDTH;
        public int WINDOW_HEIGHT;

        /**
         * 由 GameView 在 surfaceChanged 中调用，更新实际屏幕尺寸
         */
        public void setScreenSize(int width, int height) {
            WINDOW_WIDTH = width;
            WINDOW_HEIGHT = height;
        }
    }
}