package edu.hitsz.supply;

public class BulletSupply extends BaseSupply{
    public BulletSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
    }
    public void active() {
        System.out.println("BulletSupply Active!");
    }
}
