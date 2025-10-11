package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.shootstrategy.shootStraight;
import edu.hitsz.shootstrategy.shootStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.min;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.max;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    /**攻击方式 */
    private shootStrategy shootstrategy = new shootStraight();

    /**
     * 子弹伤害
     */

    /**
     * 子弹射击方向 (向上发射：1，向下发射：-1)
     */

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        direction = -1;
        power = 30;
        bullet = HeroBullet.class;
    }

    private static volatile  HeroAircraft Instance ;

    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if(Instance == null) {
            synchronized (HeroAircraft.class) {
                if(Instance == null) {
                    Instance = new HeroAircraft(locationX,locationY,speedX,speedY,hp);
                }
            }
        }
        return Instance;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return shootstrategy.shoot(this);
    }
    public void addHp(int dhp) {
        hp = min(maxHp,hp+dhp);
    }
    public void changeShootStrategy(shootStrategy newShootStrategy) {
        shootstrategy = newShootStrategy;
    }
    public List<BaseBullet> shootCircle() {
        int radius = 80;
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY();

        int bulletCount = 10;

        for (int i = 0; i < bulletCount; i++) {
            double angle = 2 * Math.PI * i / bulletCount;

            int offsetX = (int) (Math.cos(angle) * radius);
            int offsetY = (int) (Math.sin(angle) * radius);

            int bulletX = x + offsetX;
            int bulletY = y + offsetY;

            int speedX = 0;
            int speedY = direction*10;

            BaseBullet bullet = new HeroBullet(bulletX, bulletY, speedX, speedY, power);
            res.add(bullet);
        }

        return res;
    }
}
