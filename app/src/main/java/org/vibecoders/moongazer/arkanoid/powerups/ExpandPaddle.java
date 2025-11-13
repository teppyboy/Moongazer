package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ExpandPaddle extends PowerUp {
    /**
     * Constructs a new ExpandPaddle power-up.
     * @param x X position
     * @param y Y position
     * @param width Width of the power-up
     * @param height Height of the power-up
     */
    public ExpandPaddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk5.png");
    }

    /**
     * Applies the paddle expansion effect.
     * @param arkanoid The game instance
     */
    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getPaddle().setSize((int) (arkanoid.getPaddle().getWidth() * 2), arkanoid.getPaddle().getHeight());
    }

    /**
     * Removes the paddle expansion effect.
     * @param arkanoid The game instance
     */
    @Override
    public void removeEffect(Arkanoid arkanoid) {
        if (arkanoid.getPaddle() != null) {
            arkanoid.getPaddle().setSize((int) (arkanoid.getPaddle().getWidth() / 2), arkanoid.getPaddle().getHeight());
        }
    }

    /**
     * Gets the duration of the power-up effect.
     * @return Duration in milliseconds
     */
    @Override
    public int getDuration() {
        return 10000;
    }

    /**
     * Gets the name of the power-up.
     * @return Power-up name
     */
    @Override
    public String getName() {
        return "Expand Paddle";
    }
}
