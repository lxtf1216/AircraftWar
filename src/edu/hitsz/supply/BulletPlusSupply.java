package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.shootstrategy.shootCircle;

public class BulletPlusSupply extends BaseSupply implements Supply{
    public BulletPlusSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        kind = 3;
    }

    @Override
    public int active() {
        System.out.println("超级火力道具激活！改为环射模式，持续5秒");
        return 1;
    }
    
    /**
     * 激活火力道具效果
     * @param heroAircraft 英雄飞机实例
     */
    public void activateEffect(HeroAircraft heroAircraft) {
        heroAircraft.activatePowerUp(new shootCircle());
    }
}
