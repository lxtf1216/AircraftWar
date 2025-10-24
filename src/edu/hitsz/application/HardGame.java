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

public class HardGame extends AbstractGame {

    private double eliteProbability = 0.4; // 精英敌机初始出现概率
    private int bossSummonCount = 0; // Boss召唤次数
    private int initialBossHp = 1000; // Boss初始血量
    private int enemyHpIncrease = 0;
    private int scoreThreshold = 150;

    public HardGame() {
        super(3);
    }

    @Override
    protected void setInitialParameters() {
        this.enemyMaxNumber = 6;
        this.cycleDuration = 1000;
    }

    @Override
    protected void spawnNewEnemies() {
        if (enemyAircrafts.size() < enemyMaxNumber) {
            Random random = new Random();
            AircraftFactory enemyAircraftFactory;

            double rand = random.nextDouble();
            if (rand < eliteProbability) {
                // 产生精英敌机
                enemyAircraftFactory = new EliteEnemyFactory();
            } else if (rand < eliteProbability + 0.2) {
                // 产生精英+敌机
                enemyAircraftFactory = new ElitePlusEnemyFactory();
            }
            else {
                // 产生普通敌机
                enemyAircraftFactory = new MobEnemyFactory();
            }

            EnemyAircraft enemyAircraft = enemyAircraftFactory.createNewEnemyAircraft(
                    (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                    0,
                    10,
                    30 + (enemyAircraftFactory instanceof EliteEnemyFactory ? 15 : 0) + (enemyAircraftFactory instanceof ElitePlusEnemyFactory ? 30 : 0) + enemyHpIncrease
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
/
    @Override
    protected void spawnBoss() {
        if (bossscore >= 100 && !bossPresent) {
            bossscore -= 100;
            bossSummonCount++;
            int currentBossHp = initialBossHp + (bossSummonCount - 1) * 200; // 每次召唤增加200血量

            AbstractAircraft boss = new BossEnemy(
                    (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.BOSS_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                    5,
                    0,
                    currentBossHp
            );
            enemyAircrafts.add(boss);
            bossPresent = true;
            soundManager.playBossBGM();
            System.out.println("Boss summoned! HP: " + currentBossHp);
        }
    }

    @Override
    protected void updateDifficulty() {
        if (score >= scoreThreshold) {
            scoreThreshold += 150;
            enemyMaxNumber = Math.min(12, enemyMaxNumber + 1); // 敌机数量增加
            eliteProbability = Math.min(0.7, eliteProbability + 0.05); // 精英机概率增加
            cycleDuration = Math.max(300, cycleDuration - 15); // 敌机和子弹产生频率加快
            enemyHpIncrease += 10; // 敌机血量增加
            System.out.println("Difficulty Increased: enemyMaxNumber=" + enemyMaxNumber + ", eliteProbability=" + eliteProbability + ", cycleDuration=" + cycleDuration + ", enemyHpIncrease=" + enemyHpIncrease);
        }
    }
}
