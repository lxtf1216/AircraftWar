package edu.hitsz.supply;

import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

public class BaseSupply extends AbstractFlyingObject {
    public BaseSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
    }

    @Override
    public void forward() {
        super.forward();
        if(locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

}
