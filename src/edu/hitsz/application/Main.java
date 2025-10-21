package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static void main(String[] args) {

        System.out.println("Hello Aircraft War");

        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        //设置窗口的大小和位置,居中放置
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 创建开始界面
        showStartScreen(frame);
        
        frame.setVisible(true);
    }
    
    /**
     * 显示开始界面
     */
    private static void showStartScreen(JFrame frame) {
        frame.getContentPane().removeAll();
        
        StartGamePanel startPanel = new StartGamePanel();
        startPanel.setGameStartListener(new StartGamePanel.GameStartListener() {
            @Override
            public void onGameStart(int difficulty, boolean musicEnabled) {
                startGame(frame, difficulty, musicEnabled);
            }
        });
        
        frame.add(startPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    /**
     * 开始游戏
     */
    private static void startGame(JFrame frame, int difficulty, boolean musicEnabled) {
        // 清除当前界面
        frame.getContentPane().removeAll();
        
        // 创建游戏界面
        AbstractGame game;
        switch (difficulty) {
            case 1:
                game = new EasyGame();
                break;
            case 2:
                game = new NormalGame();
                break;
            case 3:
                game = new HardGame();
                break;
            default:
                game = new NormalGame();
                break;
        }
        
        // 设置音效开关
        game.setSoundEnabled(musicEnabled);
        
        // 设置游戏结束回调
        game.setGameOverCallback(new AbstractGame.GameOverCallback() {
            @Override
            public void onGameOver(int score, int difficulty) {
                handleGameOver(frame, score, difficulty);
            }
        });
        
        frame.add(game);
        frame.revalidate();
        frame.repaint();
        
        // 启动游戏
        game.action();
    }
    
    /**
     * 处理游戏结束
     */
    private static void handleGameOver(JFrame frame, int score, int difficulty) {
        // 显示用户名输入对话框
        UserNameInputDialog nameDialog = new UserNameInputDialog(frame);
        String userName = nameDialog.showDialog();
        
        // 如果用户输入了用户名，添加到排行榜
        if (userName != null && !userName.trim().isEmpty()) {
            edu.hitsz.ranklist.RankListDao rankListDao = new edu.hitsz.ranklist.RankListDaoImpl();
            rankListDao.addRecord(userName, score, java.time.LocalDateTime.now());
            rankListDao.saveRankList();
        }
        
        // 显示游戏结束界面（排行榜）
        showGameOverScreen(frame, difficulty);
    }
    
    /**
     * 显示游戏结束界面
     */
    private static void showGameOverScreen(JFrame frame, int difficulty) {
        // 清除当前界面
        frame.getContentPane().removeAll();
        
        // 获取难度文本
        String difficultyText = getDifficultyText(difficulty);
        
        // 创建游戏结束界面
        GameOverPanel gameOverPanel = new GameOverPanel(difficultyText);
        gameOverPanel.setGameOverListener(new GameOverPanel.GameOverListener() {
            @Override
            public void onRestart() {
                showStartScreen(frame);
            }
            
            @Override
            public void onBackToMenu() {
                showStartScreen(frame);
            }
        });
        
        frame.add(gameOverPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    /**
     * 获取难度文本
     */
    private static String getDifficultyText(int difficulty) {
        switch (difficulty) {
            case 1: return "简单";
            case 2: return "普通";
            case 3: return "困难";
            default: return "普通";
        }
    }
}
