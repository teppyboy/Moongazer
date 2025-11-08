package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;

public class ClassicPowerUpFactory implements PowerUpFactory {
    
    @Override
    public PowerUp createExpandPaddle(float x, float y, float width, float height) {
        return new ClassicExpandPaddlePowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createMultiBall(float x, float y, float width, float height) {
        return new ClassicMultiBallPowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createExtraLife(float x, float y, float width, float height) {
        return new ClassicExtraLifePowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createFastBall(float x, float y, float width, float height) {
        return new ClassicFastBallPowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createSlowBall(float x, float y, float width, float height) {
        return new ClassicSlowBallPowerUp(x, y, width, height);
    }

    @Override
    public PowerUp createBulletPaddle(float x, float y, float width, float height) {
        return new ClassicBulletPaddlePowerUp(x, y, width, height);
    }
}
