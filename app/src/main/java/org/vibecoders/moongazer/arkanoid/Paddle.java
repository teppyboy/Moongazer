package org.vibecoders.moongazer.arkanoid;

import org.vibecoders.moongazer.Settings;
import org.vibecoders.moongazer.managers.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Paddle extends MovableObject {
    private Texture texture;
    private float speed = 500f;
    private float targetX;
    private float smoothingFactor = 0.25f;
    private boolean isSticky = false;
    private float originalY;
    private float currentYOffset = 0f;
    private float targetYOffset = 0f;
    private static final float MAX_Y_OFFSET = 8f;
    private static final float BOUNCE_SPEED = 12f;
    private boolean aiMoveLeft = false;
    private boolean aiMoveRight = false;

    // Bullet functionality
    private boolean bulletEnabled = false;
    private List<Bullet> bullets = new ArrayList<>();
    private float bulletCooldown = 0f;
    private static final float BULLET_COOLDOWN_TIME = 0.3f;
    private boolean spaceWasPressed = false;

    /**
     * Constructs a new Paddle object.
     * @param x X position
     * @param y Y position
     * @param width Width of the paddle
     * @param height Height of the paddle
     */
    public Paddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.targetX = x;
        this.originalY = y;
        this.texture = Assets.getAsset("textures/arkanoid/paddle.png", Texture.class);
    }

    /**
     * Updates the paddle's state with full screen width.
     * @param delta Time elapsed since last update
     * @param screenWidth Width of the screen
     */
    public void update(float delta, int screenWidth) {
        update(delta, 0, screenWidth);
    }

    /**
     * Updates the paddle's state with specified movement boundaries.
     * @param delta Time elapsed since last update
     * @param minX Minimum X position
     * @param maxX Maximum X position
     */
    public void update(float delta, float minX, float maxX) {
        boolean keyboardUsed = false;
        if (Gdx.input.isKeyPressed(Settings.getKeybind("p1_left")) || aiMoveLeft) {
            bounds.x -= speed * delta;
            targetX = bounds.x;
            keyboardUsed = true;
        }
        if (Gdx.input.isKeyPressed(Settings.getKeybind("p1_right")) || aiMoveRight) {
            bounds.x += speed * delta;
            targetX = bounds.x;
            keyboardUsed = true;
        }
        if (!keyboardUsed && Gdx.input.isTouched()) {
            float mouseX = Gdx.input.getX();
            targetX = mouseX - bounds.width / 2f;
            bounds.x = MathUtils.lerp(bounds.x, targetX, 1f - smoothingFactor);
        }
        bounds.x = MathUtils.clamp(bounds.x, minX, maxX - bounds.width);
        targetX = MathUtils.clamp(targetX, minX, maxX - bounds.width);
        updateBounceEffect(delta);

        if (bulletEnabled) {
            updateBullets(delta);
            handleBulletShooting();
        }
    }

    /**
     * Updates all bullets fired by the paddle.
     * @param delta Time elapsed since last update
     */
    private void updateBullets(float delta) {
        if (bulletCooldown > 0) {
            bulletCooldown -= delta;
        }

        for (Bullet bullet : bullets) {
            bullet.update(delta);
        }
    }

    /**
     * Handles the shooting of bullets when space is pressed.
     */
    private void handleBulletShooting() {
        boolean spacePressed = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (spacePressed && !spaceWasPressed && bulletCooldown <= 0) {
            shootBullet();
            bulletCooldown = BULLET_COOLDOWN_TIME;
        }

        spaceWasPressed = spacePressed;
    }

    /**
     * Shoots two bullets from the paddle.
     */
    private void shootBullet() {
        float leftX = bounds.x + bounds.width * 0.25f - 2;
        float rightX = bounds.x + bounds.width * 0.75f - 2;
        float bulletY = bounds.y + bounds.height;

        bullets.add(new Bullet(leftX, bulletY, 5, 19));
        bullets.add(new Bullet(rightX, bulletY, 5, 19));
    }

    /**
     * Updates the bounce effect animation when ball hits paddle.
     * @param delta Time elapsed since last update
     */
    private void updateBounceEffect(float delta) {
        if (targetYOffset > 0) {
            targetYOffset = Math.max(0, targetYOffset - BOUNCE_SPEED * delta);
        }
        float diff = targetYOffset - currentYOffset;
        currentYOffset += diff * 10f * delta;
        bounds.y = originalY - currentYOffset;
    }

    /**
     * Triggers bounce effect when ball hits the paddle.
     * @param ballVelocityY Vertical velocity of the ball
     */
    public void onBallHit(float ballVelocityY) {
        float impactStrength = Math.abs(ballVelocityY) / 350f;
        impactStrength = MathUtils.clamp(impactStrength, 0.3f, 1.0f);
        targetYOffset = MAX_Y_OFFSET * impactStrength;
    }

    /**
     * Renders the paddle to the screen.
     * @param batch SpriteBatch used for rendering
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);

        if (bulletEnabled) {
            for (Bullet bullet : bullets) {
                bullet.render(batch);
            }
        }
    }

    /**
     * Gets the bounding rectangle of the paddle.
     * @return Rectangle representing the paddle's bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Gets the X coordinate of the paddle's center.
     * @return Center X position
     */
    public float getCenterX() {
        return bounds.x + bounds.width / 2;
    }

    /**
     * Gets the horizontal velocity of the paddle.
     * @return X velocity
     */
    public float getVelocityX() {
        return (targetX - bounds.x);
    }

    /**
     * Extends the paddle width by a specified amount.
     * @param amount Amount to extend by
     */
    public void extend(float amount) {
        bounds.width += amount;
        bounds.x -= amount / 2f;
    }

    /**
     * Extends the paddle width by default amount (100 pixels).
     */
    public void extend() {
        extend(100f);
    }

    /**
     * Shrinks the paddle width by a specified amount.
     * @param amount Amount to shrink by
     */
    public void shrink(float amount) {
        bounds.width -= amount;
        bounds.x += amount / 2f;
        if (bounds.width < 50f) {
            bounds.width = 50f;
        }
    }

    /**
     * Shrinks the paddle width by default amount (100 pixels).
     */
    public void shrink() {
        shrink(100f);
    }

    /**
     * Sets whether the paddle is sticky (catches balls).
     * @param sticky true for sticky paddle, false otherwise
     */
    public void setSticky(boolean sticky) {
        this.isSticky = sticky;
    }

    /**
     * Checks if the paddle is sticky.
     * @return true if sticky, false otherwise
     */
    public boolean isSticky() {
        return isSticky;
    }

    /**
     * Sets the original Y position for bounce effect calculations.
     * @param y Original Y position
     */
    public void setOriginalY(float y) {
        this.originalY = y;
        this.currentYOffset = 0f;
        this.targetYOffset = 0f;
        this.bounds.y = y;
    }

    /**
     * Controls AI movement to the left.
     * @param move true to move left, false to stop
     */
    public void moveLeft(boolean move) {
        this.aiMoveLeft = move;
    }

    /**
     * Controls AI movement to the right.
     * @param move true to move right, false to stop
     */
    public void moveRight(boolean move) {
        this.aiMoveRight = move;
    }
    
    /**
     * Enables or disables bullet shooting functionality.
     * @param enabled true to enable bullets, false to disable
     */
    public void setBulletEnabled(boolean enabled) {
        this.bulletEnabled = enabled;
        if (!enabled) {
            bullets.clear();
        }
    }

    /**
     * Checks if bullet shooting is enabled.
     * @return true if bullets are enabled, false otherwise
     */
    public boolean isBulletEnabled() {
        return bulletEnabled;
    }

    /**
     * Gets the list of active bullets.
     * @return List of bullets
     */
    public List<Bullet> getBullets() {
        return bullets;
    }

    /**
     * Removes bullets that are off screen or inactive.
     * @param screenHeight Height of the screen for boundary checking
     */
    public void cleanupBullets(float screenHeight) {
        bullets.removeIf(bullet -> bullet.isOffScreen(screenHeight) || !bullet.isActive());
    }
}
