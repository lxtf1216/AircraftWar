package edu.hitsz.shootstrategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public interface shootStrategy {
    public List<BaseBullet>shoot(AbstractAircraft aircraft) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
