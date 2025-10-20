package edu.hitsz.supply;

import edu.hitsz.aircraft.CanBeBlownUp;

import java.util.List;

public class BombSupply extends BaseSupply implements Supply{
    List<CanBeBlownUp> enemies;
    public BombSupply(int locationX, int locationY,int speedX,int speedY) {

        super(locationX,locationY,speedX,speedY);
        kind = 1;
    }
    public int active() {
        int res = enemies.size()*10;
        for(CanBeBlownUp enemy:enemies) {
            enemy.beblownup();
        }
        return res;
    }

    public void add(CanBeBlownUp enemy) {
        enemies.add(enemy);
    }

}
