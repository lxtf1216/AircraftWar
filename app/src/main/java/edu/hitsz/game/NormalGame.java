package edu.hitsz.game;

import edu.hitsz.application.GameView;

public class NormalGame extends GameTemplate {
    private int upgradeCount = 0;
    private double eliteProb = 0.3;
    private int enemyCycle = 600;
    private double multiplier = 1.0;

    @Override
    protected boolean shouldUpgrade(int time, int currentScore) {
        return time / 10000 > upgradeCount;
    }

    @Override
    protected void upgradeFactors() {
        upgradeCount++;
        eliteProb = Math.min(0.5, eliteProb + 0.02);
        enemyCycle = Math.max(400, enemyCycle - 20);
        multiplier = Math.min(2.0, multiplier + 0.08);
    }

    @Override
    protected void applyCurrentParameters(GameView game, int currentScore) {
        game.setEnemyMaxNumber(getEnemyMaxNumber() + upgradeCount);
        game.setEnemyCycle(enemyCycle);
        game.setHeroShootCycle(getHeroShootCycle() - upgradeCount * 3);
        game.setEnemyShootCycle(getEnemyShootCycle() - upgradeCount * 2);
        game.setEliteProb(eliteProb);
        game.setAttributeMultiplier(multiplier);
    }

    @Override
    protected double getCurEliteProb() {
        return eliteProb;
    }
    @Override
    protected int getCurEnemyCycle() {
        return enemyCycle;
    }
    @Override
    protected double getCurAttrMultiplier() {
        return multiplier;
    }
    @Override
    public boolean hasBoss() { return true; }
    @Override
    public double getBossHpMultiplier(int bossCount) {
        return 1.0;
    }
}