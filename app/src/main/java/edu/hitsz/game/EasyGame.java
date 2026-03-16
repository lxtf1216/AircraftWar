package edu.hitsz.game;

import edu.hitsz.application.GameView;

public class EasyGame extends GameTemplate {
    @Override
    protected boolean shouldUpgrade(int time, int currentScore) {
        return false;
    }

    @Override
    protected void upgradeFactors() {
    }

    @Override
    protected void applyCurrentParameters(GameView game, int currentScore) {
        game.setEnemyMaxNumber(getEnemyMaxNumber());
        game.setEnemyCycle(getEnemyCycle());
        game.setHeroShootCycle(getHeroShootCycle());
        game.setEnemyShootCycle(getEnemyShootCycle());
        game.setEliteProb(getEliteProb());
        game.setAttributeMultiplier(getAttrMultiplier());
    }

    @Override
    protected double getCurEliteProb() {
        return getEliteProb();
    }
    @Override
    protected int getCurEnemyCycle() {
        return getEnemyCycle();
    }
    @Override
    protected double getCurAttrMultiplier() {
        return getAttrMultiplier();
    }
    @Override
    public boolean hasBoss() { return false; }
}