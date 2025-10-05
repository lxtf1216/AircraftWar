package edu.hitsz.supply;

public class BombSupply extends BaseSupply implements Supply{
    public BombSupply(int locationX, int locationY,int speedX,int speedY) {

        super(locationX,locationY,speedX,speedY);
        kind = 1;
    }
    public int active() {

        System.out.println("BombSupply active!");
        return 1;
    }
}
