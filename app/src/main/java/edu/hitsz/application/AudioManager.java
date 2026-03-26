package edu.hitsz.application;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.AudioAttributes;

import edu.hitsz.R;

/**
 * 音频管理类（单例）
 * MediaPlayer: 游戏BGM、Boss BGM
 * SoundPool: 子弹击中、炸弹爆炸等短音效
 */
public class AudioManager {

    private static AudioManager instance;

    private MediaPlayer bgmPlayer;
    private MediaPlayer bossBgmPlayer;
    private SoundPool soundPool;

    private int soundBulletHit = -1;
    private int soundBomb = -1;
    private int soundBulletShoot = -1;
    private int soundGameOver = -1;
    private int soundGetSupply = -1;

    private boolean isBossBgmPlaying = false;

    private AudioManager() {}

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (!GameConfig.musicEnabled) return;

        // 游戏BGM
        bgmPlayer = MediaPlayer.create(context, R.raw.bgm);
        bgmPlayer.setLooping(true);
        bgmPlayer.setVolume(0.7f, 0.7f);

        // Boss BGM
        bossBgmPlayer = MediaPlayer.create(context, R.raw.bgm_boss);
        bossBgmPlayer.setLooping(true);
        bossBgmPlayer.setVolume(0.7f, 0.7f);

        // SoundPool 音效
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setAudioAttributes(attrs)
                .setMaxStreams(5)
                .build();
        soundBulletHit = soundPool.load(context, R.raw.bullet_hit, 1);
        soundBomb = soundPool.load(context, R.raw.bomb, 1);
        soundBulletShoot = soundPool.load(context, R.raw.bullet_shoot, 1);
        soundGameOver = soundPool.load(context, R.raw.game_over, 1);
        soundGetSupply = soundPool.load(context, R.raw.get_supply, 1);

        bgmPlayer.start();
    }

    public void playBgm() {
        if (!GameConfig.musicEnabled || bgmPlayer == null) return;
        if (!bgmPlayer.isPlaying()) bgmPlayer.start();
    }

    public void pauseBgm() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) bgmPlayer.pause();
    }

    public void playBossBgm() {
        if (!GameConfig.musicEnabled || bossBgmPlayer == null) return;
        if (isBossBgmPlaying) return;
        pauseBgm();
        bossBgmPlayer.start();
        isBossBgmPlaying = true;
    }

    public void stopBossBgm() {
        if (bossBgmPlayer != null && isBossBgmPlaying) {
            bossBgmPlayer.pause();
            bossBgmPlayer.seekTo(0);
            isBossBgmPlaying = false;
            playBgm();
        }
    }

    public void playBulletHit() {
        if (!GameConfig.musicEnabled || soundPool == null || soundBulletHit == -1) return;
        soundPool.play(soundBulletHit, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playBomb() {
        if (!GameConfig.musicEnabled || soundPool == null || soundBomb == -1) return;
        soundPool.play(soundBomb, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void stopBgm() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) bgmPlayer.pause();
        if (bossBgmPlayer != null && bossBgmPlayer.isPlaying()) bossBgmPlayer.pause();
    }

    public void resumeBgm() {
        if (!GameConfig.musicEnabled) return;
        if (isBossBgmPlaying) {
            if (bossBgmPlayer != null) bossBgmPlayer.start();
        } else {
            if (bgmPlayer != null) bgmPlayer.start();
        }
    }

    public void playNormalBgm() {
        if (!GameConfig.musicEnabled) return;
        if (bossBgmPlayer != null && bossBgmPlayer.isPlaying()) bossBgmPlayer.pause();
        isBossBgmPlaying = false;
        if (bgmPlayer != null) bgmPlayer.start();
    }

    public void playBulletShoot() {
        if (!GameConfig.musicEnabled || soundPool == null || soundBulletShoot == -1) return;
        soundPool.play(soundBulletShoot, 0.5f, 0.5f, 0, 0, 1.0f);
    }

    public void playGameOver() {
        if (!GameConfig.musicEnabled || soundPool == null || soundGameOver == -1) return;
        soundPool.play(soundGameOver, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playGetSupply() {
        if (!GameConfig.musicEnabled || soundPool == null || soundGetSupply == -1) return;
        soundPool.play(soundGetSupply, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void release() {
        if (bgmPlayer != null) { bgmPlayer.release(); bgmPlayer = null; }
        if (bossBgmPlayer != null) { bossBgmPlayer.release(); bossBgmPlayer = null; }
        if (soundPool != null) { soundPool.release(); soundPool = null; }
        isBossBgmPlaying = false;
    }
}
