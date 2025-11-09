package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;

public interface PowerUpFactory {
    PowerUp createExpandPaddle(float x, float y, float width, float height);
    PowerUp createMultiBall(float x, float y, float width, float height);
    PowerUp createExtraLife(float x, float y, float width, float height);
    PowerUp createFastBall(float x, float y, float width, float height);
    PowerUp createSlowBall(float x, float y, float width, float height);
    PowerUp createBulletPaddle(float x, float y, float width, float height);
    PowerUp createSuperBall(float x, float y, float width, float height);
}
