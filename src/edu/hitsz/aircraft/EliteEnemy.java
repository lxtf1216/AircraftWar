package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 * 可射击
 *
 * @author ljc
 */
public class EliteEnemy extends AbstractAircraft {
    private int shootNum = 1;
    private int power = 10;
    private int direction = 1;
    public EliteEnemy(int locationX, int locationY,int speedX,int speedY,int hp) {
        super(locationX,locationY,speedX,speedY,hp);
    }
    
    @Override
    public void forward() {
        super.forward();
        if(locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }
    
    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction*2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction*5;
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }
}
