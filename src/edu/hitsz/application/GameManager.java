package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * 游戏管理器
 * 负责管理游戏的各个界面切换和游戏流程控制
 */
public class GameManager {
    
    private JFrame mainFrame;
    private StartGamePanel startGamePanel;
    private AbstractGame gamePanel;
    private GameOverPanel gameOverPanel;
    
    // 游戏设置
    private int currentDifficulty = 2; // 1-简单, 2-普通, 3-困难
    private boolean musicEnabled = true;
    private int currentScore = 0;
    
    public GameManager() {
        initMainFrame();
        showStartScreen();
    }
    
    private void initMainFrame() {
        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame = new JFrame("Aircraft War");
        mainFrame.setSize(Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        mainFrame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        mainFrame.setBounds(((int) screenSize.getWidth() - Main.WINDOW_WIDTH) / 2, 0,
                Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    /**
     * 显示开始界面
     */
    public void showStartScreen() {
        // 清除当前内容
        mainFrame.getContentPane().removeAll();
        
        // 创建开始界面
        startGamePanel = new StartGamePanel();
        startGamePanel.setGameStartListener(new StartGamePanel.GameStartListener() {
            @Override
            public void onGameStart(int difficulty, boolean musicEnabled) {
                currentDifficulty = difficulty;
                GameManager.this.musicEnabled = musicEnabled;
                startGame();
            }
        });
        
        mainFrame.add(startGamePanel);
        mainFrame.revalidate();
        mainFrame.repaint();
        mainFrame.setVisible(true);
    }
    
    /**
     * 开始游戏
     */
    private void startGame() {
        // 清除当前内容
        mainFrame.getContentPane().removeAll();
        
        // 根据难度创建不同的游戏实例
        switch (currentDifficulty) {
            case 1:
                gamePanel = new EasyGame();
                break;
            case 2:
                gamePanel = new NormalGame();
                break;
            case 3:
                gamePanel = new HardGame();
                break;
            default:
                gamePanel = new NormalGame();
                break;
        }
        
        gamePanel.setSoundEnabled(musicEnabled);
        
        mainFrame.add(gamePanel);
        mainFrame.revalidate();
        mainFrame.repaint();
        
        // 启动游戏
        gamePanel.action();
        
        // 设置游戏结束回调
        gamePanel.setGameOverCallback((score, difficulty) -> {
            handleGameOver(score);
        });
    }
    
    /**
     * 处理游戏结束
     */
    private void handleGameOver(int score) {
        this.currentScore = score;
        
        // 显示用户名输入对话框
        UserNameInputDialog nameDialog = new UserNameInputDialog(mainFrame);
        String userName = nameDialog.showDialog();
        
        // 如果用户输入了用户名，添加到排行榜
        if (userName != null && !userName.trim().isEmpty()) {
            // 访问排行榜DAO并添加记录
            // 注意：这里假设 AbstractGame 中 rankList 是 protected 的
            // 如果不是，需要提供一个 getter 方法
            // 为了简单起见，我们直接访问（在 AbstractGame 中已设为 protected）
            gamePanel.rankList.addRecord(userName, currentScore, java.time.LocalDateTime.now());
            gamePanel.rankList.saveRankList();
        }
        
        // 显示游戏结束界面
        showGameOverScreen();
    }
    
    /**
     * 显示游戏结束界面
     */
    private void showGameOverScreen() {
        // 清除当前内容
        mainFrame.getContentPane().removeAll();
        
        // 创建游戏结束界面
        String difficultyText = getDifficultyText(currentDifficulty);
        gameOverPanel = new GameOverPanel(difficultyText);
        gameOverPanel.setGameOverListener(new GameOverPanel.GameOverListener() {
            @Override
            public void onRestart() {
                startGame();
            }
            
            @Override
            public void onBackToMenu() {
                showStartScreen();
            }
        });
        
        mainFrame.add(gameOverPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }
    
    /**
     * 获取难度文本
     */
    private String getDifficultyText(int difficulty) {
        switch (difficulty) {
            case 1: return "简单";
            case 2: return "普通";
            case 3: return "困难";
            default: return "普通";
        }
    }
    
    /**
     * 获取主窗口
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }
    
    /**
     * 启动游戏管理器
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 设置系统外观
                //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new GameManager();
        });
    }
}
