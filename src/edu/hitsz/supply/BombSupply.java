package edu.hitsz.supply;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.CanBeBlownUp;
import edu.hitsz.aircraft.EnemyAircraft;
import edu.hitsz.basic.AbstractFlyingObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class BombSupply extends BaseSupply implements Supply{
    private final ConcurrentLinkedQueue<CanBeBlownUp> registeredEnemies = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean activated = new AtomicBoolean(false);
    public BombSupply(int locationX, int locationY,int speedX,int speedY) {
        super(locationX,locationY,speedX,speedY);
        kind = 1;
    }
    public int active() {
        if(!activated.compareAndSet(false,true)) {
            return 0;
        }
        int res = 0 ;
        List<CanBeBlownUp> snapshot = new ArrayList<>();
        CanBeBlownUp e;
        while ((e = registeredEnemies.poll()) != null) {
            snapshot.add(e);
        }

        for (CanBeBlownUp enemy : snapshot) {
            try {
                if (enemy == null) continue;
                // 若敌机已经无效，跳过
                if (((AbstractAircraft)enemy).notValid()) continue;

                // 给敌机一个致命伤（根据你的敌机实现，你可以改为 decreaseHp(remainingHp) 或者直接 vanish）
                // 我这里尝试先调用 decreaseHp 大数值，若没有该方法同样可调用 vanish()
                ((AbstractAircraft)enemy).decreaseHp(45); // 保守的“大伤

                // 如果 enemy 被标记为无效或 HP <= 0，则视为被炸毁
                if (((AbstractAircraft)enemy).notValid()) {
                    res+=10;
                }
            } catch (Exception ex) {
                // 防御性：单个敌机处理异常不应影响其他敌机处理
                ex.printStackTrace();
            }
        }

        // 激活后补给自身应消失
        this.vanish();

        return res;
    }

    public void addenemy(CanBeBlownUp enemy) {
        if(enemy == null ) {
            return;
        } else {
            try {
                if(((AbstractAircraft)enemy).notValid()) return ;
            } catch (Exception e) {
                return;
            }
        }
        registeredEnemies.add(enemy);
    }


}
