package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.vibecoders.moongazer.managers.Assets;

public class Bullet extends MovableObject {
    private final Texture texture;
    private boolean active = true;

    public Bullet(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.texture = Assets.getAsset("textures/arkanoid/laser_bullet.png", Texture.class);
        setVelocity(0, 700f);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isOffScreen(float screenHeight) {
        return bounds.y > screenHeight;
    }
}