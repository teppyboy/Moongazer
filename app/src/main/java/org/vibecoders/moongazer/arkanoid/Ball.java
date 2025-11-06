package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.vibecoders.moongazer.managers.Assets;

public class Ball extends MovableObject {
    private Vector2 velocity;
    private float radius;
    private Texture texture;
    private boolean active;
    
    private float previousY;          // For swept collision detection
    private boolean isHeavyBall = false;  // Heavy ball passes through bricks
    private boolean isStuckToPaddle = false;  // For sticky paddle power-up
    private float stuckOffsetX = 0f;  // Offset from paddle center when stuck
    private int comboCount = 0;       // Consecutive brick hits counter
    private float speedMultiplier = 1.0f;  // Dynamic speed adjustment

    public Ball(float x, float y, float radius) {
        super((int)x, (int)y, (int)radius * 2, (int)radius * 2);
        this.velocity = new Vector2(300, 300); // Initial velocity
        this.radius = radius;
        this.active = false;
        this.texture = Assets.getAsset("textures/arkanoid/normal_ball.png", Texture.class);
    }

    public void update(float delta) {
        // Store previous Y position for swept collision detection
        previousY = bounds.y;
        
        // If ball is stuck to paddle, don't update position
        if (active && !isStuckToPaddle) {
            bounds.x += velocity.x * delta;
            bounds.y += velocity.y * delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x - radius, bounds.y - radius, radius * 2, radius * 2);
    }

    public void launch() {
        active = true;
    }

    public void reset(float x, float y) {
        bounds.set(x, y, radius * 2, radius * 2);
        velocity.set(300, 300);
        active = false;
    }

    public void reverseX() {
        velocity.x = -velocity.x;
    }

    public void reverseY() {
        velocity.y = -velocity.y;
    }

    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getRadius() {
        return radius;
    }

    public boolean isActive() {
        return active;
    }
    
    /**
     * Get the center X coordinate of the ball.
     * Since bounds stores top-left corner, we add radius to get center.
     */
    public float getCenterX() {
        return bounds.x + radius;
    }
    
    /**
     * Get the center Y coordinate of the ball.
     * Since bounds stores top-left corner, we add radius to get center.
     */
    public float getCenterY() {
        return bounds.y + radius;
    }
    
    /**
     * Get current speed magnitude.
     */
    public float getSpeed() {
        return velocity.len();
    }
    
    /**
     * Normalize velocity to maintain constant speed.
     */
    public void normalizeVelocity(float targetSpeed) {
        float currentSpeed = getSpeed();
        if (currentSpeed > 0) {
            velocity.scl(targetSpeed / currentSpeed);
        }
    }
    
    /**
     * Gets the previous Y position (from last frame).
     * Used for swept collision detection.
     */
    public float getPreviousY() {
        return previousY;
    }
    
    // ===== Heavy Ball Mode =====
    
    /**
     * Sets the heavy ball mode for this ball.
     * When heavy ball is active, the ball destroys bricks without bouncing.
     *
     * @param heavyBall true to enable heavy ball mode, false to disable
     */
    public void setHeavyBall(boolean heavyBall) {
        this.isHeavyBall = heavyBall;
    }
    
    /**
     * Gets whether this ball is in heavy ball mode.
     *
     * @return true if heavy ball mode is active, false otherwise
     */
    public boolean isHeavyBall() {
        return isHeavyBall;
    }
    
    // ===== Sticky Paddle Support =====
    
    /**
     * Sets whether this ball is stuck to the paddle.
     *
     * @param stuck true if stuck to paddle, false otherwise
     */
    public void setStuckToPaddle(boolean stuck) {
        this.isStuckToPaddle = stuck;
    }
    
    /**
     * Gets whether this ball is stuck to the paddle.
     *
     * @return true if stuck to paddle, false otherwise
     */
    public boolean isStuckToPaddle() {
        return isStuckToPaddle;
    }
    
    /**
     * Sets the offset from paddle center where the ball is stuck.
     *
     * @param offsetX offset in pixels from paddle center
     */
    public void setStuckOffsetX(float offsetX) {
        this.stuckOffsetX = offsetX;
    }
    
    /**
     * Gets the offset from paddle center where the ball is stuck.
     *
     * @return offset in pixels from paddle center
     */
    public float getStuckOffsetX() {
        return stuckOffsetX;
    }
    
    /**
     * Resets the ball to the center of the paddle and sticks it there.
     * Used when ball is lost or for sticky paddle power-up.
     *
     * @param paddle the paddle to attach to
     */
    public void resetToCenter(Paddle paddle) {
        Rectangle paddleBounds = paddle.getBounds();
        float paddleCenterX = paddleBounds.x + paddleBounds.width / 2f;
        float paddleTop = paddleBounds.y + paddleBounds.height;
        reset(paddleCenterX, paddleTop + radius + 5);
        isStuckToPaddle = true;
        stuckOffsetX = 0f;
        velocity.set(0, 0);
    }
    
    /**
     * Launches the ball from the paddle with angle based on stuck position.
     * The further from center, the steeper the angle.
     */
    public void launchFromPaddle(Paddle paddle, float maxBounceAngle) {
        if (isStuckToPaddle) {
            // Calculate offset ratio [-1, 1] based on stuck position
            float paddleHalfWidth = paddle.getBounds().width / 2f;
            float offset = stuckOffsetX / paddleHalfWidth;
            offset = Math.max(-1.0f, Math.min(1.0f, offset));
            
            // Map offset to bounce angle
            float angleFromVertical = offset * maxBounceAngle;
            float angleInRadians = (float) Math.toRadians(angleFromVertical);
            
            // Calculate velocity (upward direction)
            float speed = 350f * speedMultiplier;
            float vx = speed * (float) Math.sin(angleInRadians);
            float vy = speed * (float) Math.cos(angleInRadians);
            
            // Ensure ball always goes upward
            vy = Math.abs(vy);
            
            velocity.set(vx, vy);
            normalizeVelocity(speed);
            setStuckToPaddle(false);
        }
    }
    
    // ===== Combo System =====
    
    /**
     * Increments the combo counter when a brick is destroyed.
     */
    public void incrementCombo() {
        comboCount++;
    }
    
    /**
     * Gets the current combo count.
     *
     * @return number of consecutive brick hits
     */
    public int getComboCount() {
        return comboCount;
    }
    
    /**
     * Resets the combo counter to zero.
     * Should be called when ball is lost or misses paddle.
     */
    public void resetCombo() {
        comboCount = 0;
    }
    
    /**
     * Sets the combo count directly.
     *
     * @param comboCount the combo count to set
     */
    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }
    
    // ===== Speed Multiplier =====
    
    /**
     * Sets the speed multiplier for dynamic speed changes.
     * Base speed will be multiplied by this value.
     *
     * @param speedMultiplier the multiplier (1.0 = normal speed)
     */
    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        // Re-normalize velocity with new speed
        if (active && !isStuckToPaddle) {
            float currentSpeed = getSpeed();
            if (currentSpeed > 0) {
                normalizeVelocity(350f * speedMultiplier);
            }
        }
    }
    
    /**
     * Gets the current speed multiplier.
     *
     * @return the speed multiplier
     */
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
}
