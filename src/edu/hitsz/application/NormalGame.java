package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.CanBeBlownUp;
import edu.hitsz.aircraft.EnemyAircraft;
import edu.hitsz.aircraftfactory.AircraftFactory;
import edu.hitsz.aircraftfactory.EliteEnemyFactory;
import edu.hitsz.aircraftfactory.ElitePlusEnemyFactory;
import edu.hitsz.aircraftfactory.MobEnemyFactory;
import edu.hitsz.supply.BaseSupply;
import edu.hitsz.supply.BombSupply;

import java.util.Random;

public class NormalGame extends AbstractGame {

    private double eliteProbability = 0.3; // 精英敌机出现概率
    private int enemyHpIncrease = 0;
    private int scoreThreshold = 200;

    public NormalGame() {
        super(2);
    }

    @Override
    protected void setInitialParameters() {
        this.enemyMaxNumber = 5;
        this.cycleDuration = 1000;
    }

    @Override
    protected void spawnNewEnemies() {
        if (enemyAircrafts.size() < enemyMaxNumber) {
            Random random = new Random();
            AircraftFactory enemyAircraftFactory;

            if (random.nextDouble() < eliteProbability) {

                // 产生精英敌机
                enemyAircraftFactory = new ElitePlusEnemyFactory();
            } else if(random.nextDouble() < eliteProbability + 0.2){
                // 产生普通敌机
                enemyAircraftFactory = new EliteEnemyFactory();
            } else {
                enemyAircraftFactory = new MobEnemyFactory();
            }

            EnemyAircraft enemyAircraft = enemyAircraftFactory.createNewEnemyAircraft(
                    (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                    0,
                    10,
                    30 + (enemyAircraftFactory instanceof EliteEnemyFactory ? 15 : 0) + enemyHpIncrease
            );
            enemyAircrafts.add((AbstractAircraft) enemyAircraft);

            if (enemyAircraft instanceof CanBeBlownUp) {
                for (BaseSupply sp : supplies) {
                    if (sp instanceof BombSupply) {
                        ((BombSupply) sp).addenemy((CanBeBlownUp) enemyAircraft);
                    }
                }
            }
        }
    }

    @Override
    protected void spawnBoss() {
        if (bossscore >= 100 && !bossPresent) {
            bossscore -= 100;
            AbstractAircraft boss = new BossEnemy(
                    (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.BOSS_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                    5,
                    0,
                    1000
            );
            enemyAircrafts.add(boss);
            bossPresent = true;
            soundManager.playBossBGM();
        }
    }

    @Override
    protected void updateDifficulty() {
        if (score >= scoreThreshold) {
            scoreThreshold += 200;
            enemyMaxNumber = Math.min(10, enemyMaxNumber + 1); // 敌机数量增加
            eliteProbability = Math.min(0.6, eliteProbability + 0.05); // 精英机概率增加
            cycleDuration = Math.max(400, cycleDuration - 20); // 敌机和子弹产生频率加快
            enemyHpIncrease += 5; // 敌机血量增加
            System.out.println("Difficulty Increased: enemyMaxNumber=" + enemyMaxNumber + ", eliteProbability=" + eliteProbability + ", cycleDuration=" + cycleDuration + ", enemyHpIncrease=" + enemyHpIncrease);
        }
    }
}
