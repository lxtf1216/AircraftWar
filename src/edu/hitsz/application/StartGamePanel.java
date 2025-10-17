package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 游戏开始界面
 * 包含难度选择、音乐开关设置
 */
public class StartGamePanel extends JPanel {
    
    private JButton easyButton;
    private JButton normalButton;
    private JButton hardButton;
    private JComboBox<String> musicComboBox;
    private JLabel titleLabel;
    private JLabel difficultyLabel;
    private JLabel musicLabel;
    
    // 游戏设置
    private int selectedDifficulty = 2; // 1-简单, 2-普通, 3-困难
    private boolean musicEnabled = true;
    
    // 回调接口
    public interface GameStartListener {
        void onGameStart(int difficulty, boolean musicEnabled);
    }
    
    private GameStartListener gameStartListener;
    
    public StartGamePanel() {
        initComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initComponents() {
        // 标题
        titleLabel = new JLabel("飞机大战", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        titleLabel.setForeground(Color.YELLOW);
        
        // 难度选择标签
        difficultyLabel = new JLabel("选择难度:", SwingConstants.CENTER);
        difficultyLabel.setFont(new Font("微软雅黑", Font.BOLD, 40));
        difficultyLabel.setForeground(Color.BLACK);
        
        // 难度按钮
        easyButton = new JButton("简单");
        normalButton = new JButton("普通");
        hardButton = new JButton("困难");
        
        // 设置按钮样式
        setupButton(easyButton,Color.GREEN,30);
        setupButton(normalButton,Color.YELLOW,30);
        setupButton(hardButton,Color.RED,30);
        
        // 默认选中普通难度
        normalButton.setBackground(new Color(100, 149, 237));
        
        // 音乐设置标签
        musicLabel = new JLabel("背景音乐:", SwingConstants.CENTER);
        musicLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        musicLabel.setForeground(Color.WHITE);
        
        // 音乐开关下拉框
        String[] musicOptions = {"开启", "关闭"};
        musicComboBox = new JComboBox<>(musicOptions);
        musicComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        musicComboBox.setPreferredSize(new Dimension(120, 40));
    }
    
    private void setupButton(JButton button,Color color,int size) {
        button.setFont(new Font("微软雅黑", Font.BOLD, size));
        button.setPreferredSize(new Dimension(120, 50));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(color);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        setBackground(new Color(25, 25, 112)); // 深蓝色背景
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // 标题
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        add(titleLabel, gbc);
        
        // 难度选择标签
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        add(difficultyLabel, gbc);
        
        // 难度按钮
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(easyButton, gbc);
        
        gbc.gridx = 1;
        add(normalButton, gbc);
        
        gbc.gridx = 2;
        add(hardButton, gbc);
        
        // 音乐设置标签
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        add(musicLabel, gbc);
        
        // 音乐下拉框
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.gridx = 1;
        add(musicComboBox, gbc);
        
        // 开始游戏按钮
        JButton startButton = new JButton("开始游戏");
        setupButton(startButton,Color.RED,50);
        startButton.setBackground(new Color(34, 139, 34));
        startButton.setPreferredSize(new Dimension(150, 60));
        
        gbc.gridy = 5;
        gbc.gridx = 1;
        gbc.insets = new Insets(40, 20, 20, 20);
        add(startButton, gbc);
        
        // 开始游戏按钮事件
        startButton.addActionListener(e -> {
            if (gameStartListener != null) {
                gameStartListener.onGameStart(selectedDifficulty, musicEnabled);
            }
        });
    }
    
    private void setupEventListeners() {
        // 难度按钮事件
        easyButton.addActionListener(e -> selectDifficulty(1, easyButton));
        normalButton.addActionListener(e -> selectDifficulty(2, normalButton));
        hardButton.addActionListener(e -> selectDifficulty(3, hardButton));
        
        // 音乐设置事件
        musicComboBox.addActionListener(e -> {
            musicEnabled = musicComboBox.getSelectedIndex() == 0;
        });
    }
    
    private void selectDifficulty(int difficulty, JButton selectedButton) {
        // 重置所有按钮颜色
        easyButton.setBackground(new Color(70, 130, 180));
        normalButton.setBackground(new Color(70, 130, 180));
        hardButton.setBackground(new Color(70, 130, 180));
        
        // 高亮选中的按钮
        selectedButton.setBackground(new Color(100, 149, 237));
        selectedDifficulty = difficulty;
    }
    
    public void setGameStartListener(GameStartListener listener) {
        this.gameStartListener = listener;
    }
    
    public int getSelectedDifficulty() {
        return selectedDifficulty;
    }
    
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
}
