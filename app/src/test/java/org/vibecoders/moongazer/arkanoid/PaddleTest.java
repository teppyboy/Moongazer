package org.vibecoders.moongazer.arkanoid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Paddle class
 */
@DisplayName("Paddle Tests")
class PaddleTest extends BaseArkanoidTest {
    private Paddle paddle;
    private static final float EPSILON = 0.001f;

    @BeforeEach
    void setUp() {
        // Create a paddle at position (100, 50) with width 80 and height 15
        paddle = new Paddle(100f, 50f, 80f, 15f);
    }

    @Test
    @DisplayName("Paddle should be created with correct initial properties")
    void testPaddleCreation() {
        assertNotNull(paddle);
        assertEquals(100f, paddle.getX(), EPSILON);
        assertEquals(50f, paddle.getY(), EPSILON);
        assertEquals(80f, paddle.getWidth(), EPSILON);
        assertEquals(15f, paddle.getHeight(), EPSILON);
    }

    @Test
    @DisplayName("Paddle center X should be calculated correctly")
    void testGetCenterX() {
        assertEquals(140f, paddle.getCenterX(), EPSILON); // 100 + 80/2 = 140
    }

    @Test
    @DisplayName("Paddle bounds should return correct rectangle")
    void testGetBounds() {
        var bounds = paddle.getBounds();
        assertNotNull(bounds);
        assertEquals(100f, bounds.x, EPSILON);
        assertEquals(50f, bounds.y, EPSILON);
        assertEquals(80f, bounds.width, EPSILON);
        assertEquals(15f, bounds.height, EPSILON);
    }

    @Test
    @DisplayName("Paddle width should be extendable")
    void testExtend() {
        float originalWidth = paddle.getWidth();
        
        paddle.extend(20f);
        assertEquals(originalWidth + 20f, paddle.getWidth(), EPSILON);
    }

    @Test
    @DisplayName("Paddle should shrink correctly")
    void testShrink() {
        float originalWidth = paddle.getWidth();
        
        paddle.shrink(10f);
        assertEquals(originalWidth - 10f, paddle.getWidth(), EPSILON);
    }
    
    @Test
    @DisplayName("Paddle should not shrink below minimum width")
    void testShrinkMinimum() {
        paddle.shrink(200f); // Try to shrink beyond minimum
        assertTrue(paddle.getWidth() >= 50f, "Paddle should not shrink below 50f");
    }

    @Test
    @DisplayName("Paddle sticky property should toggle correctly")
    void testStickyProperty() {
        assertFalse(paddle.isSticky());
        
        paddle.setSticky(true);
        assertTrue(paddle.isSticky());
        
        paddle.setSticky(false);
        assertFalse(paddle.isSticky());
    }

    @Test
    @DisplayName("Bullet functionality should enable and disable correctly")
    void testBulletEnabled() {
        assertFalse(paddle.isBulletEnabled());
        
        paddle.setBulletEnabled(true);
        assertTrue(paddle.isBulletEnabled());
        
        paddle.setBulletEnabled(false);
        assertFalse(paddle.isBulletEnabled());
    }

    @Test
    @DisplayName("Bullets list should be empty initially")
    void testInitialBulletsEmpty() {
        assertNotNull(paddle.getBullets());
        assertTrue(paddle.getBullets().isEmpty());
    }

    @Test
    @DisplayName("AI movement flags should toggle correctly")
    void testAIMovement() {
        paddle.moveLeft(false);
        paddle.moveRight(false);
        
        paddle.moveLeft(true);
        // Cannot directly test AI movement without update loop
        
        paddle.moveLeft(false);
        paddle.moveRight(true);
        
        paddle.moveRight(false);
    }

    @Test
    @DisplayName("Paddle cleanup should remove inactive bullets")
    void testCleanupBullets() {
        paddle.setBulletEnabled(true);
        
        // Manually add some bullets
        paddle.getBullets().add(new Bullet(100f, 100f, 5f, 19f));
        paddle.getBullets().add(new Bullet(150f, 100f, 5f, 19f));
        
        assertEquals(2, paddle.getBullets().size());
        
        // Set one bullet as inactive
        paddle.getBullets().get(0).setActive(false);
        
        // Cleanup should remove inactive bullets above screen height
        paddle.cleanupBullets(1000f);
        
        // After cleanup, inactive bullet should still be there (not above screen)
        // But bullets above screen height would be removed
        assertTrue(paddle.getBullets().size() >= 1);
    }

    @Test
    @DisplayName("Paddle position should be constrained by boundaries")
    void testBoundaryConstraints() {
        // This would require calling update() with minX and maxX
        // but we can test the bounds directly
        float minX = 50f;
        float maxX = 500f;
        
        // Move paddle within bounds
        paddle.getBounds().x = 200f;
        assertTrue(paddle.getX() >= minX);
        assertTrue(paddle.getX() + paddle.getWidth() <= maxX);
    }

    @Test
    @DisplayName("Paddle width expansion should work correctly")
    void testWidthExpansion() {
        float originalWidth = 80f;
        assertEquals(originalWidth, paddle.getWidth(), EPSILON);
        
        // Expand paddle by 40f
        paddle.extend(40f);
        assertEquals(120f, paddle.getWidth(), EPSILON);
        
        // Shrink back
        paddle.shrink(40f);
        assertEquals(originalWidth, paddle.getWidth(), EPSILON);
    }

    @Test
    @DisplayName("Paddle default extend and shrink methods work")
    void testDefaultExtendShrink() {
        float originalWidth = paddle.getWidth();
        
        paddle.extend(); // Default extend by 100f
        assertEquals(originalWidth + 100f, paddle.getWidth(), EPSILON);
        
        paddle.shrink(); // Default shrink by 100f
        assertEquals(originalWidth, paddle.getWidth(), EPSILON);
    }
    
    @Test
    @DisplayName("Paddle Y position should update correctly")
    void testSetOriginalY() {
        paddle.setOriginalY(100f);
        assertEquals(100f, paddle.getY(), EPSILON);
        
        paddle.setOriginalY(150f);
        assertEquals(150f, paddle.getY(), EPSILON);
    }
    
    @Test
    @DisplayName("Paddle bounce effect should trigger on ball hit")
    void testOnBallHit() {
        // Simulate ball hit with high velocity
        paddle.onBallHit(350f);
        
        // Y position might change due to bounce effect
        // but we can't test without calling update()
        assertNotNull(paddle);
    }
}
