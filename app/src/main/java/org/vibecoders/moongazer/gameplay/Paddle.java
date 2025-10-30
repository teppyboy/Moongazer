package org.vibecoders.moongazer.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Paddle {
    private Rectangle bounds;
    private Texture texture;
    private float speed = 500f;

    // Smooth mouse movement
    private float targetX;
    private float smoothingFactor = 0.25f; // 0 = instant, 1 = no movement
    private boolean useSmoothing = true;

    public Paddle(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
        this.targetX = x;
        loadTexture();
    }

    private void loadTexture() {
        try {
            com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.internal("arkanoid_assets/paddle.png");
            if (fileHandle.exists()) {
                texture = new Texture(fileHandle);
            } else {
                createFallbackTexture();
            }
        } catch (Exception e) {
            createFallbackTexture();
        }
    }

    private void createFallbackTexture() {
        Pixmap pixmap = new Pixmap((int)bounds.width, (int)bounds.height, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.9f, 0.9f, 0.9f, 1f);
        pixmap.fill();

        // Add border
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.drawRectangle(0, 0, (int)bounds.width, (int)bounds.height);

        texture = new Texture(pixmap);
        pixmap.dispose();
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