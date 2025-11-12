package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ExpandPaddle extends PowerUp {
    public ExpandPaddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk5.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getPaddle().setSize((int) (arkanoid.getPaddle().getWidth() * 2), arkanoid.getPaddle().getHeight());
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        if (arkanoid.getPaddle() != null) {
            arkanoid.getPaddle().setSize((int) (arkanoid.getPaddle().getWidth() / 2), arkanoid.getPaddle().getHeight());
        }
    }

    @Override
    public int getDuration() {
        return 10000;
    }

    @Override
    public String getName() {
        return "Expand Paddle";
    }
}
