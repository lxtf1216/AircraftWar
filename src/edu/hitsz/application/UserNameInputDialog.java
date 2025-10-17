package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 用户名输入对话框
 * 游戏结束时弹出，要求用户输入用户名
 */
public class UserNameInputDialog extends JDialog {
    
    private JTextField nameTextField;
    private JButton confirmButton;
    private JButton cancelButton;
    private String userName = null;
    private boolean confirmed = false;
    
    public UserNameInputDialog(Frame parent) {
        super(parent, "输入用户名", true);
        initComponents();
        setupLayout();
        setupEventListeners();
        setupDialog();
    }
    
    private void initComponents() {
        // 用户名输入框
        nameTextField = new JTextField(20);
        nameTextField.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        nameTextField.setPreferredSize(new Dimension(200, 30));
        
        // 确认按钮
        confirmButton = new JButton("确认");
        confirmButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        confirmButton.setPreferredSize(new Dimension(80, 35));
        confirmButton.setBackground(new Color(34, 139, 34));
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusPainted(false);
        
        // 取消按钮
        cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.setBackground(new Color(220, 20, 60));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));
        
        // 标题面板
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 248, 255));
        JLabel titleLabel = new JLabel("游戏结束！请输入您的用户名：");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(25, 25, 112));
        titlePanel.add(titleLabel);
        
        // 输入面板
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBackground(new Color(240, 248, 255));
        JLabel nameLabel = new JLabel("用户名：");
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        inputPanel.add(nameLabel);
        inputPanel.add(nameTextField);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 248, 255));
        buttonPanel.add(confirmButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(cancelButton);
        
        // 添加到主面板
        add(titlePanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 添加边距
        //setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    }
    
    private void setupEventListeners() {
        // 确认按钮事件
        confirmButton.addActionListener(e -> {
            String name = nameTextField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "用户名不能为空！", 
                    "输入错误", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.length() > 20) {
                JOptionPane.showMessageDialog(this, 
                    "用户名长度不能超过20个字符！", 
                    "输入错误", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            userName = name;
            confirmed = true;
            dispose();
        });
        
        // 取消按钮事件
        cancelButton.addActionListener(e -> {
            userName = "匿名用户";
            confirmed = false;
            dispose();
        });
        
        // 回车键确认
        nameTextField.addActionListener(e -> confirmButton.doClick());
        
        // ESC键取消
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelButton.doClick();
            }
        });
    }
    
    private void setupDialog() {
        setSize(400, 200);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // 居中显示
        setLocationRelativeTo(getParent());
        
        // 设置焦点到输入框
        SwingUtilities.invokeLater(() -> nameTextField.requestFocus());
    }
    
    /**
     * 显示对话框并获取用户输入的用户名
     * @return 用户输入的用户名，如果取消则返回"匿名用户"
     */
    public String showDialog() {
        setVisible(true);
        return userName;
    }
    
    /**
     * 检查用户是否确认输入
     * @return true如果用户点击确认，false如果点击取消
     */
    public boolean isConfirmed() {
        return confirmed;
    }
}
