package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

/**
 * MultiBall power-up that spawns 2 additional balls.
 * Maximum of 3 balls total can exist at once.
 * This is an instant effect (no duration).
 */
public class MultiBall extends PowerUp {

    public MultiBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk3.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        // Spawn 2 additional balls (max 3 total)
        arkanoid.spawnBalls(2);
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        // No effect to remove - this is an instant power-up
    }

    @Override
    public int getDuration() {
        return -1; // Instant effect, no duration
    }

    @Override
    public String getName() {
        return "Multi Ball";
    }
}
