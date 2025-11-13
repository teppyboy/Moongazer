package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.math.Vector2;

public abstract class MovableObject extends GameObject {
    public Vector2 velocity;

    /**
     * Constructs a new MovableObject with specified bounds.
     * @param x X position
     * @param y Y position
     * @param width Width of the object
     * @param height Height of the object
     */
    public MovableObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.velocity = new Vector2(0, 0);
    }

    /**
     * Sets the velocity of the object.
     * @param vx Horizontal velocity
     * @param vy Vertical velocity
     */
    public void setVelocity(float vx, float vy) {
        this.velocity.x = Math.abs(vx) < 0.01 ? 0 : vx;
        this.velocity.y = Math.abs(vy) < 0.01 ? 0 : vy;
    }

    /**
     * Gets the velocity vector of the object.
     * @return Velocity vector
     */
    public Vector2 getVelocity() {
        return velocity;
    }

    /**
     * Gets the horizontal velocity component.
     * @return X velocity
     */
    public float getVelocityX() {
        return velocity.x;
    }

    /**
     * Gets the vertical velocity component.
     * @return Y velocity
     */
    public float getVelocityY() {
        return velocity.y;
    }

    /**
     * Updates the object's position based on velocity and delta time.
     * @param deltaTime Time elapsed since last update
     */
    @Override
    public void update(float deltaTime) {
        int newX = getX() + (int) (velocity.x * deltaTime);
        int newY = getY() + (int) (velocity.y * deltaTime);
        setPosition(newX, newY);
    }
}
