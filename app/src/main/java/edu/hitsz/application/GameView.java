package edu.hitsz.application;

import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import edu.hitsz.aircraft.*;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.game.*;
import edu.hitsz.factory.*;
import edu.hitsz.observer.BombClearObserver;
import edu.hitsz.supply.BaseSupply;
import edu.hitsz.supply.BombSupply;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * 游戏逻辑
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder holder;
    private Thread gameThread;
    private boolean isRunning;

    private Canvas canvas;
    private Paint paint;
    private int screenWidth;
    private int screenHeight;

    /* 游戏对象 */

    private final HeroAircraft heroAircraft;

    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseSupply> props;

    private final BombClearObserver bombObserver = new BombClearObserver();

    /* 工厂 */

    private final AircraftFactory mobFactory = new MobEnemyFactory();
    private final AircraftFactory eliteFactory = new EliteEnemyFactory();
    private final AircraftFactory elitePlusFactory = new ElitePlusEnemyFactory();
    private final AircraftFactory bossFactory = new BossEnemyFactory();

    private final SupplyFactory[] propFactories = {
            new HpSupplyFactory(),
            new BulletSupplyFactory(),
            new BombSupplyFactory(),
            new BulletPlusSupplyFactory()
    };

    /* 游戏参数 */

    private int score = 0;
    private int time = 0;
    private int timeInterval = 16;
    private int cycleDuration = 600;
    private int cycleTime = 0;

    private int shootCounter = 0;
    private int enemyShootCounter = 0;
    private int heroShootCycle = 6;
    private int enemyShootCycle = 12;
    private int enemyMaxNumber = 10;

    private double currentEliteProb = 0.3;
    private double currentAttributeMultiplier = 1.0;
    private final int bossThreshold = 500;
    private int bossTriggerCount = 0;

    private boolean gameOverFlag = false;

    private int backGroundTop = 0;

    private final GameTemplate difficultyTemplate;

    /* 构造 */

    public GameView(Context context) {

        super(context);

        holder = getHolder();
        holder.addCallback(this);

        setFocusable(true);

        heroAircraft = HeroAircraft.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        switch (GameConfig.difficulty) {
            case "EASY":
                difficultyTemplate = new EasyGame();
                break;
            case "HARD":
                difficultyTemplate = new HardGame();
                break;
            default:
                difficultyTemplate = new NormalGame();
        }

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);

        setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.performClick();
            }

            if (event.getAction() == MotionEvent.ACTION_MOVE ||
                    event.getAction() == MotionEvent.ACTION_DOWN) {

                float x = event.getX();
                float y = event.getY();

                if (x < 0 || x > screenWidth || y < 0 || y > screenHeight) {
                    return true;
                }

                heroAircraft.setLocation((int) x, (int) y);
            }

            return true;
        });
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    /* Surface */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenWidth = width;
        screenHeight = height;
        AppSettings.setScreenSize(width, height);
        heroAircraft.setLocation(screenWidth / 2, screenHeight - 150);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /* 游戏主循环 */

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            updateFrame();
            drawGame();
            controlFrameRate(start);
        }
    }

    private void updateFrame() {
        try {
            updateGame();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void controlFrameRate(long start) {
        long frameTime = System.currentTimeMillis() - start;
        if (frameTime < timeInterval) {
            try {
                Thread.sleep(timeInterval - frameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /* ---------------- 游戏逻辑 ---------------- */

    private void updateGame() throws Exception {
        time += timeInterval;
        if (timeCountAndNewCycleJudge()) {
            if (shouldSpawnBoss()) {
                spawnBoss();
            }
            spawnEnemy();
            shootAction();
        }
        moveObjects();
        difficultyTemplate.updateDifficulty(this, score, time);
        crashCheckAction();
        postProcessAction();
        checkGameOver();
    }

    private void moveObjects() {
        bulletsMoveAction();
        aircraftsMoveAction();
        propsMoveAction();
    }

    private void checkGameOver() {
        if (heroAircraft.getHp() <= 0) {
            gameOverFlag = true;
            isRunning = false;
            AudioManager.getInstance().stopBgm();
            AudioManager.getInstance().playGameOver();
        }
    }

    /* draw */
    private void drawGame() {
        canvas = holder.lockCanvas();
        if (canvas == null) return;
        try {
            canvas.drawColor(Color.BLACK);
            drawBackground();
            paintObjects(canvas, enemyBullets);
            paintObjects(canvas, heroBullets);
            paintObjects(canvas, props);
            paintObjects(canvas, enemyAircrafts);

            drawHero();
            drawHUD();
        } finally {

            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawBackground() {
        Bitmap bg = ImageManager.getBackground();
        if (bg == null) return;

        int bgHeight = screenHeight;
        backGroundTop += 2;
        if (backGroundTop >= bgHeight) {
            backGroundTop = 0;
        }

        Rect rect1 = new Rect(0, backGroundTop - bgHeight, screenWidth, backGroundTop);
        Rect rect2 = new Rect(0, backGroundTop, screenWidth, backGroundTop + bgHeight);

        canvas.drawBitmap(bg, null, rect1, null);
        canvas.drawBitmap(bg, null, rect2, null);
    }

    private void drawHero() {
        Bitmap heroImg = ImageManager.get(HeroAircraft.class.getName());
        if (heroImg == null) return;
        canvas.drawBitmap(
                heroImg,
                heroAircraft.getLocationX() - heroImg.getWidth() / 2f,
                heroAircraft.getLocationY() - heroImg.getHeight() / 2f,
                null
        );
    }

    private void drawHUD() {
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, 20, 80, paint);
        canvas.drawText("Life: " + heroAircraft.getHp(), 20, 140, paint);
        if (gameOverFlag) {
            paint.setTextSize(100);
            canvas.drawText(
                    "GAME OVER",
                    screenWidth / 2 - 200,
                    screenHeight / 2,
                    paint
            );
        }
    }

    private void paintObjects(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            AbstractFlyingObject obj = objects.get(i);
            if (obj.notValid()) continue;
            Bitmap img = ImageManager.get(obj.getClass().getName());
            if (img != null) {
                canvas.drawBitmap(
                        img,
                        obj.getLocationX() - img.getWidth() / 2f,
                        obj.getLocationY() - img.getHeight() / 2f,
                        null
                );
            }
        }
    }

    /* Boss */

    private boolean shouldSpawnBoss() {
        int expected = score / bossThreshold;
        if (expected > bossTriggerCount && difficultyTemplate.hasBoss()) {
            bossTriggerCount = expected;
            return true;
        }
        return false;
    }

    private void spawnBoss() {
        int x = (int) (Math.random() * (screenWidth - 200));
        int y = (int) (Math.random() * screenHeight * 0.05);
        double hpMul = difficultyTemplate.getBossHpMultiplier(bossTriggerCount);
        int hp = (int) (600 * hpMul);

        enemyAircrafts.add(
                (AbstractAircraft) bossFactory.createNewEnemyAircraft(
                        x, y, 2, 2, hp
                )
        );
        AudioManager.getInstance().playBossBgm();
    }

    /* ---------------- 敌机生成 ---------------- */

    private void spawnEnemy() {
        if (enemyAircrafts.size() >= enemyMaxNumber) return;
        int x = (int) (Math.random() * (screenWidth - 100));
        int y = (int) (Math.random() * screenHeight * 0.05);
        double r = Math.random();

        if (r < (1 - currentEliteProb)) {
            int hp = (int) (30 * currentAttributeMultiplier);
            int speed = (int) (8 * currentAttributeMultiplier);
            enemyAircrafts.add(
                    (AbstractAircraft) mobFactory.createNewEnemyAircraft(
                            x, y, 0, speed, hp
                    )
            );
        } else if (r < (1 - currentEliteProb * 0.4)) {
            int hp = (int) (60 * currentAttributeMultiplier);
            int speed = (int) (5 * currentAttributeMultiplier);
            int dir = Math.random() > 0.5 ? 2 : -2;
            enemyAircrafts.add(
                    (AbstractAircraft) eliteFactory.createNewEnemyAircraft(
                            x, y, dir, speed, hp
                    )
            );
        } else {
            int hp = (int) (80 * currentAttributeMultiplier);
            int speed = (int) (4 * currentAttributeMultiplier);
            int dir = Math.random() > 0.5 ? 3 : -3;
            enemyAircrafts.add(
                    (AbstractAircraft) elitePlusFactory.createNewEnemyAircraft(
                            x, y, dir, speed, hp
                    )
            );
        }
    }

    /* shoot */

    private void shootAction() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (shootCounter >= heroShootCycle) {
            heroBullets.addAll(heroAircraft.shoot());
            shootCounter = 0;
        } else {
            shootCounter++;
        }
        if (enemyShootCounter >= enemyShootCycle) {
            for (AbstractAircraft enemy : enemyAircrafts) {
                if (!enemy.notValid()) {
                    enemyBullets.addAll(enemy.shoot());
                }
            }
            enemyShootCounter = 0;
        } else {
            enemyShootCounter++;
        }
    }

    /* move */
    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) bullet.forward();
        for (BaseBullet bullet : enemyBullets) bullet.forward();
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemy : enemyAircrafts) enemy.forward();
    }

    private void propsMoveAction() {
        for (BaseSupply prop : props) prop.forward();
    }

    /* 碰撞 */

    private void crashCheckAction() {
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) continue;
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) continue;
            for (AbstractAircraft enemy : enemyAircrafts) {
                if (enemy.notValid()) continue;
                if (enemy.crash(bullet)) {
                    enemy.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    AudioManager.getInstance().playBulletHit();
                    if (enemy.notValid()) {
                        score += 10;
                        if (enemy instanceof EliteEnemy) {
                            if (Math.random() < 0.5) {
                                randomProp(enemy.getLocationX(), enemy.getLocationY());
                            }
                            score += 10;
                        }
                        if (enemy instanceof ElitePlusEnemy) {
                            if (Math.random() < 0.3) {
                                randomProp(enemy.getLocationX() + 50, enemy.getLocationY() + 50);
                                randomProp(enemy.getLocationX() - 50, enemy.getLocationY() - 50);
                            }
                            score += 15;
                        }
                        if (enemy instanceof BossEnemy) {
                            randomProp(enemy.getLocationX(), enemy.getLocationY());
                            randomProp(enemy.getLocationX() + 50, enemy.getLocationY() + 50);
                            randomProp(enemy.getLocationX() - 50, enemy.getLocationY() - 50);
                            score += 20;
                            AudioManager.getInstance().playNormalBgm();
                        }
                    }
                }
                if (enemy.crash(heroAircraft) || heroAircraft.crash(enemy)) {
                    enemy.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        for (BaseSupply prop : props) {
            if (prop.notValid()) continue;
            if (heroAircraft.crash(prop)) {
                prop.active();
                if (prop instanceof BombSupply) {
                    score = bombObserver.onBombClear(
                            enemyAircrafts,
                            enemyBullets,
                            heroAircraft,
                            score
                    );
                    AudioManager.getInstance().playBomb();
                } else {
                    AudioManager.getInstance().playGetSupply();
                }
                prop.vanish();
            }
        }
    }

    /* prop */
    private void randomProp(int x, int y) {
        double r = Math.random();
        SupplyFactory factory;
        if (r < 0.1) {
            factory = propFactories[2];
        } else if (r < 0.6) {
            factory = propFactories[1];
        } else if (r < 0.8) {
            factory = propFactories[0];
        } else {
            factory = propFactories[3];
        }
        props.add(
                (BaseSupply) factory.createNewSupply(x, y, 0, 4)
        );
    }

    /* 后处理 */

    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            cycleTime %= cycleDuration;
            return true;
        }

        return false;
    }

    /* 难度接口 */

    public void setHeroShootCycle(int cycleMs) {
        this.heroShootCycle = cycleMs / timeInterval;
    }

    public void setEnemyShootCycle(int cycleMs) {
        this.enemyShootCycle = cycleMs / timeInterval;
    }

    public void setEnemyMaxNumber(int max) {
        this.enemyMaxNumber = max;
    }

    public void setEnemyCycle(int cycleMs) {
        this.cycleDuration = cycleMs;
    }

    public void setEliteProb(double prob) {
        this.currentEliteProb = prob;
    }

    public void setAttributeMultiplier(double multiplier) {
        this.currentAttributeMultiplier = multiplier;
    }
}