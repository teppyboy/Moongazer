package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    public Rectangle bounds;

    /**
     * Constructs a new GameObject with specified bounds.
     * @param x X position
     * @param y Y position
     * @param width Width of the object
     * @param height Height of the object
     */
    public GameObject(float x, float y, float width, float height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Gets the bounding rectangle of the object.
     * @return Rectangle representing the object's bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Gets the X position of the object.
     * @return X position as integer
     */
    public int getX() {
        return (int) bounds.x;
    }

    /**
     * Gets the Y position of the object.
     * @return Y position as integer
     */
    public int getY() {
        return (int) bounds.y;
    }

    /**
     * Gets the width of the object.
     * @return Width as integer
     */
    public int getWidth() {
        return (int) bounds.width;
    }

    /**
     * Gets the height of the object.
     * @return Height as integer
     */
    public int getHeight() {
        return (int) bounds.height;
    }

    /**
     * Sets the position of the object.
     * @param x New X position
     * @param y New Y position
     */
    public void setPosition(int x, int y) {
        this.bounds.x = x;
        this.bounds.y = y;
    }

    /**
     * Sets the size of the object.
     * @param width New width
     * @param height New height
     */
    public void setSize(int width, int height) {
        this.bounds.width = width;
        this.bounds.height = height;
    }

    /**
     * Sets the bounds of the object.
     * @param x X position
     * @param y Y position
     * @param width Width
     * @param height Height
     */
    public void setBounds(int x, int y, int width, int height) {
        this.bounds.set(x, y, width, height);
    }

    /**
     * Updates the object's state.
     * @param deltaTime Time elapsed since last update
     */
    public abstract void update(float deltaTime);
}