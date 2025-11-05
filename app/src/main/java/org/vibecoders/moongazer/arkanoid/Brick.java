package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.vibecoders.moongazer.managers.Assets;

public class Brick extends GameObject {
    public enum BrickType {
        BREAKABLE,
        UNBREAKABLE
    }

    private BrickType type;
    private Texture texture;
    private boolean destroyed;
    private int hits; // For multi-hit bricks in the future

    public Brick(float x, float y, float width, float height, BrickType type) {
        super((int)x, (int)y, (int)width, (int)height);
        this.type = type;
        this.destroyed = false;
        this.hits = (type == BrickType.UNBREAKABLE) ? -1 : 1;
        switch (type) {
            case UNBREAKABLE:
                texture = Assets.getAsset("textures/arkanoid/bricks/unbreakable_brick.png", Texture.class);
                break;
            case BREAKABLE:
                texture = Assets.getAsset("textures/arkanoid/bricks/breakable_brick_lv1.png", Texture.class);
                break;
        }
    }

    public void hit() {
        if (type == BrickType.UNBREAKABLE) {
            return;
        }
        hits--;
        if (hits <= 0) {
            destroyed = true;
        }
    }

    public void render(SpriteBatch batch) {
        if (!destroyed && texture != null) {
            // Draw texture scaled to brick bounds
            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public BrickType getType() {
        return type;
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
    public void update(float deltaTime) {
        // Bricks are stationary; no update needed
    }
}
