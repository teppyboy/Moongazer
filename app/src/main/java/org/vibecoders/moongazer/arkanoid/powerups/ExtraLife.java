package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ExtraLife extends PowerUp {
    /**
     * Constructs a new ExtraLife power-up.
     * @param x X position
     * @param y Y position
     * @param width Width of the power-up
     * @param height Height of the power-up
     */
    public ExtraLife(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/ui/hearth.png");
    }

    /**
     * Applies the extra life effect, adding one life if under maximum.
     * @param arkanoid The game instance
     */
    @Override
    public void applyEffect(Arkanoid arkanoid) {
        if (arkanoid.lives < 3) {
            arkanoid.lives += 1;
        }
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
        return "Add live";
    }
}
