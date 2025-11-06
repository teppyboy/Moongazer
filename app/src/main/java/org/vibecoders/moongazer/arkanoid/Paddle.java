package org.vibecoders.moongazer.arkanoid;

import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Paddle extends MovableObject {
    private Texture texture;
    private float speed = 500f;

    // Smooth mouse movement
    private float targetX;
    private float smoothingFactor = 0.25f; // 0 = instant, 1 = no movement
    
    // Sticky paddle feature
    private boolean isSticky = false;

    public Paddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.targetX = x;
        this.texture = Assets.getAsset("textures/arkanoid/paddle.png", Texture.class);
    }

    public void update(float delta, int screenWidth) {
        update(delta, 0, screenWidth);
    }

    public void update(float delta, float minX, float maxX) {
        boolean keyboardUsed = false;

        // Keyboard controls - direct movement
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            bounds.x -= speed * delta;
            targetX = bounds.x; // Sync target with actual position
            keyboardUsed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            bounds.x += speed * delta;
            targetX = bounds.x;
            keyboardUsed = true;
        }

        // Mouse control with smoothing (only if keyboard not used)
        if (!keyboardUsed && Gdx.input.isTouched()) {
            float mouseX = Gdx.input.getX();
            targetX = mouseX - bounds.width / 2f;

            // Smooth interpolation - lerp toward target
            bounds.x = MathUtils.lerp(bounds.x, targetX, 1f - smoothingFactor);
        }

        // Keep paddle within gameplay area bounds
        bounds.x = MathUtils.clamp(bounds.x, minX, maxX - bounds.width);
        targetX = MathUtils.clamp(targetX, minX, maxX - bounds.width);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getCenterX() {
        return bounds.x + bounds.width / 2;
    }

    // Get current velocity (for advanced collision)
    public float getVelocityX() {
        return (targetX - bounds.x);
    }
    
    /**
     * Extends the paddle width
     * Increases paddle width by specified amount.
     * 
     * @param amount pixels to extend (default: 100)
     */
    public void extend(float amount) {
        bounds.width += amount;
        // Adjust position to keep paddle centered
        bounds.x -= amount / 2f;
    }
    
    /**
     * Extends the paddle width by default amount.
     */
    public void extend() {
        extend(100f);
    }
    
    /**
     * Shrinks the paddle width 
     * Decreases paddle width by specified amount.
     * 
     * @param amount pixels to shrink (default: 100)
     */
    public void shrink(float amount) {
        bounds.width -= amount;
        // Adjust position to keep paddle centered
        bounds.x += amount / 2f;
        
        // Minimum paddle width
        if (bounds.width < 50f) {
            bounds.width = 50f;
        }
    }
    
    /**
     * Shrinks the paddle width by default amount.
     */
    public void shrink() {
        shrink(100f);
    }
    
    /**
     * Sets the sticky paddle mode
     * When enabled, ball sticks to paddle on contact.
     * 
     * @param sticky true to enable sticky mode, false to disable
     */
    public void setSticky(boolean sticky) {
        this.isSticky = sticky;
    }
    
    /**
     * Gets whether paddle is in sticky mode.
     * 
     * @return true if sticky mode is active
     */
    public boolean isSticky() {
        return isSticky;
    }
}