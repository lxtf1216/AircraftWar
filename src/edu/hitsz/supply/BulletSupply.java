package edu.hitsz.supply;

public class BulletSupply extends BaseSupply implements Supply{
    public BulletSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
        kind = 2;
    }
    public int active() {

        System.out.println("FireSupply active!");
        return 1;
    }
}
