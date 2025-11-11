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
    private float originalY;
    private float currentYOffset = 0f;
    private float targetYOffset = 0f;
    private static final float MAX_Y_OFFSET = 8f;
    private static final float BOUNCE_SPEED = 12f;
    private boolean aiMoveLeft = false;
    private boolean aiMoveRight = false;

    public Paddle(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.targetX = x;
        this.originalY = y;
        this.texture = Assets.getAsset("textures/arkanoid/paddle.png", Texture.class);
    }

    public void update(float delta, int screenWidth) {
        update(delta, 0, screenWidth);
    }

    public void update(float delta, float minX, float maxX) {
        boolean keyboardUsed = false;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || aiMoveLeft) {
            bounds.x -= speed * delta;
            targetX = bounds.x;
            keyboardUsed = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || aiMoveRight) {
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
        updateBounceEffect(delta);
    }

    private void updateBounceEffect(float delta) {
        if (targetYOffset > 0) {
            targetYOffset = Math.max(0, targetYOffset - BOUNCE_SPEED * delta);
        }
        float diff = targetYOffset - currentYOffset;
        currentYOffset += diff * 10f * delta;
        bounds.y = originalY - currentYOffset;
    }

    public void onBallHit(float ballVelocityY) {
        float impactStrength = Math.abs(ballVelocityY) / 350f;
        impactStrength = MathUtils.clamp(impactStrength, 0.3f, 1.0f);
        targetYOffset = MAX_Y_OFFSET * impactStrength;
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

    public void setOriginalY(float y) {
        this.originalY = y;
        this.currentYOffset = 0f;
        this.targetYOffset = 0f;
        this.bounds.y = y;
    }

    public void moveLeft(boolean move) {
        this.aiMoveLeft = move;
    }

    public void moveRight(boolean move) {
        this.aiMoveRight = move;
    }
}