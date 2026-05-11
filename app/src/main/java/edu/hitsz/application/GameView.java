package edu.hitsz.application;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import edu.hitsz.aircraft.*;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.game.*;
import edu.hitsz.factory.*;
import edu.hitsz.multiplayer.GameClient;
import edu.hitsz.observer.BombClearObserver;
import edu.hitsz.supply.BaseSupply;
import edu.hitsz.supply.BombSupply;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * Game logic
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder holder;
    private Thread gameThread;
    private boolean isRunning;

    private Canvas canvas;
    private Paint paint;
    private int screenWidth;
    private int screenHeight;

    /* Game objects */
    private final HeroAircraft heroAircraft;

    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseSupply> props;

    private final BombClearObserver bombObserver = new BombClearObserver();

    /* Factories */
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

    /* Game parameters */
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

    private volatile boolean gameOverFlag = false;

    private int backGroundTop = 0;

    private final GameTemplate difficultyTemplate;

    /* Multiplayer */
    private boolean multiplayerMode = false;
    private String localPlayerName = "";
    private String serverIp = "";

    // Opponent info
    private volatile String opponentName = "Opponent";
    private volatile int opponentScore = 0;
    private volatile int opponentHp = 100;
    private volatile boolean opponentAlive = true;
    private volatile boolean localPlayerDead = false;
    private volatile boolean bothPlayersDead = false;
    private volatile int finalOpponentScore = 0;

    /* Constructor */
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
            if (localPlayerDead) {
                return true;
            }
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

    public GameView(Context context, boolean multiplayerMode, String playerName, String serverIp) {
        this(context);
        this.multiplayerMode = multiplayerMode;
        this.localPlayerName = playerName;
        this.serverIp = serverIp;

        if (multiplayerMode) {
            setupMultiplayerListener();
        }
    }

    private void setupMultiplayerListener() {
        GameClient client = GameClient.getInstance();
        client.setListener(new GameClient.GameClientListener() {
            @Override
            public void onConnected() {}

            @Override
            public void onDisconnected() {}

            @Override
            public void onConnectionError(String error) {}

            @Override
            public void onScoreUpdate(int score) {
                opponentScore = score;
            }

            @Override
            public void onHpUpdate(int hp) {
                opponentHp = hp;
                if (hp <= 0) {
                    opponentAlive = false;
                }
            }

            @Override
            public void onPlayerDead() {
                opponentAlive = false;
            }

            @Override
            public void onGameOver(int finalScore) {
                finalOpponentScore = finalScore;
                opponentAlive = false;
            }

            @Override
            public void onOpponentInfoReceived(String name, int score, int hp, boolean alive) {
                opponentName = name;
                opponentScore = score;
                opponentHp = hp;
                opponentAlive = alive;
            }

            @Override
            public void onGameStart() {}

            @Override
            public void onOpponentDisconnected() {
                opponentAlive = false;
            }
        });
    }

    public interface OnGameOverListener {
        void onGameOver(int finalScore);
    }

    private OnGameOverListener onGameOverListener;
    private boolean gameOverNotified = false;

    public void setOnGameOverListener(OnGameOverListener listener) {
        this.onGameOverListener = listener;
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
        heroAircraft.reset(screenWidth / 2, screenHeight - 150);
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

    /* Game loop */
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

    /* Game logic */
    private void updateGame() throws Exception {

        // 本地玩家死亡后，不再生成/射击
        if (!localPlayerDead) {
            time += timeInterval;
            if (timeCountAndNewCycleJudge()) {
                if (shouldSpawnBoss()) {
                    spawnBoss();
                }
                spawnEnemy();
                shootAction();
            }
            difficultyTemplate.updateDifficulty(this, score, time);
        }

        // 世界继续运行
        moveObjects();
        // 本地玩家死亡后不再碰撞
        if (!localPlayerDead) {
            crashCheckAction();
        }

        postProcessAction();
        checkGameOver();
    }

    private void moveObjects() {
        bulletsMoveAction();
        aircraftsMoveAction();
        propsMoveAction();
    }

    private void checkGameOver() {
        if (gameOverFlag) return;

        if (multiplayerMode) {
            if (heroAircraft.getHp() <= 0 && !localPlayerDead) {
                localPlayerDead = true;
                new Thread(() -> {
                    try {
                        GameClient client = GameClient.getInstance();
                        client.sendPlayerDead();
                        client.sendGameOver(score);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            if (localPlayerDead && !opponentAlive) {
                bothPlayersDead = true;
                gameOverFlag = true;
                AudioManager.getInstance().stopBgm();
                AudioManager.getInstance().playGameOver();

                // 触发结算弹窗
                if (!gameOverNotified && onGameOverListener != null) {
                    gameOverNotified = true;
                    post(() -> onGameOverListener.onGameOver(score));
                }
            }
        } else {
            if (heroAircraft.getHp() <= 0 && !gameOverFlag) {
                gameOverFlag = true;
                AudioManager.getInstance().stopBgm();
                AudioManager.getInstance().playGameOver();
                if (!gameOverNotified && onGameOverListener != null) {
                    gameOverNotified = true;
                    post(() -> onGameOverListener.onGameOver(score));
                }
            }
        }
    }

    /* Draw */
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
        canvas.drawBitmap(heroImg,
                heroAircraft.getLocationX() - heroImg.getWidth() / 2f,
                heroAircraft.getLocationY() - heroImg.getHeight() / 2f, null);
    }

    private void drawHUD() {
        paint.setTextSize(50);

        if (multiplayerMode) {
            paint.setColor(0xFF00FF00);
            canvas.drawText(localPlayerName + ": " + score, 20, 80, paint);
            canvas.drawText("HP: " + Math.max(0, heroAircraft.getHp()), 20, 140, paint);

            paint.setColor(0xFFFF6666);
            String status = opponentAlive ? "HP: " + opponentHp : "Dead";
            canvas.drawText(opponentName + ": " + opponentScore, screenWidth - 400, 80, paint);
            canvas.drawText(status, screenWidth - 400, 140, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(30);
            canvas.drawText("[MULTIPLAYER MODE]", screenWidth / 2 - 130, 40, paint);
        } else {
            paint.setColor(Color.WHITE);
            canvas.drawText("Score: " + score, 20, 80, paint);
            canvas.drawText("Life: " + heroAircraft.getHp(), 20, 140, paint);
        }

        if (multiplayerMode && localPlayerDead && !gameOverFlag && opponentAlive) {
            paint.setColor(0x88000000);
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

            paint.setColor(Color.RED);
            paint.setTextSize(100);
            paint.setFakeBoldText(true);
            canvas.drawText("YOU DIED", screenWidth / 2f - 220, screenHeight / 2f - 50, paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            paint.setFakeBoldText(false);
            canvas.drawText("Waiting for opponent...", screenWidth / 2f - 270, screenHeight / 2f + 50, paint);

            paint.setTextSize(40);
            canvas.drawText("Opponent Score: " + opponentScore, screenWidth / 2f - 200, screenHeight / 2f + 150, paint);
        }

        if (gameOverFlag) {
            paint.setColor(0xAA000000);
            canvas.drawRect(0, 0, screenWidth, screenHeight, paint);

            paint.setTextSize(120);
            paint.setColor(Color.RED);
            paint.setFakeBoldText(true);
            canvas.drawText("GAME OVER", screenWidth / 2 - 300, screenHeight / 2 - 100, paint);

            paint.setColor(Color.WHITE);
            paint.setFakeBoldText(false);
            if (multiplayerMode) {
                paint.setTextSize(50);
                int displayOpponentScore = finalOpponentScore > 0 ? finalOpponentScore : opponentScore;
                String result;
                if (score > displayOpponentScore) {
                    paint.setColor(Color.YELLOW);
                    result = "VICTORY!";
                } else if (score < displayOpponentScore) {
                    paint.setColor(Color.LTGRAY);
                    result = "DEFEAT";
                } else {
                    result = "DRAW";
                }
                canvas.drawText(result, screenWidth / 2 - 100, screenHeight / 2 + 50, paint);

                paint.setColor(Color.WHITE);
                paint.setTextSize(40);
                canvas.drawText("Final Score: " + score + " vs " + displayOpponentScore, screenWidth / 2 - 250, screenHeight / 2 + 150, paint);
            }
        }
    }

    private void paintObjects(Canvas canvas, List<? extends AbstractFlyingObject> objects) {
        for (AbstractFlyingObject obj : objects) {
            if (obj.notValid()) continue;
            Bitmap img = ImageManager.get(obj.getClass().getName());
            if (img != null) {
                canvas.drawBitmap(img,
                        obj.getLocationX() - img.getWidth() / 2f,
                        obj.getLocationY() - img.getHeight() / 2f, null);
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
        enemyAircrafts.add((AbstractAircraft) bossFactory.createNewEnemyAircraft(x, y, 2, 2, hp));
        AudioManager.getInstance().playBossBgm();
    }

    /* Enemy spawn */
    private void spawnEnemy() {
        if (enemyAircrafts.size() >= enemyMaxNumber) return;
        int x = (int) (Math.random() * (screenWidth - 100));
        int y = (int) (Math.random() * screenHeight * 0.05);
        double r = Math.random();

        if (r < (1 - currentEliteProb)) {
            enemyAircrafts.add((AbstractAircraft) mobFactory.createNewEnemyAircraft(
                    x, y, 0, (int) (8 * currentAttributeMultiplier),
                    (int) (30 * currentAttributeMultiplier)));
        } else if (r < (1 - currentEliteProb * 0.4)) {
            int dir = Math.random() > 0.5 ? 2 : -2;
            enemyAircrafts.add((AbstractAircraft) eliteFactory.createNewEnemyAircraft(
                    x, y, dir, (int) (5 * currentAttributeMultiplier),
                    (int) (60 * currentAttributeMultiplier)));
        } else {
            int dir = Math.random() > 0.5 ? 3 : -3;
            enemyAircrafts.add((AbstractAircraft) elitePlusFactory.createNewEnemyAircraft(
                    x, y, dir, (int) (4 * currentAttributeMultiplier),
                    (int) (80 * currentAttributeMultiplier)));
        }
    }

    /* Shoot */
    private void shootAction() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (!localPlayerDead && shootCounter >= heroShootCycle) {
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

    /* Move */
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

    /* Collision */
    private void crashCheckAction() {
        int hpBefore = heroAircraft.getHp();
        int scoreBefore = score;

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
                            if (Math.random() < 0.5) randomProp(enemy.getLocationX(), enemy.getLocationY());
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
                    score = bombObserver.onBombClear(enemyAircrafts, enemyBullets, heroAircraft, score);
                    AudioManager.getInstance().playBomb();
                } else {
                    AudioManager.getInstance().playGetSupply();
                }
                prop.vanish();
            }
        }

        // Send multiplayer updates
        if (multiplayerMode && !gameOverFlag) {
            GameClient client = GameClient.getInstance();
            int hpAfter = heroAircraft.getHp();
            if (hpAfter != hpBefore) {
                client.sendHpUpdate(hpAfter);
            }
            if (score != scoreBefore) {
                client.sendScoreUpdate(score);
            }
        }
    }

    private void randomProp(int x, int y) {
        double r = Math.random();
        SupplyFactory factory;
        if (r < 0.1) factory = propFactories[2];
        else if (r < 0.6) factory = propFactories[1];
        else if (r < 0.8) factory = propFactories[0];
        else factory = propFactories[3];
        props.add((BaseSupply) factory.createNewSupply(x, y, 0, 4));
    }

    /* Post process */
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

    /* Difficulty interface */
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

    public int getScore() {
        return score;
    }

    public boolean isMultiplayerMode() {
        return multiplayerMode;
    }
}
