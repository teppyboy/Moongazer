package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.math.Vector2;

/**
 * A GameObject that can move with a constant velocity.
 * Positions are updated every frame based on velocity without friction.
 */
public abstract class MovableObject extends GameObject {
    public Vector2 velocity;

    /**
     * Creates a stationary movable object.
     *
     * @param x        initial X position
     * @param y        initial Y position
     * @param width    width in pixels
     * @param height   height in pixels
     * @param filepath texture resource path
     */
    public MovableObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.velocity = new Vector2(0, 0);
    }


    /**
     * Sets the velocity. Add thresholds to avoid drift.
     *
     * @param vx horizontal velocity
     * @param vy vertical velocity
     */
    public void setVelocity(float vx, float vy) {
        if (Math.abs(vx) < 0.01) {
            this.velocity.x = 0;
        } else {
            this.velocity.x = vx;
        }

        if (Math.abs(vy) < 0.01) {
            this.velocity.y = 0;
        } else {
            this.velocity.y = vy;
        }
    }

    public Vector2 getVelocity() {
        return velocity;
    }
    public float getVelocityX() {
        return velocity.x;
    }
    public float getVelocityY() {
        return velocity.y;
    }


    /**
     * Updates the position according to the current velocity.
     *
     * @param deltaTime time since last frame in seconds
     */
    @Override
    public void update(float deltaTime) {
        int newX = getX() + (int) (velocity.x * deltaTime);
        int newY = getY() + (int) (velocity.y * deltaTime);
        setPosition(newX, newY);
    }

}
