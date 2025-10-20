package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.shootstrategy.shootScatter;
import edu.hitsz.shootstrategy.shootStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class ElitePlusEnemy extends AbstractAircraft implements EnemyAircraft,CanBeBlownUp{
    final private shootStrategy shootstrategy = new shootScatter();
    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        direction = 1;
        power = 5;
        bullet = EnemyBullet.class;
    }

    @Override
    public List<BaseBullet> shoot() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return shootstrategy.shoot(this);
    }

    @Override
    public void forward() {
        super.forward();
        if(locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public void beblownup() {
        decreaseHp(maxHp);
    }
}
