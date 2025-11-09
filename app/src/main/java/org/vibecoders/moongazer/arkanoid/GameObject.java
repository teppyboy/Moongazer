package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {
    public Rectangle bounds;

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

    public abstract void update(float deltaTime);
}