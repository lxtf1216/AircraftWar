import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeroAircraftTest {
    @Test
    @DisplayName("H01: shoot() 应发射 1 发居中子弹")
    public void testShoot_Normal() {
        HeroAircraft hero = HeroAircraft.getInstance(200, 500, 0, 10, 100);
        List<BaseBullet> bullets = hero.shoot();

        assertEquals(1, bullets.size(), "应发射 1 发子弹");

        BaseBullet bullet = bullets.get(0);
        assertTrue(bullet instanceof HeroBullet, "子弹应为 HeroBullet 类型");

        assertEquals(200, bullet.getLocationX());
        assertEquals(498, bullet.getLocationY());

        assertEquals(5, bullet.getSpeedY());

    }
    @Test
    @DisplayName("H02a: addHp() 正常增加生命值")
    public void testAddHp_WithinLimit() {
        HeroAircraft hero = HeroAircraft.getInstance(200, 500, 0, 10, 100);
        hero.decreaseHp(30); // hp = 70
        assertEquals(70, hero.getHp());

        hero.addHp(20);
        assertEquals(90, hero.getHp());
    }

    @Test
    @DisplayName("H02b: addHp() 不会超过最大生命值")
    public void testAddHp_Overflow() {
        HeroAircraft hero = HeroAircraft.getInstance(200, 500, 0, 10, 100);
        hero.addHp(100);
        assertEquals(100, hero.getHp(), "生命值不应超过 maxHp");
    }
    @Test
    @DisplayName("H03: decreaseHp() 应在生命值≤0时触发消失")
    public void testDecreaseHp_Die() {

        HeroAircraft hero = HeroAircraft.getInstance(200, 500, 0, 10, 100);
        assertFalse(hero.notValid());

        hero.decreaseHp(150); // hp = 0, should vanish

        assertEquals(0, hero.getHp());
        assertTrue(hero.notValid(), "生命值归零后英雄机应变为无效");
    }


}