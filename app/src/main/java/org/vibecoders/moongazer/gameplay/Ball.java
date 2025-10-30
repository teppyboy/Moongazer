package org.vibecoders.moongazer.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.managers.Assets;

public class Ball {
    private static final Logger log = LoggerFactory.getLogger(Ball.class);
    private Vector2 position;
    private Vector2 velocity;
    private float radius;
    private Texture texture;
    private boolean active;

    public Ball(float x, float y, float radius) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(300, 300); // Initial velocity
        this.radius = radius;
        this.active = false;

        loadTexture();
    }

    private void loadTexture() {
        try {
            // Load ball texture directly
            com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.internal("arkanoid_assets/normal_ball.png");
            if (fileHandle.exists()) {
                log.info("Ball texture file exists, attempting to load...");
                texture = new Texture(fileHandle);
                log.info("Ball texture loaded successfully!");
            } else {
                log.warn("Ball texture file not found, using fallback");
                createFallbackTexture();
            }
        } catch (Exception e) {
            log.error("Could not load ball texture: {}, using fallback", e.getMessage());
            createFallbackTexture();
        }
    }

    private void createFallbackTexture() {
        Pixmap pixmap = new Pixmap((int)(radius * 2), (int)(radius * 2), Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f);
        pixmap.fillCircle((int)radius, (int)radius, (int)radius);
        texture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(float delta) {
        if (active) {
            position.x += velocity.x * delta;
            position.y += velocity.y * delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - radius, position.y - radius, radius * 2, radius * 2);
    }

    public void launch() {
        active = true;
    }

    public void reset(float x, float y) {
        position.set(x, y);
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

    public Vector2 getPosition() {
        return position;
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

    public Rectangle getBounds() {
        return new Rectangle(position.x - radius, position.y - radius, radius * 2, radius * 2);
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
