package edu.hitsz.factory;

import edu.hitsz.supply.BulletSupply;
import edu.hitsz.supply.Supply;

public class BulletSupplyFactory implements SupplyFactory{

    @Override
    public Supply createNewSupply(int LocationX, int LocationY, int SpeedX, int SpeedY) {
        return new BulletSupply(LocationX,LocationY,SpeedX,SpeedY);
    }
}
