package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class SlowBall extends PowerUp {
    public SlowBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk4.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSpeedMultiplier(0.5f);
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        if (arkanoid.getBall() != null) {
            arkanoid.getBall().setSpeedMultiplier(1.0f);
        }
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
