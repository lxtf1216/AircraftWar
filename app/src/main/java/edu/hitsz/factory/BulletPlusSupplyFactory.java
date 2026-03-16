package edu.hitsz.factory;

import edu.hitsz.supply.BulletPlusSupply;
import edu.hitsz.supply.Supply;

public class BulletPlusSupplyFactory implements SupplyFactory{

    @Override
    public Supply createNewSupply(int LocationX, int LocationY, int SpeedX, int SpeedY) {
        return new BulletPlusSupply(LocationX,LocationY,SpeedX,SpeedY);
    }
}
