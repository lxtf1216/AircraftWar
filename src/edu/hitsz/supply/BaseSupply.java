package edu.hitsz.supply;

import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

public abstract  class BaseSupply extends AbstractFlyingObject {
    protected int kind = 0;
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

    public abstract int active() ;

    public int getKind() {
        return kind;
    }
}
