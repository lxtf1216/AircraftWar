package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface EnemyAircraft {
    public void forward() ;
    public List<BaseBullet> shoot() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
    public void decreaseHp(int decrease) ;
    public int getHp();
}
