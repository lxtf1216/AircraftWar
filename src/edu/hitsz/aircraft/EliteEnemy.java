package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.shootstrategy.shootStraight;
import edu.hitsz.shootstrategy.shootStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 * 可射击
 *
 * @author ljc
 */
public class EliteEnemy extends AbstractAircraft implements EnemyAircraft{
    final private shootStrategy shootstrategy = new shootStraight();
    public EliteEnemy(int locationX, int locationY,int speedX,int speedY,int hp) {
        super(locationX,locationY,speedX,speedY,hp);
        direction = 1;
        power = 5;
        bullet = EnemyBullet.class;
    }
    
    @Override
    public void forward() {
        super.forward();
        if(locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }
    
    @Override
    public List<BaseBullet> shoot() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return shootstrategy.shoot(this);
    }
}
