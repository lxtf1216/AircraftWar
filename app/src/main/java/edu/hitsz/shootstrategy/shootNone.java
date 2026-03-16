package edu.hitsz.shootstrategy;

import java.util.LinkedList;
import java.util.List;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

public class shootNone implements shootStrategy{


    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        return new LinkedList<>();
    }
}
