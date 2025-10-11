package edu.hitsz.supply;

public class BulletPlusSupply extends BaseSupply implements Supply{
    public BulletPlusSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        kind = 3;
    }

    @Override
    public int active() {
        System.out.println("BulletPlusSupply active!!!");
        return 1;
    }
}
