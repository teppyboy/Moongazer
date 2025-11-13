package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.vibecoders.moongazer.managers.Assets;

public class Bullet extends MovableObject {
    private final Texture texture;
    private boolean active = true;

    /**
     * Constructs a new Bullet object.
     * @param x X position
     * @param y Y position
     * @param width Width of the bullet
     * @param height Height of the bullet
     */
    public Bullet(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.texture = Assets.getAsset("textures/arkanoid/laser_bullet.png", Texture.class);
        setVelocity(0, 700f);
    }

    /**
     * Renders the bullet to the screen.
     * @param batch SpriteBatch used for rendering
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /**
     * Checks if the bullet is currently active.
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active state of the bullet.
     * @param active true to activate, false to deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if the bullet has gone off screen.
     * @param screenHeight Height of the screen
     * @return true if off screen, false otherwise
     */
    public boolean isOffScreen(float screenHeight) {
        return bounds.y > screenHeight;
    }
}