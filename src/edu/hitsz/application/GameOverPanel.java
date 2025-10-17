package edu.hitsz.application;

import edu.hitsz.ranklist.RankList;
import edu.hitsz.ranklist.UserGameRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 游戏结束界面
 * 显示排行榜，支持删除记录功能
 */
public class GameOverPanel extends JPanel {
    
    private JLabel difficultyLabel;
    private JList<String> rankList;
    private DefaultListModel<String> listModel;
    private JScrollPane scrollPane;
    private JButton deleteButton;
    private JButton restartButton;
    private JButton backToMenuButton;
    
    private List<UserGameRecord> gameRecords;
    private String currentDifficulty;
    
    // 回调接口
    public interface GameOverListener {
        void onRestart();
        void onBackToMenu();
    }
    
    private GameOverListener gameOverListener;
    
    public GameOverPanel(String difficulty) {
        this.currentDifficulty = difficulty;
        initComponents();
        setupLayout();
        setupEventListeners();
        loadRankListData();
    }
    
    private void initComponents() {
        // 难度显示标签
        difficultyLabel = new JLabel("游戏难度：" + currentDifficulty);
        difficultyLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        difficultyLabel.setForeground(Color.WHITE);
        
        // 排行榜列表
        listModel = new DefaultListModel<>();
        rankList = new JList<>(listModel);
        rankList.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        rankList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankList.setBackground(new Color(240, 248, 255));
        rankList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 滚动面板
        scrollPane = new JScrollPane(rankList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            "排行榜",
            0, 0,
            new Font("微软雅黑", Font.BOLD, 18),
            Color.WHITE
        ));
        
        // 删除按钮
        deleteButton = new JButton("删除选中记录");
        setupButton(deleteButton, Color.RED);

        // 重新开始按钮
        restartButton = new JButton("重新开始");
        setupButton(restartButton, new Color(34, 139, 34));

        // 返回主菜单按钮
        backToMenuButton = new JButton("返回主菜单");
        setupButton(backToMenuButton, new Color(70, 130, 180));
    }
    
    private void setupButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 112)); // 深蓝色背景
        
        // 顶部面板 - 难度显示
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(25, 25, 112));
        topPanel.add(difficultyLabel);
        
        // 中间面板 - 排行榜
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(25, 25, 112));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 底部面板 - 按钮
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBackground(new Color(25, 25, 112));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 30, 20));
        
        bottomPanel.add(deleteButton);
        //bottomPanel.add(Box.createHorizontalStrut(20));
       // bottomPanel.add(restartButton);
        //bottomPanel.add(Box.createHorizontalStrut(20));
       // bottomPanel.add(backToMenuButton);
        
        // 添加到主面板
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // 删除按钮事件
        deleteButton.addActionListener(e -> {
            int selectedIndex = rankList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(this,
                    "请先选择要删除的记录！",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // 确认删除对话框
            int result = JOptionPane.showConfirmDialog(this,
                "确定要删除选中的记录吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (result == JOptionPane.YES_OPTION) {
                deleteRecord(selectedIndex);
            }
        });
        
        // 重新开始按钮事件
//        restartButton.addActionListener(e -> {
//            if (gameOverListener != null) {
//                gameOverListener.onRestart();
//            }
//        });
//
//        // 返回主菜单按钮事件
//        backToMenuButton.addActionListener(e -> {
//            if (gameOverListener != null) {
//                gameOverListener.onBackToMenu();
//            }
//        });
        
        // 列表双击事件（可选择记录）
        rankList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    // 单击选择
                    int index = rankList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        rankList.setSelectedIndex(index);
                    }
                }
            }
        });
    }
    
    private void loadRankListData() {
        try {
            edu.hitsz.ranklist.RankListDao rankListDao = new edu.hitsz.ranklist.RankListDaoImpl();
            gameRecords = rankListDao.getAllRecords();
            updateListDisplay();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "加载排行榜数据失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateListDisplay() {
        listModel.clear();
        
        if (gameRecords.isEmpty()) {
            listModel.addElement("暂无记录");
            return;
        }
        
        for (int i = 0; i < gameRecords.size(); i++) {
            UserGameRecord record = gameRecords.get(i);
            String timeStr = record.getTime().toString().substring(5, 10) + " " +
                    record.getTime().toString().substring(11, 16);
            String displayText = String.format("第%d名：%s，%d分，%s",
                i + 1, record.getName(), record.getScore(), timeStr);
            listModel.addElement(displayText);
        }
    }
    
    private void deleteRecord(int index) {
        try {
            if (index >= 0 && index < gameRecords.size()) {
                // 使用DAO接口删除记录
                edu.hitsz.ranklist.RankListDao rankListDao = new edu.hitsz.ranklist.RankListDaoImpl();
                rankListDao.deleteRecord(index);
                rankListDao.saveRankList();
                
                // 重新加载数据
                loadRankListData();
                
                JOptionPane.showMessageDialog(this,
                    "记录删除成功！",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "删除记录失败：" + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setGameOverListener(GameOverListener listener) {
        this.gameOverListener = listener;
    }
    
    public void refreshRankList() {
        loadRankListData();
    }
    
    public void updateDifficulty(String difficulty) {
        this.currentDifficulty = difficulty;
        difficultyLabel.setText("游戏难度：" + difficulty);
    }
}
