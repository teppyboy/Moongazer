package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;

public class Brick extends GameObject {
    public enum BrickType {
        BREAKABLE,
        UNBREAKABLE
    }

    public enum PowerUpType {
        NONE, EXPAND_PADDLE, EXTRA_LIFE, FAST_BALL, SLOW_BALL,
        MULTI_BALL, SUPER_BALL, LASER, EXPLOSIVE
    }

    private BrickType type;
    private PowerUpType powerUpType;
    private Texture texture;
    private boolean destroyed;
    private int durability;
    private int maxDurability;
    private boolean disappearing = false;
    private float disappearTimer = 0f;
    private float totalDisappearTime = 1f;
    private float shakeIntensity = 4f;
    private float alpha = 1f;
    private float originalX;

    // New fields for hit animation
    private boolean hitAnimating = false;
    private float hitAnimTimer = 0f;
    private float totalHitAnimTime = 0.6f;
    private float hitShakeIntensity = 3f;

    public Brick(float x, float y, float width, float height, BrickType type) {
        this(x, y, width, height, type, type == BrickType.UNBREAKABLE ? -1 : 1, PowerUpType.NONE);
    }

    public Brick(float x, float y, float width, float height, BrickType type, int durability) {
        this(x, y, width, height, type, durability, PowerUpType.NONE);
    }

    public Brick(float x, float y, float width, float height, BrickType type, PowerUpType powerUpType) {
        this(x, y, width, height, type, type == BrickType.UNBREAKABLE ? -1 : 1, powerUpType);
    }

    /**
     * Create a breakable brick with specific level (1, 2, or 3)
     * Level 1 = easiest (1 hit), Level 2 = medium (2 hits), Level 3 = hardest (3 hits)
     */
    public static Brick createBreakableBrick(float x, float y, float width, float height, int level) {
        return new Brick(x, y, width, height, BrickType.BREAKABLE, level, PowerUpType.NONE);
    }

    /**
     * Create a breakable brick with specific level and power-up
     */
    public static Brick createBreakableBrick(float x, float y, float width, float height, int level, PowerUpType powerUpType) {
        return new Brick(x, y, width, height, BrickType.BREAKABLE, level, powerUpType);
    }

    public Brick(float x, float y, float width, float height, BrickType type, int durability, PowerUpType powerUpType) {
        super((int)x, (int)y, (int)width, (int)height);
        this.type = type;
        this.powerUpType = powerUpType;
        this.destroyed = false;
        this.durability = durability;
        this.maxDurability = durability;
        this.originalX = x;
        loadTexture();
    }

    private void loadTexture() {
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
        switch (type) {
            case UNBREAKABLE:
                texture = Assets.getAsset("textures/arkanoid/bricks/unbreakable_brick.png", Texture.class);
                break;
            case BREAKABLE:
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

    public void hit() {
        Audio.playSfxBrickHit();
        if (durability == -1) return;
        durability--;

        if (durability <= 0) {
            startDisappearing();
            destroyed = true;
        } else {
            startHitAnimation();
            loadTexture();
        }
    }

    private void startDisappearing() {
        disappearing = true;
        disappearTimer = 0f;
        originalX = bounds.x;
    }

    private void startHitAnimation() {
        hitAnimating = true;
        hitAnimTimer = 0f;
        originalX = bounds.x;
        alpha = 1f;
    }

    public void render(SpriteBatch batch) {
        if (texture != null && (disappearing || hitAnimating || !destroyed)) {
            float oldColor = batch.getPackedColor();

            if (disappearing || hitAnimating) {
                batch.setColor(1f, 1f, 1f, alpha);
            }

            batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
            batch.setPackedColor(oldColor);
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public BrickType getType() {
        return type;
    }

    public PowerUpType getPowerUpType() {
        return powerUpType;
    }

    public int getDurability() {
        return durability;
    }

    public int getMaxDurability() {
        return maxDurability;
    }

    public float getDurabilityPercentage() {
        if (durability == -1) return -1f;
        if (maxDurability <= 0) return 0f;
        return (float) durability / maxDurability;
    }

    public void update(float deltaTime) {
        if (disappearing) {
            disappearTimer += deltaTime;
            float progress = disappearTimer / totalDisappearTime;
            if (progress < 1f) {
                float frequency = 30f + progress * 20f;
                float shakeOffset = (float) Math.sin(disappearTimer * frequency) * shakeIntensity * (1f - progress);
                bounds.x = originalX + shakeOffset;
                alpha = Math.max(0f, 1f - progress);
            } else {
                bounds.x = originalX;
                alpha = 0f;
                disappearing = false;
            }
        }

        if (hitAnimating) {
            hitAnimTimer += deltaTime;
            float progress = hitAnimTimer / totalHitAnimTime;
            if (progress < 1f) {
                float frequency = 40f;
                float shakeOffset = (float) Math.sin(hitAnimTimer * frequency) * hitShakeIntensity * (1f - progress);
                bounds.x = originalX + shakeOffset;

                if (progress < 0.5f) {
                    alpha = 1f - (progress * 2f * 0.5f);
                } else {
                    alpha = 0.5f + ((progress - 0.5f) * 2f * 0.5f);
                }
            } else {
                bounds.x = originalX;
                alpha = 1f;
                hitAnimating = false;
            }
        }
    }

    public boolean isDisappearing() {
        return disappearing;
    }

    public boolean isHitAnimating() {
        return hitAnimating;
    }

    public float getAlpha() {
        return alpha;
    }
}