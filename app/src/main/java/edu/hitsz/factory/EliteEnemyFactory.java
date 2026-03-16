package edu.hitsz.factory;

import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.EnemyAircraft;

public class EliteEnemyFactory implements AircraftFactory{

    @Override
    public EnemyAircraft createNewEnemyAircraft(int LocationX, int LocationY, int SpeedX, int SpeedY, int hp) {
        return new EliteEnemy(LocationX,LocationY,SpeedX,SpeedY,hp);
    }
}
