package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.managers.Assets;

public class Ball extends MovableObject {
    private static final Logger log = LoggerFactory.getLogger(Ball.class);
    private Vector2 velocity;
    private float radius;
    private Texture texture;
    private boolean active;

    public Ball(float x, float y, float radius) {
        super((int)x, (int)y, (int)radius * 2, (int)radius * 2);
        this.velocity = new Vector2(300, 300); // Initial velocity
        this.radius = radius;
        this.active = false;
        this.texture = Assets.getAsset("textures/arkanoid/normal_ball.png", Texture.class);
    }

    public void update(float delta) {
        if (active) {
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

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
