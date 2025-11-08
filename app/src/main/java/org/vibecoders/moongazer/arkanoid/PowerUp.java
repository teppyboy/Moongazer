package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.arkanoid.PowerUps.PowerUpType;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class PowerUp extends MovableObject{
    private String path;
    private Texture texture;
    public float x;
    public float y;
    public float width;
    public float height;
    public PowerUpType type;
    public float speedY = -100;

    /**
     * Creates a stationary movable object.
     *
     * @param x      initial X position
     * @param y      initial Y position
     * @param width  width in pixels
     * @param height height in pixels
     */
    public PowerUp(String path, float x, float y, float width, float height, PowerUpType effect) {
        super(x, y, width, height);
        texture = Assets.getAsset(path, Texture.class);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = effect;
    }

    public void applyEffect(Arkanoid arkanoid) {
        type.applyEffect(arkanoid);
    }

    public void update(float delta) {
        y += speedY * delta;
    }

    public void render(Batch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public PowerUpType getType() {
        return type;
    }
}
