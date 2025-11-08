package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ExpandPaddlePowerUp implements PowerUpType {
    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getPaddle().setSize((int) (arkanoid.getPaddle().getWidth() * 2), arkanoid.getPaddle().getHeight());
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        arkanoid.getPaddle().setSize((int) (arkanoid.getPaddle().getWidth() / 2), arkanoid.getPaddle().getHeight());
    }

    @Override
    public int getDuration() {
        return 5000;
    }

    @Override
    public String getName() {
        return "Expand Paddle";
    }
}
