package edu.hitsz.application;

/**
 * 应用全局设置类，存储屏幕尺寸等运行时参数
 */
public class AppSettings {
    public static int WINDOW_WIDTH;
    public static int WINDOW_HEIGHT;

    /* GameView调用 */
    public static void setScreenSize(int width, int height) {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;
    }
}