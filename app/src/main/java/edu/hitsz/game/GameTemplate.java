package edu.hitsz.game;

import edu.hitsz.application.GameView;


/**
 * 游戏难度模板：定义算法骨架，子类重写具体步骤
 */
public abstract class GameTemplate {
    /* 改变难度 */
    public final void updateDifficulty(GameView game, int currentScore, int time) {
        boolean needUpdate = shouldUpgrade(time, currentScore);

        if (needUpdate) {
            upgradeFactors();
            printUpgradeInfo();
        }
        applyCurrentParameters(game, currentScore);
    }

    /* 接口：由具体游戏难度实现 */
    protected abstract boolean shouldUpgrade(int time, int currentScore);
    protected abstract void upgradeFactors();
    protected abstract void applyCurrentParameters(GameView game, int currentScore);

    protected void printUpgradeInfo() {
        double eliteProb = getCurEliteProb();
        int enemyCycle = getCurEnemyCycle();
        double multiplier = getCurAttrMultiplier();

        System.out.printf(
                "难度升级 -> 精英机概率: %.2f | 敌机生成周期: %d | 属性倍率: %.2f%n",
                eliteProb, enemyCycle, multiplier
        );
    }

    protected abstract double getCurEliteProb();
    protected abstract int getCurEnemyCycle();
    protected abstract double getCurAttrMultiplier();

    public int getEnemyMaxNumber() { return 5; }
    public int getEnemyCycle() { return 600; }
    public int getHeroShootCycle() { return 30; }
    public int getEnemyShootCycle() { return 120; }
    public double getEliteProb() { return 0.3; }
    public double getAttrMultiplier() { return 1.0; }

    /* Boss */
    public boolean hasBoss() { return false; }
    public boolean bossHpIncreases() { return false; }
    public double getBossHpMultiplier(int bossCount) {
        if (bossHpIncreases()) {
            return 1.0 + bossCount * 0.2;
        }
        return 1.0;
    }
}