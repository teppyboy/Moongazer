package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class SlowBall extends PowerUp {
    /**
     * Constructs a new SlowBall power-up.
     * @param x X position
     * @param y Y position
     * @param width Width of the power-up
     * @param height Height of the power-up
     */
    public SlowBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk4.png");
    }

    /**
     * Applies the slow ball effect, halving ball speed.
     * @param arkanoid The game instance
     */
    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSpeedMultiplier(0.5f);
    }

    /**
     * Removes the slow ball effect, returning to normal speed.
     * @param arkanoid The game instance
     */
    @Override
    public void removeEffect(Arkanoid arkanoid) {
        if (arkanoid.getBall() != null) {
            arkanoid.getBall().setSpeedMultiplier(1.0f);
        }
    }

    /**
     * Gets the duration of the power-up effect.
     * @return Duration in milliseconds
     */
    @Override
    public int getDuration() {
        return 5000;
    }

    /**
     * Gets the name of the power-up.
     * @return Power-up name
     */
    @Override
    public String getName() {
        return "speed x0.5";
    }
}
