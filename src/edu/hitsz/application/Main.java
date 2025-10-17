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
        StartGamePanel startPanel = new StartGamePanel();
        startPanel.setGameStartListener(new StartGamePanel.GameStartListener() {
            @Override
            public void onGameStart(int difficulty, boolean musicEnabled) {
                // 清除开始界面
                frame.getContentPane().removeAll();
                
                // 创建游戏界面
                Game game = new Game(difficulty);
                frame.add(game);
                frame.revalidate();
                frame.repaint();
                
                // 启动游戏
                game.action();
            }
        });
        
        frame.add(startPanel);
        frame.setVisible(true);
    }
}
