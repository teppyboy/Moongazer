package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class BulletPaddle extends PowerUp {
    /**
     * Constructs a new BulletPaddle power-up.
     * @param x X position
     * @param y Y position
     * @param width Width of the power-up
     * @param height Height of the power-up
     */
    public BulletPaddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk.png");
    }

    /**
     * Applies the bullet shooting effect to the paddle.
     * @param arkanoid The game instance
     */
    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getPaddle().setBulletEnabled(true);
    }

    /**
     * Removes the bullet shooting effect from the paddle.
     * @param arkanoid The game instance
     */
    @Override
    public void removeEffect(Arkanoid arkanoid) {
        arkanoid.getPaddle().setBulletEnabled(false);
    }

    /**
     * Gets the duration of the power-up effect.
     * @return Duration in milliseconds
     */
    @Override
    public int getDuration() {
        return 12000;
    }

    /**
     * Gets the name of the power-up.
     * @return Power-up name
     */
    @Override
    public String getName() {
        return "Bullet Paddle";
    }
}
