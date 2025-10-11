package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.shootstrategy.shootCircle;
import edu.hitsz.shootstrategy.shootStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends AbstractAircraft implements EnemyAircraft{
    private int shootcount = 3;
    final private shootStrategy shootstrategy = new shootCircle();
    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        direction = 1;
        power = 5;
        bullet = EnemyBullet.class;
    }
    public List<BaseBullet> shoot() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        shootcount ++ ;
        if(shootcount < 5) {
            return new LinkedList<>();
        }
        shootcount -= 5;
        return shootstrategy.shoot(this);
    }
}
