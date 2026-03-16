package edu.hitsz.aircraft;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import edu.hitsz.bullet.BaseBullet;

public interface EnemyAircraft {
    public void forward() ;
    public List<BaseBullet> shoot() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException;
    public void decreaseHp(int decrease) ;
    public int getHp();
}
