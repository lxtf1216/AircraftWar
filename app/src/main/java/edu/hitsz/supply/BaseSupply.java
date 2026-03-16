package edu.hitsz.supply;

import edu.hitsz.application.AppSettings;
import edu.hitsz.basic.AbstractFlyingObject;

public abstract  class BaseSupply extends AbstractFlyingObject {
    protected int kind = 0;
    public BaseSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
    }

    @Override
    public void forward() {
        super.forward();
        if(locationY >= AppSettings.WINDOW_HEIGHT) {
            vanish();
        }
    }

    public abstract int active() ;

    public int getKind() {
        return kind;
    }
}
