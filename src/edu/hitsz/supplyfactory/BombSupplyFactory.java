package edu.hitsz.supplyfactory;

import edu.hitsz.supply.BombSupply;
import edu.hitsz.supply.Supply;

public class BombSupplyFactory implements SupplyFactory{

    @Override
    public Supply createNewSupply(int LocationX, int LocationY, int SpeedX, int SpeedY) {
        return new BombSupply(LocationX,LocationY,SpeedX,SpeedY);
    }
}
