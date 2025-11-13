package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class MultiBall extends PowerUp {
    /**
     * Constructs a new MultiBall power-up.
     * @param x X position
     * @param y Y position
     * @param width Width of the power-up
     * @param height Height of the power-up
     */
    public MultiBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk3.png");
    }

    /**
     * Applies the multi-ball effect, spawning additional balls.
     * @param arkanoid The game instance
     */
    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.spawnBalls(2);
    }

    /**
     * Removes the effect (does nothing for instant effects).
     * @param arkanoid The game instance
     */
    @Override
    public void removeEffect(Arkanoid arkanoid) {
    }

    /**
     * Gets the duration of the power-up effect.
     * @return -1 indicating permanent/instant effect
     */
    @Override
    public int getDuration() {
        return -1;
    }

    /**
     * Gets the name of the power-up.
     * @return Power-up name
     */
    @Override
    public String getName() {
        return "Multi Ball";
    }
}
