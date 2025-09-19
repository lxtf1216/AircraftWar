package edu.hitsz.supply;

public class BombSupply extends BaseSupply{
    public BombSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
    }
    public void active() {
        System.out.println("BombSupply Active!");
    }
}
