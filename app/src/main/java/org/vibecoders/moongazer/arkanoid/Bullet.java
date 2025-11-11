package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import org.vibecoders.moongazer.managers.Assets;

public class Bullet extends MovableObject {
    private Texture texture;
    private float speedY = 700f;
    private boolean active = true;

    public Bullet(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.texture = Assets.getAsset("textures/arkanoid/laser_bullet.png", Texture.class);
    }

    @Override
    public void update(float delta) {
        bounds.y += speedY * delta;
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
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