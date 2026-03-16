package edu.hitsz.factory;

import edu.hitsz.aircraft.ElitePlusEnemy;
import edu.hitsz.aircraft.EnemyAircraft;

public class ElitePlusEnemyFactory implements AircraftFactory{
    @Override
    public EnemyAircraft createNewEnemyAircraft(int LocationX, int LocationY, int SpeedX, int SpeedY, int hp) {
        return new ElitePlusEnemy(LocationX,LocationY,SpeedX,SpeedY,hp);
    }
}
