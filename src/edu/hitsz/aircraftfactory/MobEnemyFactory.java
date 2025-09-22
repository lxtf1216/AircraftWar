package edu.hitsz.aircraftfactory;

import edu.hitsz.aircraft.EnemyAircraft;
import edu.hitsz.aircraft.MobEnemy;

public class MobEnemyFactory implements AircraftFactory{
    @Override
    public EnemyAircraft createNewEnemyAircraft(int LocationX, int LocationY, int SpeedX, int SpeedY, int hp) {
        return new MobEnemy(LocationX,LocationY,SpeedX,SpeedY,hp);
    }
}
