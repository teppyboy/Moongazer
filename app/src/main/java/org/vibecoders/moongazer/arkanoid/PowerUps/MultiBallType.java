package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class MultiBallType implements PowerUpType {
    @Override
    public void applyEffect(Arkanoid arkanoid) {
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {

    }

    @Override
    public int getDuration() {
        return -1;
    }

    @Override
    public String getName() {
        return "Multi Ball";
    }
}
