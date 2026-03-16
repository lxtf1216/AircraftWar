package edu.hitsz.factory;

import edu.hitsz.aircraft.EnemyAircraft;

public interface AircraftFactory {
    public EnemyAircraft createNewEnemyAircraft(int LocationX,int LocationY,int SpeedX,int SpeedY,int hp) ;
}
