package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

/**
 * SuperBall power-up that makes the ball pass through breakable bricks.
 * The ball transforms into an enchanted ball and can destroy breakable bricks without bouncing.
 * Unbreakable bricks and gameplay borders still cause normal collision.
 * Duration: 15 seconds (default)
 */
public class SuperBall extends PowerUp {

    public SuperBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk5.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSuperBall(true);
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
        arkanoid.getBall().setSuperBall(false);
    }

    @Override
    public int getDuration() {
        return 15000; // 15 seconds
    }

    @Override
    public String getName() {
        return "Super Ball";
    }
}
