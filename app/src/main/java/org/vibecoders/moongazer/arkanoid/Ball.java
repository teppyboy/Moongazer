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
    private float previousY;
    private boolean isHeavyBall = false;
    private boolean isSuperBall = false;
    private boolean isStuckToPaddle = false;
    private float stuckOffsetX = 0f;
    private int comboCount = 0;
    private float speedMultiplier = 1.0f;
    private Texture normalTexture;
    private Texture enchantedTexture;

    /**
     * Constructs a new Ball object.
     * @param x Initial X position
     * @param y Initial Y position
     * @param radius Radius of the ball
     */
    public Ball(float x, float y, float radius) {
        super((int)x, (int)y, (int)radius * 2, (int)radius * 2);
        this.velocity = new Vector2(300, 300);
        this.radius = radius;
        this.active = false;
        this.normalTexture = Assets.getAsset("textures/arkanoid/normal_ball.png", Texture.class);
        this.enchantedTexture = Assets.getAsset("textures/arkanoid/enchanted_ball.png", Texture.class);
        this.texture = normalTexture;
    }

    /**
     * Updates the ball's position based on velocity and delta time.
     * @param delta Time elapsed since last update in seconds
     */
    public void update(float delta) {
        previousY = bounds.y;
        if (active && !isStuckToPaddle) {
            bounds.x += velocity.x * delta;
            bounds.y += velocity.y * delta;
        }
    }

    /**
     * Renders the ball to the screen.
     * @param batch SpriteBatch used for rendering
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x - radius, bounds.y - radius, radius * 2, radius * 2);
    }

    /**
     * Launches the ball into motion.
     */
    public void launch() {
        active = true;
    }

    /**
     * Resets the ball to a new position with default velocity.
     * @param x New X position
     * @param y New Y position
     */
    public void reset(float x, float y) {
        bounds.set(x, y, radius * 2, radius * 2);
        velocity.set(300, 300);
        active = false;
    }

    /**
     * Reverses the horizontal velocity of the ball.
     */
    public void reverseX() {
        velocity.x = -velocity.x;
    }

    /**
     * Reverses the vertical velocity of the ball.
     */
    public void reverseY() {
        velocity.y = -velocity.y;
    }

    /**
     * Sets the velocity of the ball.
     * @param x Horizontal velocity
     * @param y Vertical velocity
     */
    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    /**
     * Gets the current velocity vector of the ball.
     * @return Velocity vector
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Gets the radius of the ball.
     * @return Ball radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Checks if the ball is currently active.
     * @return true if ball is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the X coordinate of the ball's center.
     * @return Center X position
     */
    public float getCenterX() {
        return bounds.x + radius;
    }

    /**
     * Gets the Y coordinate of the ball's center.
     * @return Center Y position
     */
    public float getCenterY() {
        return bounds.y + radius;
    }

    /**
     * Gets the current speed of the ball.
     * @return Ball speed (magnitude of velocity)
     */
    public float getSpeed() {
        return velocity.len();
    }

    /**
     * Normalizes the ball's velocity to a target speed.
     * @param targetSpeed Desired speed magnitude
     */
    public void normalizeVelocity(float targetSpeed) {
        float currentSpeed = getSpeed();
        if (currentSpeed > 0) {
            velocity.scl(targetSpeed / currentSpeed);
        }
    }

    /**
     * Gets the ball's previous Y position from last frame.
     * @return Previous Y position
     */
    public float getPreviousY() {
        return previousY;
    }

    /**
     * Sets whether the ball is a heavy ball.
     * @param heavyBall true for heavy ball, false otherwise
     */
    public void setHeavyBall(boolean heavyBall) {
        this.isHeavyBall = heavyBall;
    }

    /**
     * Checks if the ball is a heavy ball.
     * @return true if heavy ball, false otherwise
     */
    public boolean isHeavyBall() {
        return isHeavyBall;
    }

    /**
     * Sets whether the ball is a super ball.
     * @param superBall true for super ball, false otherwise
     */
    public void setSuperBall(boolean superBall) {
        this.isSuperBall = superBall;
        this.texture = superBall ? enchantedTexture : normalTexture;
    }

    /**
     * Checks if the ball is a super ball.
     * @return true if super ball, false otherwise
     */
    public boolean isSuperBall() {
        return isSuperBall;
    }

    /**
     * Sets whether the ball is stuck to the paddle.
     * @param stuck true to stick to paddle, false to release
     */
    public void setStuckToPaddle(boolean stuck) {
        this.isStuckToPaddle = stuck;
    }

    /**
     * Checks if the ball is stuck to the paddle.
     * @return true if stuck, false otherwise
     */
    public boolean isStuckToPaddle() {
        return isStuckToPaddle;
    }

    /**
     * Sets the horizontal offset from paddle center when stuck.
     * @param offsetX Offset from paddle center
     */
    public void setStuckOffsetX(float offsetX) {
        this.stuckOffsetX = offsetX;
    }

    /**
     * Gets the horizontal offset from paddle center when stuck.
     * @return Offset from paddle center
     */
    public float getStuckOffsetX() {
        return stuckOffsetX;
    }

    /**
     * Resets the ball to the center of the paddle.
     * @param paddle The paddle to center on
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
     * Launches the ball from the paddle with an angle based on offset.
     * @param paddle The paddle launching the ball
     * @param maxBounceAngle Maximum launch angle in degrees
     */
    public void launchFromPaddle(Paddle paddle, float maxBounceAngle) {
        if (isStuckToPaddle) {
            float paddleHalfWidth = paddle.getBounds().width / 2f;
            float offset = Math.max(-1.0f, Math.min(1.0f, stuckOffsetX / paddleHalfWidth));
            float angleFromVertical = offset * maxBounceAngle;
            float angleInRadians = (float) Math.toRadians(angleFromVertical);
            float speed = 350f * speedMultiplier;
            float vx = speed * (float) Math.sin(angleInRadians);
            float vy = Math.abs(speed * (float) Math.cos(angleInRadians));
            velocity.set(vx, vy);
            normalizeVelocity(speed);
            setStuckToPaddle(false);
        }
    }

    /**
     * Increments the combo counter.
     */
    public void incrementCombo() {
        comboCount++;
    }

    /**
     * Gets the current combo count.
     * @return Current combo count
     */
    public int getComboCount() {
        return comboCount;
    }

    /**
     * Resets the combo counter to zero.
     */
    public void resetCombo() {
        comboCount = 0;
    }

    /**
     * Sets the combo count to a specific value.
     * @param comboCount New combo count value
     */
    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    /**
     * Sets the speed multiplier for the ball.
     * @param speedMultiplier Speed multiplier (1.0 is normal speed)
     */
    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        if (active && !isStuckToPaddle) {
            float currentSpeed = getSpeed();
            if (currentSpeed > 0) {
                normalizeVelocity(350f * speedMultiplier);
            }
        }
    }

    /**
     * Gets the current speed multiplier.
     * @return Current speed multiplier
     */
    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
}
