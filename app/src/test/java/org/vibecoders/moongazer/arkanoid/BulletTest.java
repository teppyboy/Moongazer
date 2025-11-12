package org.vibecoders.moongazer.arkanoid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Bullet class
 */
@DisplayName("Bullet Tests")
class BulletTest extends BaseArkanoidTest {
    private Bullet bullet;
    private static final float EPSILON = 0.001f;

    @BeforeEach
    void setUp() {
        // Create a bullet at position (100, 100) with width 5 and height 19
        bullet = new Bullet(100f, 100f, 5f, 19f);
    }

    @Test
    @DisplayName("Bullet should be created with correct initial properties")
    void testBulletCreation() {
        assertNotNull(bullet);
        assertEquals(100f, bullet.getX(), EPSILON);
        assertEquals(100f, bullet.getY(), EPSILON);
        assertEquals(5f, bullet.getWidth(), EPSILON);
        assertEquals(19f, bullet.getHeight(), EPSILON);
        assertTrue(bullet.isActive(), "Bullet should be active initially");
    }

    @Test
    @DisplayName("Bullet should move upward when updated")
    void testBulletMovement() {
        float initialY = bullet.getY();
        
        // Update bullet for 1 second
        bullet.update(1.0f);
        
        // Bullet should move upward (Y increases)
        assertTrue(bullet.getY() > initialY, "Bullet should move upward");
    }

    @Test
    @DisplayName("Bullet active state should toggle correctly")
    void testBulletActiveState() {
        assertTrue(bullet.isActive());
        
        bullet.setActive(false);
        assertFalse(bullet.isActive());
        
        bullet.setActive(true);
        assertTrue(bullet.isActive());
    }

    @Test
    @DisplayName("Bullet should detect when off screen")
    void testIsOffScreen() {
        float screenHeight = 720f;
        
        // Bullet at y=100 should not be off screen
        assertFalse(bullet.isOffScreen(screenHeight));
        
        // Move bullet above screen (bullets move upward)
        bullet.getBounds().y = screenHeight + 10f;
        assertTrue(bullet.isOffScreen(screenHeight));
    }

    @Test
    @DisplayName("Bullet bounds should be accessible")
    void testGetBounds() {
        var bounds = bullet.getBounds();
        assertNotNull(bounds);
        assertEquals(100f, bounds.x, EPSILON);
        assertEquals(100f, bounds.y, EPSILON);
        assertEquals(5f, bounds.width, EPSILON);
        assertEquals(19f, bounds.height, EPSILON);
    }

    @Test
    @DisplayName("Multiple bullets should be independent")
    void testMultipleBullets() {
        Bullet bullet1 = new Bullet(100f, 100f, 5f, 19f);
        Bullet bullet2 = new Bullet(200f, 150f, 5f, 19f);
        
        assertNotEquals(bullet1.getX(), bullet2.getX());
        assertNotEquals(bullet1.getY(), bullet2.getY());
        
        bullet1.setActive(false);
        assertTrue(bullet2.isActive());
        assertFalse(bullet1.isActive());
    }

    @Test
    @DisplayName("Bullet should move consistently over time")
    void testConsistentMovement() {
        float initialY = bullet.getY();
        
        // Update for 0.5 seconds
        bullet.update(0.5f);
        float y1 = bullet.getY();
        
        // Update for another 0.5 seconds
        bullet.update(0.5f);
        float y2 = bullet.getY();
        
        // Both movements should be roughly equal
        float movement1 = y1 - initialY;
        float movement2 = y2 - y1;
        
        assertEquals(movement1, movement2, 1.0f, "Bullet should move consistently");
    }
}
