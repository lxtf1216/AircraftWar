package edu.hitsz.supplyfactory;

import edu.hitsz.supply.HpSupply;
import edu.hitsz.supply.Supply;

public class HpSupplyFactory implements SupplyFactory{

    @Override
    public Supply createNewSupply(int LocationX, int LocationY, int SpeedX, int SpeedY) {
        return new HpSupply(LocationX,LocationY,SpeedX,SpeedY);
    }
}
