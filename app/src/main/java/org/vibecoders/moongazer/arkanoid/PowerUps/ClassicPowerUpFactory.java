package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;

public class ClassicPowerUpFactory implements PowerUpFactory {
    
    @Override
    public PowerUp createExpandPaddle(float x, float y, float width, float height) {
        return new ExpandPaddle(x, y, width, height);
    }

    @Override
    public PowerUp createMultiBall(float x, float y, float width, float height) {
        return new MultiBall(x, y, width, height);
    }

    @Override
    public PowerUp createExtraLife(float x, float y, float width, float height) {
        return new ExtraLife(x, y, width, height);
    }

    @Override
    public PowerUp createFastBall(float x, float y, float width, float height) {
        return new FastBall(x, y, width, height);
    }

    @Override
    public PowerUp createSlowBall(float x, float y, float width, float height) {
        return new SlowBall(x, y, width, height);
    }

    @Override
    public PowerUp createBulletPaddle(float x, float y, float width, float height) {
        return new BulletPaddle(x, y, width, height);
    }

    @Override
    public PowerUp createSuperBall(float x, float y, float width, float height) {
        return new SuperBall(x, y, width, height);
    }
}
