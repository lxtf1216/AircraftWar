package edu.hitsz.supply;

import edu.hitsz.aircraft.HeroAircraft;

public class HpSupply extends BaseSupply implements Supply{
    public HpSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
        kind = 0;
    }

    public int active() {
        HeroAircraft heroAircraft = HeroAircraft.getInstance();
        heroAircraft.increaseHp(30);
        return 1;
    }
}
