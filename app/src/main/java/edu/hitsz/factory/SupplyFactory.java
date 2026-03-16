package edu.hitsz.factory;

import edu.hitsz.supply.Supply;

public interface SupplyFactory {
    public Supply createNewSupply(int LocationX,int LocationY,int SpeedX,int SpeedY);
}
