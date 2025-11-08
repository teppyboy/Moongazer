package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;

public class ClassicPowerUpFactory implements PowerUpFactory {
    
    @Override
    public PowerUp createExpandPaddle(float x, float y, float width, float height) {
        return new ExpandPaddlePowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createMultiBall(float x, float y, float width, float height) {
        return new MultiBallPowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createExtraLife(float x, float y, float width, float height) {
        return new ExtraLifePowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createFastBall(float x, float y, float width, float height) {
        return new FastBallPowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createSlowBall(float x, float y, float width, float height) {
        return new SlowBallPowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createBulletPaddle(float x, float y, float width, float height) {
        return new BulletPaddlePowerUp(x, y, width, height);
    }
}
