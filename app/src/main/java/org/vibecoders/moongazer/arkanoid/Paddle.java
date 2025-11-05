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
    private boolean useSmoothing = true;

    public Paddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.targetX = x;
        this.texture = Assets.getAsset("textures/arkanoid/paddle.png", Texture.class);
    }

    public void update(float delta, int screenWidth) {
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

            if (useSmoothing) {
                // Smooth interpolation - lerp toward target
                bounds.x = MathUtils.lerp(bounds.x, targetX, 1f - smoothingFactor);
            } else {
                // Direct movement (old behavior)
                bounds.x = targetX;
            }
        }

        // Keep paddle within screen bounds
        bounds.x = MathUtils.clamp(bounds.x, 0, screenWidth - bounds.width);
        targetX = MathUtils.clamp(targetX, 0, screenWidth - bounds.width);
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

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}