package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.shootstrategy.shootScatter;

public class BulletSupply extends BaseSupply implements Supply{
    public BulletSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
        kind = 2;
    }
    
    public int active() {
        System.out.println("普通火力道具激活！改为散射模式，持续5秒");
        return 1;
    }
    
    /**
     * 激活火力道具效果
     * @param heroAircraft 英雄飞机实例
     */
    public void activateEffect(HeroAircraft heroAircraft) {
        heroAircraft.activatePowerUp(new shootScatter());
    }
}
