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
    private int durability; // Durability level: -1 = unbreakable, 1+ = hits needed to destroy
    private int maxDurability; // Store original durability for visual feedback

    public Brick(float x, float y, float width, float height, BrickType type) {
        super((int)x, (int)y, (int)width, (int)height);
        this.type = type;
        this.destroyed = false;
        this.durability = (type == BrickType.UNBREAKABLE) ? -1 : 1;
        this.maxDurability = this.durability;
        loadTexture();
    }
    
    /**
     * Constructor with custom durability level.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @param type Brick type
     * @param durability Durability level (-1 for unbreakable, 1+ for breakable)
     */
    public Brick(float x, float y, float width, float height, BrickType type, int durability) {
        super((int)x, (int)y, (int)width, (int)height);
        this.type = type;
        this.destroyed = false;
        this.durability = durability;
        this.maxDurability = durability;
        loadTexture();
    }
    
    /**
     * Loads the appropriate texture based on brick type and durability.
     */
    private void loadTexture() {
        switch (type) {
            case UNBREAKABLE:
                texture = Assets.getAsset("textures/arkanoid/bricks/unbreakable_brick.png", Texture.class);
                break;
            case BREAKABLE:
                // Load texture based on durability level
                if (durability >= 3) {
                    texture = Assets.getAsset("textures/arkanoid/bricks/breakable_brick_lv3.png", Texture.class);
                } else if (durability == 2) {
                    texture = Assets.getAsset("textures/arkanoid/bricks/breakable_brick_lv2.png", Texture.class);
                } else {
                    texture = Assets.getAsset("textures/arkanoid/bricks/breakable_brick_lv1.png", Texture.class);
                }
                break;
        }
    }

    /**
     * Handles a hit on the brick.
     * Decreases durability and updates texture.
     * Unbreakable bricks (durability = -1) are not affected.
     */
    public void hit() {
        if (durability == -1) {
            // Unbreakable brick
            return;
        }
        
        durability--;
        
        if (durability <= 0) {
            destroyed = true;
        } else {
            // Update texture to show reduced durability
            loadTexture();
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
    
    /**
     * Gets the current durability level.
     * @return -1 for unbreakable, 0+ for remaining hits
     */
    public int getDurability() {
        return durability;
    }
    
    /**
     * Gets the maximum durability level (original).
     * @return -1 for unbreakable, 1+ for max hits
     */
    public int getMaxDurability() {
        return maxDurability;
    }
    
    /**
     * Gets the durability percentage for visual effects.
     * @return 1.0 for full durability, 0.0 for destroyed, -1 for unbreakable
     */
    public float getDurabilityPercentage() {
        if (durability == -1) {
            return -1f; // Unbreakable
        }
        if (maxDurability <= 0) {
            return 0f;
        }
        return (float) durability / maxDurability;
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
