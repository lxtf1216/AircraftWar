package edu.hitsz.application;

import java.util.HashMap;
import java.util.Map;

/**
 * 音效管理器
 * 使用单例模式管理游戏中的所有音效
 */
public class SoundManager {
    
    private static SoundManager instance;
    
    // 音效文件路径常量
    public static final String BGM_NORMAL = "src/videos/bgm.wav";
    public static final String BGM_BOSS = "src/videos/bgm_boss.wav";
    public static final String BULLET_SHOOT = "src/videos/bullet.wav";
    public static final String BULLET_HIT = "src/videos/bullet_hit.wav";
    public static final String BOMB_EXPLOSION = "src/videos/bomb_explosion.wav";
    public static final String GET_SUPPLY = "src/videos/get_supply.wav";
    public static final String GAME_OVER = "src/videos/game_over.wav";
    
    // 音效开关
    private boolean soundEnabled = true;
    
    // 背景音乐线程
    private MusicThread backgroundMusic;
    private String currentBgm = "";
    
    // 音效线程池
    private Map<String, MusicThread> soundEffects;
    
    private SoundManager() {
        soundEffects = new HashMap<>();
    }
    

    public static synchronized SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * 播放背景音乐（循环播放）
     */
    public void playBackgroundMusic(String musicPath) {
        if (!soundEnabled) {
            return;
        }
        
        // 如果当前已经在播放相同的背景音乐，则不重复播放
        if (currentBgm.equals(musicPath) && backgroundMusic != null && backgroundMusic.isPlaying()) {
            return;
        }
        
        // 停止当前背景音乐
        stopBackgroundMusic();
        
        // 播放新的背景音乐
        backgroundMusic = new MusicThread(musicPath, true); // 循环播放
        backgroundMusic.start();
        currentBgm = musicPath;
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stopMusic();
            backgroundMusic = null;
        }
        currentBgm = "";
    }
    public void playSound(String soundPath) {
        if (!soundEnabled) {
            return;
        }
        
        // 创建新的音效线程并播放
        MusicThread soundThread = new MusicThread(soundPath, false); // 不循环
        soundThread.start();
        
        // 将音效线程添加到管理中（用于可能的停止操作）
        soundEffects.put(soundPath + System.currentTimeMillis(), soundThread);
        
        // 清理已完成的音效线程
        cleanupFinishedSounds();
    }

    public void stopAllSounds() {
        // 停止背景音乐
        stopBackgroundMusic();
        
        // 停止所有音效
        for (MusicThread soundThread : soundEffects.values()) {
            if (soundThread != null) {
                soundThread.stopMusic();
            }
        }
        soundEffects.clear();
    }
    

    private void cleanupFinishedSounds() {
        soundEffects.entrySet().removeIf(entry -> {
            MusicThread thread = entry.getValue();
            return thread == null || !thread.isPlaying();
        });
    }
    

    public void playNormalBGM() {
        playBackgroundMusic(BGM_NORMAL);
    }

    public void playBossBGM() {
        playBackgroundMusic(BGM_BOSS);
    }

    public void playBulletSound() {
        playSound(BULLET_SHOOT);
    }

    public void playBulletHitSound() {
        playSound(BULLET_HIT);
    }

    public void playBombExplosionSound() {
        playSound(BOMB_EXPLOSION);
    }

    public void playGetSupplySound() {
        playSound(GET_SUPPLY);
    }

    public void playGameOverSound() {
        playSound(GAME_OVER);
    }
}
