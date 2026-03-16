package edu.hitsz.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

import edu.hitsz.R;
import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.supply.BombSupply;
import edu.hitsz.supply.BulletPlusSupply;
import edu.hitsz.supply.BulletSupply;
import edu.hitsz.supply.HpSupply;

/**
 * 载入图像
 */
public class ImageManager {
    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();
    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap BACKGROUND_IMAGE_EASY;
    public static Bitmap BACKGROUND_IMAGE_NORMAL;
    public static Bitmap BACKGROUND_IMAGE_HARD;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;

    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap ELITEPLUS_ENEMY_IMAGE;
    public static Bitmap BOSS_ENEMY_IMAGE;

    public static Bitmap HP_SUPPLY_IMAGE;
    public static Bitmap BOMB_SUPPLY_IMAGE;
    public static Bitmap BULLET_SUPPLY_IMAGE;
    public static Bitmap BULLETPLUS_SUPPLY_IMAGE;

    public static void init(Context context) {
        // 加载背景
        BACKGROUND_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg);
        BACKGROUND_IMAGE_EASY = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg2);
        BACKGROUND_IMAGE_NORMAL = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg3);
        BACKGROUND_IMAGE_HARD = BitmapFactory.decodeResource(context.getResources(), R.drawable.bg4);

        // 加载英雄机图片
        HERO_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero);

        // 敌机
        MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.mob);
        ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.elite);
        ELITEPLUS_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.eliteplus);
        BOSS_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.boss);

        // 子弹
        HERO_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_hero);
        ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet_enemy);

        // 道具
        HP_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_blood);
        BOMB_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bomb);
        BULLET_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bullet);
        BULLETPLUS_SUPPLY_IMAGE = BitmapFactory.decodeResource(context.getResources(), R.drawable.prop_bulletplus);

        CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
        CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(ElitePlusEnemy.class.getName(), ELITEPLUS_ENEMY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);

        CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
        CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);

        CLASSNAME_IMAGE_MAP.put(BombSupply.class.getName(), BOMB_SUPPLY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(HpSupply.class.getName(), HP_SUPPLY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BulletSupply.class.getName(), BULLET_SUPPLY_IMAGE);
        CLASSNAME_IMAGE_MAP.put(BulletPlusSupply.class.getName(), BULLETPLUS_SUPPLY_IMAGE);
    }

    public static Bitmap get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object obj) {
        if (obj == null) return null;
        return get(obj.getClass().getName());
    }

    public static Bitmap getBackground() {
        return BACKGROUND_IMAGE;
    }
}