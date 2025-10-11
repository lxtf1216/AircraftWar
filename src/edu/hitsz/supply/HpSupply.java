package edu.hitsz.supply;

import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

public class HpSupply extends BaseSupply implements Supply{
    public HpSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
        kind = 0;
    }

    public int active() {
        return 10;
    }
}
