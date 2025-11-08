package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.vibecoders.moongazer.managers.Assets;

public class Brick extends GameObject {
    public enum BrickType {
        BREAKABLE,
        UNBREAKABLE
    }

    public enum PowerUpType {
        NONE,           // Normal brick, no power-up
        EXPAND_PADDLE,  // expandpaddlebrick.png
        EXTRA_LIFE,     // extralifebrick.png
        FAST_BALL,      // fastballbrick.png
        SLOW_BALL,      // slowballbrick.png
        MULTI_BALL,     // multiballbrick.png
        SUPER_BALL,     // superballbrick.png
        LASER,          // laserbrick.png (future implementation)
        EXPLOSIVE       // explosivebrick.png (future implementation)
    }

    private BrickType type;
    private PowerUpType powerUpType;
    private Texture texture;
    private boolean destroyed;
    private int durability; // Durability level: -1 = unbreakable, 1+ = hits needed to destroy
    private int maxDurability; // Store original durability for visual feedback

    public Brick(float x, float y, float width, float height, BrickType type) {
        super((int)x, (int)y, (int)width, (int)height);
        this.type = type;
        this.powerUpType = PowerUpType.NONE;
        this.destroyed = false;
        this.durability = (type == BrickType.UNBREAKABLE) ? -1 : 1;
        this.maxDurability = this.durability;
        loadTexture();
    }
    
    /**
     * Constructor with custom durability level.
     */
    public Brick(float x, float y, float width, float height, BrickType type, int durability) {
        super((int)x, (int)y, (int)width, (int)height);
        this.type = type;
        this.powerUpType = PowerUpType.NONE;
        this.destroyed = false;
        this.durability = durability;
        this.maxDurability = durability;
        loadTexture();
    }

    /**
     * Constructor with PowerUpType for special bricks that drop power-ups.
     */
    public Brick(float x, float y, float width, float height, BrickType type, PowerUpType powerUpType) {
        super((int)x, (int)y, (int)width, (int)height);
        this.type = type;
        this.powerUpType = powerUpType;
        this.destroyed = false;
        this.durability = (type == BrickType.UNBREAKABLE) ? -1 : 1;
        this.maxDurability = this.durability;
        loadTexture();
    }

    /**
     * Constructor with durability and PowerUpType.
     */
    public Brick(float x, float y, float width, float height, BrickType type, int durability, PowerUpType powerUpType) {
        super((int)x, (int)y, (int)width, (int)height);
        this.type = type;
        this.powerUpType = powerUpType;
        this.destroyed = false;
        this.durability = durability;
        this.maxDurability = durability;
        loadTexture();
    }
    
    /**
     * Loads the appropriate texture based on brick type, durability, and power-up type.
     */
    private void loadTexture() {
        // If this brick has a power-up, use the power-up brick texture
        if (powerUpType != PowerUpType.NONE && type == BrickType.BREAKABLE) {
            switch (powerUpType) {
                case EXPAND_PADDLE:
                    texture = Assets.getAsset("textures/arkanoid/bricks/expandpaddlebrick.png", Texture.class);
                    return;
                case EXTRA_LIFE:
                    texture = Assets.getAsset("textures/arkanoid/bricks/extralifebrick.png", Texture.class);
                    return;
                case FAST_BALL:
                    texture = Assets.getAsset("textures/arkanoid/bricks/fastballbrick.png", Texture.class);
                    return;
                case SLOW_BALL:
                    texture = Assets.getAsset("textures/arkanoid/bricks/slowballbrick.png", Texture.class);
                    return;
                case MULTI_BALL:
                    texture = Assets.getAsset("textures/arkanoid/bricks/multiballbrick.png", Texture.class);
                    return;
                case SUPER_BALL:
                    texture = Assets.getAsset("textures/arkanoid/bricks/superballbrick.png", Texture.class);
                    return;
                case LASER:
                    texture = Assets.getAsset("textures/arkanoid/bricks/laserbrick.png", Texture.class);
                    return;
                case EXPLOSIVE:
                    texture = Assets.getAsset("textures/arkanoid/bricks/explosivebrick.png", Texture.class);
                    return;
            }
        }

        // Normal brick textures
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
     * Gets the power-up type associated with this brick.
     * @return PowerUpType enum value
     */
    public PowerUpType getPowerUpType() {
        return powerUpType;
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
    
    public void update(float deltaTime) {
        // Bricks are stationary; no update needed
    }
}
