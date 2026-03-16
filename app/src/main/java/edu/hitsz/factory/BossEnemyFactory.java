package edu.hitsz.factory;

import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.EnemyAircraft;

public class BossEnemyFactory implements AircraftFactory{

    @Override
    public EnemyAircraft createNewEnemyAircraft(int LocationX, int LocationY, int SpeedX, int SpeedY, int hp) {
        return new BossEnemy(LocationX,LocationY,SpeedX,SpeedY,hp);
    }
}