package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ClassicPowerUpFactory implements PowerUpFactory {
    private Arkanoid arkanoid;
    @Override
    public PowerUp createExpandPaddle(float x, float y, float width, float height) {
        return new PowerUp("textures/arkanoid/perk2.png", x, y, width, height, new ExpandPaddlePowerUp());
    }

    @Override
    public PowerUp createMultiBall(float x, float y, float width, float height) {
        return null;
    }

    @Override
    public PowerUp createExtraLife(float x, float y, float width, float height) {
        return new PowerUp("textures/ui/hearth.png", x, y, width, height, new ExtraLifeType());
    }

    @Override
    public PowerUp createFastBall(float x, float y, float width, float height) {
        return new PowerUp("textures/arkanoid/perk3.png", x, y, width, height, new FastBallType());
    }

    @Override
    public PowerUp createSlowBall(float x, float y, float width, float height) {
        return new PowerUp("textures/arkanoid/perk4.png", x, y, width, height, new SlowBallType());
    }

    @Override
    public PowerUp createBulletPaddle(float x, float y, float width, float height) {
        return null;
    }
}
