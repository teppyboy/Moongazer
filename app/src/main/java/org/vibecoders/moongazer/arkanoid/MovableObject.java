package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.math.Vector2;

public abstract class MovableObject extends GameObject {
    public Vector2 velocity;

    public MovableObject(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.velocity = new Vector2(0, 0);
    }

    public void setVelocity(float vx, float vy) {
        this.velocity.x = Math.abs(vx) < 0.01 ? 0 : vx;
        this.velocity.y = Math.abs(vy) < 0.01 ? 0 : vy;
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

    @Override
    public void update(float deltaTime) {
        int newX = getX() + (int) (velocity.x * deltaTime);
        int newY = getY() + (int) (velocity.y * deltaTime);
        setPosition(newX, newY);
    }
}
