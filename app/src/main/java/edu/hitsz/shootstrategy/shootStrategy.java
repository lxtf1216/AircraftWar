package edu.hitsz.shootstrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

public interface shootStrategy {
    public List<BaseBullet>shoot(AbstractAircraft aircraft) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
