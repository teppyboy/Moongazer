package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public abstract class PowerUp extends MovableObject {
    protected Texture texture;
    public float x;
    public float y;
    public float width;
    public float height;
    public float speedY = -100;

    protected PowerUp(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void applyEffect(Arkanoid arkanoid);
    public abstract void removeEffect(Arkanoid arkanoid);
    public abstract int getDuration();
    public abstract String getName();

    protected void loadTexture(String texturePath) {
        texture = Assets.getAsset(texturePath, Texture.class);
    }

    public void update(float delta) {
        y += speedY * delta;
    }

    public void render(Batch batch) {
        if (texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public Texture getTexture() {
        return texture;
    }
}
