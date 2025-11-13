package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class SuperBall extends PowerUp {
    /**
     * Constructs a new SuperBall power-up.
     * @param x X position
     * @param y Y position
     * @param width Width of the power-up
     * @param height Height of the power-up
     */
    public SuperBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk1.png");
    }

    /**
     * Applies the super ball effect, allowing ball to pass through bricks.
     * @param arkanoid The game instance
     */
    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSuperBall(true);
    }

    /**
     * Removes the super ball effect, returning to normal behavior.
     * @param arkanoid The game instance
     */
    @Override
    public void removeEffect(Arkanoid arkanoid) {
        // Check if ball still exists before removing effect
        if (arkanoid.getBall() != null) {
            arkanoid.getBall().setSuperBall(false);
        }
    }

    /**
     * Gets the duration of the power-up effect.
     * @return Duration in milliseconds
     */
    @Override
    public int getDuration() {
        return 15000;
    }

    /**
     * Gets the name of the power-up.
     * @return Power-up name
     */
    @Override
    public String getName() {
        return "Super Ball";
    }
}
