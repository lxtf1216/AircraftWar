package edu.hitsz.shootstrategy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

public class shootCircle implements shootStrategy{

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        int radius = 80;
        List<BaseBullet> res = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();

        int bulletCount = 20;

        for (int i = 0; i < bulletCount; i++) {
            // 计算每个子弹的角度（弧度），以确定其起始位置
            double angle = 2 * Math.PI * i / bulletCount;

            // 根据角度计算环上的坐标偏移量
            int offsetX = (int) (Math.cos(angle) * radius); // 圆半径为30像素
            int offsetY = (int) (Math.sin(angle) * radius);

            // 子弹的位置
            int bulletX = x + offsetX;
            int bulletY = y + offsetY;

            // 子弹的速度：所有子弹都沿 Y 轴向下飞，所以 speedX=0, speedY 是固定的负值
            int speedX = 0;
            int speedY = aircraft.getDirection()*10; // 向下飞行，Y 速度为负

            // 创建子弹
            Constructor<?> constructor = aircraft.getBullet().getDeclaredConstructor(int.class,int.class,int.class,int.class,int.class);
            BaseBullet bullet = (BaseBullet) constructor.newInstance(bulletX,bulletY,speedX,speedY,aircraft.getPower());
            res.add(bullet);
        }

        return res;
    }
}
