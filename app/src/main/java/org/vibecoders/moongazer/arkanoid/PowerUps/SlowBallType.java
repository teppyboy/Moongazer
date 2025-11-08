package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class SlowBallType implements PowerUpType {

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSpeedMultiplier(0.5f);
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSpeedMultiplier(1.0f);
    }

    @Override
    public int getDuration() {
        return 5000;
    }

    @Override
    public String getName() {
        return "speed x0.5";
    }
}
