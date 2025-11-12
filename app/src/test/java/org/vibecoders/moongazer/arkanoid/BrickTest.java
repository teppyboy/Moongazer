package org.vibecoders.moongazer.arkanoid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Brick class
 */
@DisplayName("Brick Tests")
class BrickTest extends BaseArkanoidTest {
    private Brick breakableBrick;
    private Brick unbreakableBrick;
    private static final float EPSILON = 0.001f;

    @BeforeEach
    void setUp() {
        // Create a breakable brick with 1 durability
        breakableBrick = new Brick(100f, 200f, 60f, 20f, Brick.BrickType.BREAKABLE);
        
        // Create an unbreakable brick
        unbreakableBrick = new Brick(200f, 300f, 60f, 20f, Brick.BrickType.UNBREAKABLE);
    }

    @Test
    @DisplayName("Breakable brick should be created with correct properties")
    void testBreakableBrickCreation() {
        assertNotNull(breakableBrick);
        assertEquals(Brick.BrickType.BREAKABLE, breakableBrick.getType());
        assertFalse(breakableBrick.isDestroyed());
        assertEquals(1, breakableBrick.getDurability());
    }

    @Test
    @DisplayName("Unbreakable brick should be created with correct properties")
    void testUnbreakableBrickCreation() {
        assertNotNull(unbreakableBrick);
        assertEquals(Brick.BrickType.UNBREAKABLE, unbreakableBrick.getType());
        assertFalse(unbreakableBrick.isDestroyed());
        assertEquals(-1, unbreakableBrick.getDurability());
    }

    @Test
    @DisplayName("Breakable brick should be destroyed after one hit")
    void testBreakableBrickOneHit() {
        assertFalse(breakableBrick.isDestroyed());
        assertEquals(1, breakableBrick.getDurability());
        
        breakableBrick.hit();
        
        assertTrue(breakableBrick.isDestroyed());
        assertEquals(0, breakableBrick.getDurability());
    }

    @Test
    @DisplayName("Unbreakable brick should never be destroyed")
    void testUnbreakableBrickHit() {
        assertFalse(unbreakableBrick.isDestroyed());
        
        unbreakableBrick.hit();
        unbreakableBrick.hit();
        unbreakableBrick.hit();
        
        assertFalse(unbreakableBrick.isDestroyed());
        assertEquals(-1, unbreakableBrick.getDurability());
    }

    @Test
    @DisplayName("Brick with multiple durability should require multiple hits")
    void testMultiDurabilityBrick() {
        Brick strongBrick = new Brick(100f, 200f, 60f, 20f, Brick.BrickType.BREAKABLE, 3);
        
        assertEquals(3, strongBrick.getDurability());
        assertFalse(strongBrick.isDestroyed());
        
        strongBrick.hit();
        assertEquals(2, strongBrick.getDurability());
        assertFalse(strongBrick.isDestroyed());
        
        strongBrick.hit();
        assertEquals(1, strongBrick.getDurability());
        assertFalse(strongBrick.isDestroyed());
        
        strongBrick.hit();
        assertEquals(0, strongBrick.getDurability());
        assertTrue(strongBrick.isDestroyed());
    }

    @Test
    @DisplayName("Brick should be created with power-up type")
    void testBrickWithPowerUp() {
        Brick powerUpBrick = new Brick(100f, 200f, 60f, 20f, 
            Brick.BrickType.BREAKABLE, Brick.PowerUpType.EXPAND_PADDLE);
        
        assertEquals(Brick.PowerUpType.EXPAND_PADDLE, powerUpBrick.getPowerUpType());
    }

    @Test
    @DisplayName("Brick bounds should be set correctly")
    void testBrickBounds() {
        assertEquals(100f, breakableBrick.getX(), EPSILON);
        assertEquals(200f, breakableBrick.getY(), EPSILON);
        assertEquals(60f, breakableBrick.getWidth(), EPSILON);
        assertEquals(20f, breakableBrick.getHeight(), EPSILON);
    }

    @Test
    @DisplayName("Multiple power-up types should be assignable")
    void testVariousPowerUpTypes() {
        Brick extraLifeBrick = new Brick(0f, 0f, 60f, 20f, 
            Brick.BrickType.BREAKABLE, Brick.PowerUpType.EXTRA_LIFE);
        assertEquals(Brick.PowerUpType.EXTRA_LIFE, extraLifeBrick.getPowerUpType());
        
        Brick fastBallBrick = new Brick(0f, 0f, 60f, 20f, 
            Brick.BrickType.BREAKABLE, Brick.PowerUpType.FAST_BALL);
        assertEquals(Brick.PowerUpType.FAST_BALL, fastBallBrick.getPowerUpType());
        
        Brick slowBallBrick = new Brick(0f, 0f, 60f, 20f, 
            Brick.BrickType.BREAKABLE, Brick.PowerUpType.SLOW_BALL);
        assertEquals(Brick.PowerUpType.SLOW_BALL, slowBallBrick.getPowerUpType());
        
        Brick multiBallBrick = new Brick(0f, 0f, 60f, 20f, 
            Brick.BrickType.BREAKABLE, Brick.PowerUpType.MULTI_BALL);
        assertEquals(Brick.PowerUpType.MULTI_BALL, multiBallBrick.getPowerUpType());
        
        Brick superBallBrick = new Brick(0f, 0f, 60f, 20f, 
            Brick.BrickType.BREAKABLE, Brick.PowerUpType.SUPER_BALL);
        assertEquals(Brick.PowerUpType.SUPER_BALL, superBallBrick.getPowerUpType());
    }

    @Test
    @DisplayName("Static factory method should create brick with correct level")
    void testCreateBreakableBrickWithLevel() {
        Brick level1 = Brick.createBreakableBrick(0f, 0f, 60f, 20f, 1);
        assertEquals(1, level1.getDurability());
        
        Brick level2 = Brick.createBreakableBrick(0f, 0f, 60f, 20f, 2);
        assertEquals(2, level2.getDurability());
        
        Brick level3 = Brick.createBreakableBrick(0f, 0f, 60f, 20f, 3);
        assertEquals(3, level3.getDurability());
    }

    @Test
    @DisplayName("Brick should not be destroyed when durability is above zero")
    void testBrickNotDestroyedWithDurability() {
        Brick toughBrick = new Brick(100f, 200f, 60f, 20f, Brick.BrickType.BREAKABLE, 5);
        
        for (int i = 0; i < 4; i++) {
            toughBrick.hit();
            assertFalse(toughBrick.isDestroyed(), "Brick should not be destroyed yet");
        }
        
        toughBrick.hit();
        assertTrue(toughBrick.isDestroyed(), "Brick should now be destroyed");
    }
}
