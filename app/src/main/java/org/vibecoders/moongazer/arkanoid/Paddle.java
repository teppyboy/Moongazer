package org.vibecoders.moongazer.arkanoid;

import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Paddle extends MovableObject {
    private Texture texture;
    private float speed = 500f;
    private float targetX;
    private float smoothingFactor = 0.25f;
    private boolean isSticky = false;

    public Paddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.targetX = x;
        this.texture = Assets.getAsset("textures/arkanoid/paddle.png", Texture.class);
    }

    public void update(float delta, int screenWidth) {
        update(delta, 0, screenWidth);
    }

    public void update(float delta, float minX, float maxX) {
        boolean keyboardUsed = false;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            bounds.x -= speed * delta;
            targetX = bounds.x;
            keyboardUsed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            bounds.x += speed * delta;
            targetX = bounds.x;
            keyboardUsed = true;
        }

        if (!keyboardUsed && Gdx.input.isTouched()) {
            float mouseX = Gdx.input.getX();
            targetX = mouseX - bounds.width / 2f;
            bounds.x = MathUtils.lerp(bounds.x, targetX, 1f - smoothingFactor);
        }

        bounds.x = MathUtils.clamp(bounds.x, minX, maxX - bounds.width);
        targetX = MathUtils.clamp(targetX, minX, maxX - bounds.width);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getCenterX() {
        return bounds.x + bounds.width / 2;
    }

    public float getVelocityX() {
        return (targetX - bounds.x);
    }

    public void extend(float amount) {
        bounds.width += amount;
        bounds.x -= amount / 2f;
    }

    public void extend() {
        extend(100f);
    }

    public void shrink(float amount) {
        bounds.width -= amount;
        bounds.x += amount / 2f;
        if (bounds.width < 50f) {
            bounds.width = 50f;
        }
    }

    public void shrink() {
        shrink(100f);
    }

    public void setSticky(boolean sticky) {
        this.isSticky = sticky;
    }

    public boolean isSticky() {
        return isSticky;
    }
}