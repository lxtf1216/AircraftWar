package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;

import java.util.LinkedList;
import java.util.List;

public class BossEnemy extends AbstractAircraft implements EnemyAircraft{
    private int shootcount = 5;
    private int direction = 1;
    private int power = 5;
    private int radius = 80;
    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        shootcount ++ ;
        if(shootcount < 5) {
            return res;
        }
        shootcount -= 5;
        int x = this.getLocationX();
        int y = this.getLocationY();

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
            int speedY = direction*10; // 向下飞行，Y 速度为负

            // 创建子弹
            BaseBullet bullet = new EnemyBullet(bulletX, bulletY, speedX, speedY, power);
            res.add(bullet);
        }

        return res;
    }
}
