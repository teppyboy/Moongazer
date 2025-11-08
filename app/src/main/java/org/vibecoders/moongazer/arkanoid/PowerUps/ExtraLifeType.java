package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ExtraLifeType implements PowerUpType {
    @Override
    public void applyEffect(Arkanoid arkanoid) {
        if (arkanoid.lives == 3) {
            return;
        }
        arkanoid.lives += 1;
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        return;
    }

    @Override
    public int getDuration() {
        return -1;
    }

    @Override
    public String getName() {
        return "Add live";
    }
}
