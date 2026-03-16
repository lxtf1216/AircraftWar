package edu.hitsz.aircraft;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import edu.hitsz.application.AppSettings;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.shootstrategy.shootScatter;
import edu.hitsz.shootstrategy.shootStrategy;

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
        if(locationY >= AppSettings.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    public void beblownup() {
        decreaseHp(maxHp);
    }
}
