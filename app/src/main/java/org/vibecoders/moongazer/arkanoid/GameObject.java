package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.math.Rectangle;

/**
 * Base type for anything that can be rendered by the client.
 * A GameObject has a position, a size and a reference to the texture file
 * used for rendering. Subclasses define how the object updates over time.
 */
public abstract class GameObject {
    public Rectangle bounds;

    /**
     * Creates a new game object.
     *
     * @param x      initial X position in pixels
     * @param y      initial Y position in pixels
     * @param width  width in pixels
     * @param height height in pixels
     */
    public GameObject(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public int getX() {
        return (int) bounds.x;
    }

    public int getY() {
        return (int) bounds.y;
    }

    public int getWidth() {
        return (int) bounds.width;
    }

    public int getHeight() {
        return (int) bounds.height;
    }

    public void setPosition(int x, int y) {
        this.bounds.x = x;
        this.bounds.y = y;
    }

    public void setSize(int width, int height) {
        this.bounds.width = width;
        this.bounds.height = height;
    }

    public void setBounds(int x, int y, int width, int height) {
        this.bounds.set(x, y, width, height);
    }

    /**
     * Updates the internal state of the object.
     *
     * @param deltaTime time since last frame in seconds
     */
    public abstract void update(float deltaTime);
}