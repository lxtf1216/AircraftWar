package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.CanBeBlownUp;
import edu.hitsz.aircraft.EnemyAircraft;
import edu.hitsz.aircraftfactory.AircraftFactory;
import edu.hitsz.aircraftfactory.EliteEnemyFactory;
import edu.hitsz.aircraftfactory.MobEnemyFactory;
import edu.hitsz.supply.BaseSupply;
import edu.hitsz.supply.BombSupply;

import java.util.Random;

public class EasyGame extends AbstractGame {

    public EasyGame() {
        super(1);
    }

    @Override
    protected void setInitialParameters() {
        this.enemyMaxNumber = 5;
        this.cycleDuration = 600;
    }

    @Override
    protected void spawnNewEnemies() {
        if (enemyAircrafts.size() < enemyMaxNumber) {
            Random random = new Random();
            // 简单模式下只产生普通敌机和精英敌机
            int choice = random.nextInt(5); // 0-2 for Mob, 3-4 for Elite
            AircraftFactory enemyAircraftFactory;
            if (choice < 3) {
                enemyAircraftFactory = new MobEnemyFactory();
            } else {
                enemyAircraftFactory = new EliteEnemyFactory();
            }

            EnemyAircraft enemyAircraft = enemyAircraftFactory.createNewEnemyAircraft(
                    (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                    0,
                    10,
                    30 + (choice >= 3 ? 15 : 0) // 精英敌机血量更高
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
        // 简单模式不产生Boss
    }

    @Override
    protected void updateDifficulty() {
        // 简单模式难度不增加
    }
}
