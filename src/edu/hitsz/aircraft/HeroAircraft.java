package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.shootstrategy.shootStraight;
import edu.hitsz.shootstrategy.shootScatter;
import edu.hitsz.shootstrategy.shootCircle;
import edu.hitsz.shootstrategy.shootStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.min;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.ObjectUtils.max;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

    /**攻击方式 */
    private shootStrategy shootstrategy = new shootStraight();
    
    /**
     * 默认射击策略
     */
    private shootStrategy defaultShootStrategy = new shootStraight();
    
    /**
     * 线程池用于管理火力道具的定时任务
     */
    private ScheduledExecutorService powerUpExecutor = Executors.newSingleThreadScheduledExecutor();
    
    /**
     * 当前火力道具任务
     */
    private ScheduledFuture<?> currentPowerUpTask = null;

    /**
     * 子弹伤害
     */

    /**
     * 子弹射击方向 (向上发射：1，向下发射：-1)
     */

    /**
     * @param locationX 英雄机位置x坐标
     * @param locationY 英雄机位置y坐标
     * @param speedX 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param speedY 英雄机射出的子弹的基准速度（英雄机无特定速度）
     * @param hp    初始生命值
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        direction = -1;
        power = 30;
        bullet = HeroBullet.class;
    }

    private static volatile  HeroAircraft Instance ;

    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if(Instance == null) {
            synchronized (HeroAircraft.class) {
                if(Instance == null) {
                    Instance = new HeroAircraft(locationX,locationY,speedX,speedY,hp);
                }
            }
        }
        return Instance;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    /**
     * 通过射击产生子弹
     * @return 射击出的子弹List
     */
    public List<BaseBullet> shoot() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return shootstrategy.shoot(this);
    }
    public void addHp(int dhp) {
        hp = min(maxHp,hp+dhp);
    }
    public void changeShootStrategy(shootStrategy newShootStrategy) {
        shootstrategy = newShootStrategy;
    }

    public void activatePowerUp(shootStrategy powerUpStrategy) {
        // 如果已有火力道具在运行，取消之前的任务
        if (currentPowerUpTask != null && !currentPowerUpTask.isDone()) {
            currentPowerUpTask.cancel(false);
        }
        
        // 立即切换到新的射击策略
        changeShootStrategy(powerUpStrategy);
        
        // 创建新的定时任务，5秒后恢复默认射击策略
        PowerUpTask powerUpTask = new PowerUpTask();
        currentPowerUpTask = powerUpExecutor.schedule(powerUpTask, 5, TimeUnit.SECONDS);
        
        System.out.println("火力道具激活，持续5秒");
    }
    

    private class PowerUpTask implements Runnable {
        @Override
        public void run() {
            // 恢复默认射击策略
            changeShootStrategy(defaultShootStrategy);
            System.out.println("火力道具效果结束，恢复默认射击");
        }
    }
    
    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (powerUpExecutor != null && !powerUpExecutor.isShutdown()) {
            powerUpExecutor.shutdown();
        }
    }
}
