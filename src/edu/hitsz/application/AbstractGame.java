 package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.aircraftfactory.AircraftFactory;
import edu.hitsz.aircraftfactory.EliteEnemyFactory;
import edu.hitsz.aircraftfactory.ElitePlusEnemyFactory;
import edu.hitsz.aircraftfactory.MobEnemyFactory;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.ranklist.RankListDao;
import edu.hitsz.ranklist.RankListDaoImpl;
import edu.hitsz.shootstrategy.shootCircle;
import edu.hitsz.shootstrategy.shootScatter;
import edu.hitsz.supply.*;
import edu.hitsz.supplyfactory.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.Random.*;

public abstract class AbstractGame extends JPanel {

    protected int backGroundTop = 0;
    
    protected int difficulty;
    
    protected BufferedImage currentBackgroundImage;
    
    protected SoundManager soundManager;
    
    protected boolean bossPresent = false;

    protected final ScheduledExecutorService executorService;

    protected int timeInterval = 40;

    protected final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<BaseSupply> supplies;

    protected int enemyMaxNumber = 5;

    protected int score = 0;
    protected int cntscore = 0;
    protected int bossscore = 0;
    protected int time = 0;

    protected int cycleDuration = 600;
    protected int cycleTime = 0;

    protected RankListDao rankList;

    protected boolean gameOverFlag = false;
    
    public interface GameOverCallback {
        void onGameOver(int score, int difficulty);
    }
    
    protected GameOverCallback gameOverCallback;

    public AbstractGame(int difficulty) {
        this.difficulty = difficulty;
        
        soundManager = SoundManager.getInstance();
        
        switch (difficulty) {
            case 1:
                currentBackgroundImage = ImageManager.BACKGROUND_IMAGE_EASY;
                break;
            case 2:
                currentBackgroundImage = ImageManager.BACKGROUND_IMAGE_NORMAL;
                break;
            case 3:
                currentBackgroundImage = ImageManager.BACKGROUND_IMAGE_HARD;
                break;
            default:
                currentBackgroundImage = ImageManager.BACKGROUND_IMAGE;
                break;
        }
        
        heroAircraft = HeroAircraft.getInstance(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight() ,
                0, 0, 1000);

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        supplies = new LinkedList<>();
        rankList = new RankListDaoImpl();

        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        new HeroController(this, heroAircraft);
        
        setInitialParameters();
    }
    
    public void setGameOverCallback(GameOverCallback callback) {
        this.gameOverCallback = callback;
    }
    
    public void setSoundEnabled(boolean enabled) {
        soundManager.setSoundEnabled(enabled);
    }

    public void action() {
        
        soundManager.playNormalBGM();

        Runnable task = () -> {

            time += timeInterval;
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                
                spawnNewEnemies();
                
                spawnBoss();
                
                try {
                    shootAction();
                } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                         IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            bulletsMoveAction();
            aircraftsMoveAction();
            supplyMoveAction();
            crashCheckAction();
            postProcessAction();
            repaint();

            if (heroAircraft.getHp() <= 0) {
                executorService.shutdown();
                heroAircraft.shutdown();
                gameOverFlag = true;
                System.out.println("Game Over!");
                
                soundManager.stopAllSounds();
                soundManager.playGameOverSound();
                
                if (gameOverCallback != null) {
                    SwingUtilities.invokeLater(() -> gameOverCallback.onGameOver(score, difficulty));
                }
            }
            
            updateDifficulty();
        };

        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    protected abstract void spawnNewEnemies();
    protected abstract void spawnBoss();
    protected abstract void updateDifficulty();
    protected abstract void setInitialParameters();

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        List<BaseBullet> newHeroBullets = heroAircraft.shoot();
        if (!newHeroBullets.isEmpty()) {
            soundManager.playBulletSound();
        }
        heroBullets.addAll(newHeroBullets);
    }
    private void supplyMoveAction() {
        for(BaseSupply supply: supplies) {
            supply.forward();
        }
    }
    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void produceSupply(AbstractAircraft enemyAircraft,int speedX,int speedY) {
        Random random = new Random();
        int rnd = random.nextInt(13);
        Supply supply = null;
        SupplyFactory supplyFactory = null;
        if(rnd <3) supplyFactory = new BombSupplyFactory();
        if(rnd >=3 && rnd <6) supplyFactory = new BulletSupplyFactory();
        if(rnd >=6 && rnd <9) supplyFactory = new HpSupplyFactory();
        if(rnd >= 9 && rnd <12) supplyFactory = new BulletPlusSupplyFactory();
        if(rnd < 12 ) supply = supplyFactory.createNewSupply(enemyAircraft.getLocationX(),enemyAircraft.getLocationY(),speedX,speedY
        );
        if(rnd < 12 ) supplies.add((BaseSupply) supply);
        if(rnd < 3) {
            for(AbstractAircraft enemy:enemyAircrafts) {
                if(!enemy.notValid() && enemy instanceof CanBeBlownUp) {
                    ((BombSupply)supply).addenemy((CanBeBlownUp) enemy);
                }
            }
        }
    }
    public void deathOfEnemy(AbstractAircraft enemyAircraft) {
        if(enemyAircraft instanceof EliteEnemy || enemyAircraft instanceof ElitePlusEnemy ) {
            produceSupply(enemyAircraft,0,10);
        }
        if(enemyAircraft instanceof BossEnemy) {
            produceSupply(enemyAircraft,5,10);
            produceSupply(enemyAircraft,0,10);
            produceSupply(enemyAircraft,-5,10);
        }
    }
    private void crashCheckAction() {
        for(BaseBullet bullet:enemyBullets) {
            if(bullet.notValid()) {
                continue;
            }
            if(heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
                soundManager.playBulletHitSound();
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    soundManager.playBulletHitSound();
                    
                    if (enemyAircraft.notValid()) {
                        score += 10;
                        cntscore += 10;
                        bossscore += 10;
                        deathOfEnemy(enemyAircraft);
                        
                        if (enemyAircraft instanceof BossEnemy) {
                            bossPresent = false;
                            soundManager.playNormalBGM();
                        }
                    }
                }
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    produceSupply(enemyAircraft,enemyAircraft.getLocationX(),enemyAircraft.getLocationY());
                    heroAircraft.decreaseHp(100);
                    soundManager.playBulletHitSound();
                }
            }
        }

        for(BaseSupply supply:supplies) {
            if(supply.notValid()) continue;
            if(heroAircraft.crash(supply)) {
                soundManager.playGetSupplySound();
                
                if(supply.getKind() == 0) heroAircraft.addHp(supply.active());
                if(supply.getKind() == 1) {
                    int dscore = supply.active();
                    score += dscore;
                    cntscore += dscore;
                    soundManager.playBombExplosionSound();
                }
                if(supply.getKind() == 2) {
                    BulletSupply bulletSupply = (BulletSupply) supply;
                    bulletSupply.activateEffect(heroAircraft);
                }
                if(supply.getKind() == 3) {
                    BulletPlusSupply bulletPlusSupply = (BulletPlusSupply) supply;
                    bulletPlusSupply.activateEffect(heroAircraft);
                }
                supply.vanish();
            }
        }

    }

    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        supplies.removeIf(AbstractFlyingObject::notValid);
        
        boolean hasBoss = false;
        for (AbstractAircraft aircraft : enemyAircrafts) {
            if (aircraft instanceof BossEnemy) {
                hasBoss = true;
                break;
            }
        }
        
        if (bossPresent && !hasBoss) {
            bossPresent = false;
            soundManager.playNormalBGM();
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.drawImage(currentBackgroundImage, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(currentBackgroundImage, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);

        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, supplies);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }
}
