package edu.hitsz.shootstrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

public class shootStraight implements shootStrategy{
    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + aircraft.getDirection()*2;
        int speedX = 0;
        int speedY = aircraft.getSpeedY() + aircraft.getDirection()*5;
        for(int i=0; i<1; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            Constructor<?> constructor = aircraft.getBullet().getDeclaredConstructor(int.class,int.class,int.class,int.class,int.class);
            BaseBullet bullet = (BaseBullet) constructor.newInstance(x,y,speedX,speedY,aircraft.getPower());
            res.add(bullet);
        }
        return res;
    }
}
