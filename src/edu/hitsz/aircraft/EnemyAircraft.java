package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public interface EnemyAircraft {
    public void forward() ;
    public List<BaseBullet> shoot() ;
    public void decreaseHp(int decrease) ;
    public int getHp();
}
