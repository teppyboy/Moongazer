package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class SuperBall extends PowerUp {
    public SuperBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk1.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSuperBall(true);
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        // Check if ball still exists before removing effect
        if (arkanoid.getBall() != null) {
            arkanoid.getBall().setSuperBall(false);
        }
    }

    @Override
    public int getDuration() {
        return 15000;
    }

    @Override
    public String getName() {
        return "Super Ball";
    }
}
