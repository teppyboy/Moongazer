package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Ball class
 */
@DisplayName("Ball Tests")
class BallTest extends BaseArkanoidTest {
    private Ball ball;
    private static final float EPSILON = 0.001f;

    @BeforeEach
    void setUp() {
        // Create a ball at position (100, 100) with radius 12
        ball = new Ball(100f, 100f, 12f);
    }

    @Test
    @DisplayName("Ball should be created with correct initial properties")
    void testBallCreation() {
        assertNotNull(ball);
        assertEquals(12f, ball.getRadius(), EPSILON);
        assertFalse(ball.isActive(), "Ball should not be active initially");
        assertFalse(ball.isSuperBall(), "Ball should not be super ball initially");
        assertFalse(ball.isHeavyBall(), "Ball should not be heavy ball initially");
    }

    @Test
    @DisplayName("Ball should be activated when launched")
    void testBallLaunch() {
        assertFalse(ball.isActive());
        ball.launch();
        assertTrue(ball.isActive());
    }

    @Test
    @DisplayName("Ball velocity should reverse X direction correctly")
    void testReverseX() {
        Vector2 initialVelocity = ball.getVelocity().cpy();
        ball.reverseX();
        assertEquals(-initialVelocity.x, ball.getVelocity().x, EPSILON);
        assertEquals(initialVelocity.y, ball.getVelocity().y, EPSILON);
    }

    @Test
    @DisplayName("Ball velocity should reverse Y direction correctly")
    void testReverseY() {
        Vector2 initialVelocity = ball.getVelocity().cpy();
        ball.reverseY();
        assertEquals(initialVelocity.x, ball.getVelocity().x, EPSILON);
        assertEquals(-initialVelocity.y, ball.getVelocity().y, EPSILON);
    }

    @Test
    @DisplayName("Ball should update custom velocity")
    void testSetVelocity() {
        ball.setVelocity(500f, 400f);
        assertEquals(500f, ball.getVelocity().x, EPSILON);
        assertEquals(400f, ball.getVelocity().y, EPSILON);
    }

    @Test
    @DisplayName("Ball speed should be calculated correctly")
    void testGetSpeed() {
        ball.setVelocity(300f, 400f);
        // Speed = sqrt(300^2 + 400^2) = 500
        assertEquals(500f, ball.getSpeed(), EPSILON);
    }

    @Test
    @DisplayName("Ball velocity should normalize to target speed")
    void testNormalizeVelocity() {
        ball.setVelocity(300f, 400f); // Speed = 500
        ball.normalizeVelocity(100f); // Normalize to speed 100
        
        assertEquals(100f, ball.getSpeed(), EPSILON);
        // Direction should be maintained (3:4 ratio)
        assertEquals(60f, ball.getVelocity().x, EPSILON); // 300/500 * 100
        assertEquals(80f, ball.getVelocity().y, EPSILON); // 400/500 * 100
    }

    @Test
    @DisplayName("Ball should reset to specified position and become inactive")
    void testReset() {
        ball.launch();
        ball.setVelocity(500f, 500f);
        
        ball.reset(200f, 200f);
        
        assertFalse(ball.isActive());
        assertEquals(300f, ball.getVelocity().x, EPSILON);
        assertEquals(300f, ball.getVelocity().y, EPSILON);
    }

    @Test
    @DisplayName("Ball center coordinates should be calculated correctly")
    void testGetCenter() {
        ball.reset(100f, 100f);
        assertEquals(112f, ball.getCenterX(), EPSILON); // 100 + 12 (radius)
        assertEquals(112f, ball.getCenterY(), EPSILON); // 100 + 12 (radius)
    }

    @Test
    @DisplayName("Super ball property should toggle correctly")
    void testSuperBall() {
        assertFalse(ball.isSuperBall());
        ball.setSuperBall(true);
        assertTrue(ball.isSuperBall());
        ball.setSuperBall(false);
        assertFalse(ball.isSuperBall());
    }

    @Test
    @DisplayName("Heavy ball property should toggle correctly")
    void testHeavyBall() {
        assertFalse(ball.isHeavyBall());
        ball.setHeavyBall(true);
        assertTrue(ball.isHeavyBall());
        ball.setHeavyBall(false);
        assertFalse(ball.isHeavyBall());
    }

    @Test
    @DisplayName("Combo count should increment and reset correctly")
    void testComboCount() {
        assertEquals(0, ball.getComboCount());
        
        ball.incrementCombo();
        assertEquals(1, ball.getComboCount());
        
        ball.incrementCombo();
        ball.incrementCombo();
        assertEquals(3, ball.getComboCount());
        
        ball.resetCombo();
        assertEquals(0, ball.getComboCount());
    }

    @Test
    @DisplayName("Ball speed multiplier should affect speed correctly")
    void testSpeedMultiplier() {
        assertEquals(1.0f, ball.getSpeedMultiplier(), EPSILON);
        
        ball.setSpeedMultiplier(2.0f);
        assertEquals(2.0f, ball.getSpeedMultiplier(), EPSILON);
        
        ball.setSpeedMultiplier(0.5f);
        assertEquals(0.5f, ball.getSpeedMultiplier(), EPSILON);
    }

    @Test
    @DisplayName("Ball should stick to paddle correctly")
    void testStuckToPaddle() {
        assertFalse(ball.isStuckToPaddle());
        
        ball.setStuckToPaddle(true);
        assertTrue(ball.isStuckToPaddle());
        
        ball.setStuckToPaddle(false);
        assertFalse(ball.isStuckToPaddle());
    }

    @Test
    @DisplayName("Ball stuck offset should be stored correctly")
    void testStuckOffset() {
        ball.setStuckOffsetX(25.5f);
        assertEquals(25.5f, ball.getStuckOffsetX(), EPSILON);
        
        ball.setStuckOffsetX(-10.0f);
        assertEquals(-10.0f, ball.getStuckOffsetX(), EPSILON);
    }
}
