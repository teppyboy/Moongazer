package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;

public class ClassicPowerUpFactory implements PowerUpFactory {
    
    /**
     * Creates an ExpandPaddle power-up.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @return ExpandPaddle instance
     */
    @Override
    public PowerUp createExpandPaddle(float x, float y, float width, float height) {
        return new ExpandPaddle(x, y, width, height);
    }

    /**
     * Creates a MultiBall power-up.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @return MultiBall instance
     */
    @Override
    public PowerUp createMultiBall(float x, float y, float width, float height) {
        return new MultiBall(x, y, width, height);
    }

    /**
     * Creates an ExtraLife power-up.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @return ExtraLife instance
     */
    @Override
    public PowerUp createExtraLife(float x, float y, float width, float height) {
        return new ExtraLife(x, y, width, height);
    }

    /**
     * Creates a FastBall power-up.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @return FastBall instance
     */
    @Override
    public PowerUp createFastBall(float x, float y, float width, float height) {
        return new FastBall(x, y, width, height);
    }

    /**
     * Creates a SlowBall power-up.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @return SlowBall instance
     */
    @Override
    public PowerUp createSlowBall(float x, float y, float width, float height) {
        return new SlowBall(x, y, width, height);
    }

    /**
     * Creates a BulletPaddle power-up.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @return BulletPaddle instance
     */
    @Override
    public PowerUp createBulletPaddle(float x, float y, float width, float height) {
        return new BulletPaddle(x, y, width, height);
    }

    /**
     * Creates a SuperBall power-up.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     * @return SuperBall instance
     */
    @Override
    public PowerUp createSuperBall(float x, float y, float width, float height) {
        return new SuperBall(x, y, width, height);
    }
}
