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
    private Game gamePanel;
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
        
        // 创建游戏界面
        gamePanel = new Game();
        
        // 这里可以根据难度设置游戏参数
        // 由于要求不修改现有代码，暂时使用默认设置
        
        mainFrame.add(gamePanel);
        mainFrame.revalidate();
        mainFrame.repaint();
        
        // 启动游戏
        gamePanel.action();
        
        // 监听游戏结束（这里需要修改Game类来支持回调，但按要求不修改现有代码）
        // 暂时使用定时器检查游戏状态
        Timer gameStatusTimer = new Timer(1000, e -> {
            if (isGameOver()) {
                ((Timer) e.getSource()).stop();
                handleGameOver();
            }
        });
        gameStatusTimer.start();
    }
    
    /**
     * 检查游戏是否结束
     * 这是一个简化的实现，实际应该通过Game类的回调来处理
     */
    private boolean isGameOver() {
        try {
            // 通过反射检查游戏结束标志
            java.lang.reflect.Field field = Game.class.getDeclaredField("gameOverFlag");
            field.setAccessible(true);
            return (Boolean) field.get(gamePanel);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 处理游戏结束
     */
    private void handleGameOver() {
        // 获取游戏分数
        try {
            java.lang.reflect.Field scoreField = Game.class.getDeclaredField("score");
            scoreField.setAccessible(true);
            currentScore = (Integer) scoreField.get(gamePanel);
        } catch (Exception e) {
            currentScore = 0;
        }
        
        // 显示用户名输入对话框
        UserNameInputDialog nameDialog = new UserNameInputDialog(mainFrame);
        String userName = nameDialog.showDialog();
        
        // 如果用户输入了用户名，添加到排行榜
        if (userName != null && !userName.trim().isEmpty()) {
            try {
                java.lang.reflect.Field rankListField = Game.class.getDeclaredField("rankList");
                rankListField.setAccessible(true);
                Object rankListDao = rankListField.get(gamePanel);
                
                // 调用addRecord方法
                rankListDao.getClass().getMethod("addRecord", String.class, int.class, java.time.LocalDateTime.class)
                        .invoke(rankListDao, userName, currentScore, java.time.LocalDateTime.now());
                
                // 保存排行榜
                rankListDao.getClass().getMethod("saveRankList").invoke(rankListDao);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
