package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class BulletPaddle extends PowerUp {
    public BulletPaddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getPaddle().setBulletEnabled(true);
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        arkanoid.getPaddle().setBulletEnabled(false);
    }

    @Override
    public int getDuration() {
        return 12000;
    }

    @Override
    public String getName() {
        return "Bullet Paddle";
    }
}
