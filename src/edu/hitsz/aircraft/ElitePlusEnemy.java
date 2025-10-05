package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class ElitePlusEnemy extends AbstractAircraft implements EnemyAircraft{
    private int shootNum = 1;
    private int power = 5;
    private int direction = 1;
    public ElitePlusEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY();
        int speedY = this.getSpeedY() + direction*2;
        BaseBullet bullet1,bullet2,bullet3;
        for(int i=0;i<shootNum;++i) {
            bullet1 = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, direction*2, speedY, power);
            res.add(bullet1);
            bullet2 = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, 0, speedY, power);
            res.add(bullet2);
            bullet3 = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, direction*(-2), speedY, power);
            res.add(bullet3);
        }
        return res;
    }

    @Override
    public void forward() {
        super.forward();
        if(locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }
}
