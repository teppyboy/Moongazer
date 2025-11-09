package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class FastBall extends PowerUp {
    public FastBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk2.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSpeedMultiplier(2f);
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        if (arkanoid.getBall() != null) {
            arkanoid.getBall().setSpeedMultiplier(1f);
        }
    }

    @Override
    public int getDuration() {
        return 5000;
    }

    @Override
    public String getName() {
        return "Speed x2";
    }
}
