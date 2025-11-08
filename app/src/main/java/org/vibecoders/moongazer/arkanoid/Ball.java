package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.vibecoders.moongazer.managers.Assets;

public class Ball extends MovableObject {
    private Vector2 velocity;
    private float radius;
    private Texture texture;
    private boolean active;
    private float previousY;
    private boolean isHeavyBall = false;
    private boolean isSuperBall = false;
    private boolean isStuckToPaddle = false;
    private float stuckOffsetX = 0f;
    private int comboCount = 0;
    private float speedMultiplier = 1.0f;
    private Texture normalTexture;
    private Texture enchantedTexture;

    public Ball(float x, float y, float radius) {
        super((int)x, (int)y, (int)radius * 2, (int)radius * 2);
        this.velocity = new Vector2(300, 300);
        this.radius = radius;
        this.active = false;
        this.normalTexture = Assets.getAsset("textures/arkanoid/normal_ball.png", Texture.class);
        this.enchantedTexture = Assets.getAsset("textures/arkanoid/enchanted_ball.png", Texture.class);
        this.texture = normalTexture;
    }

    public void update(float delta) {
        previousY = bounds.y;
        if (active && !isStuckToPaddle) {
            bounds.x += velocity.x * delta;
            bounds.y += velocity.y * delta;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, bounds.x - radius, bounds.y - radius, radius * 2, radius * 2);
    }

    public void launch() {
        active = true;
    }

    public void reset(float x, float y) {
        bounds.set(x, y, radius * 2, radius * 2);
        velocity.set(300, 300);
        active = false;
    }

    public void reverseX() {
        velocity.x = -velocity.x;
    }

    public void reverseY() {
        velocity.y = -velocity.y;
    }

    public void setVelocity(float x, float y) {
        velocity.set(x, y);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getRadius() {
        return radius;
    }

    public boolean isActive() {
        return active;
    }

    public float getCenterX() {
        return bounds.x + radius;
    }

    public float getCenterY() {
        return bounds.y + radius;
    }

    public float getSpeed() {
        return velocity.len();
    }

    public void normalizeVelocity(float targetSpeed) {
        float currentSpeed = getSpeed();
        if (currentSpeed > 0) {
            velocity.scl(targetSpeed / currentSpeed);
        }
    }

    public float getPreviousY() {
        return previousY;
    }

    public void setHeavyBall(boolean heavyBall) {
        this.isHeavyBall = heavyBall;
    }

    public boolean isHeavyBall() {
        return isHeavyBall;
    }

    public void setSuperBall(boolean superBall) {
        this.isSuperBall = superBall;
        this.texture = superBall ? enchantedTexture : normalTexture;
    }

    public boolean isSuperBall() {
        return isSuperBall;
    }

    public void setStuckToPaddle(boolean stuck) {
        this.isStuckToPaddle = stuck;
    }

    public boolean isStuckToPaddle() {
        return isStuckToPaddle;
    }

    public void setStuckOffsetX(float offsetX) {
        this.stuckOffsetX = offsetX;
    }

    public float getStuckOffsetX() {
        return stuckOffsetX;
    }

    public void resetToCenter(Paddle paddle) {
        Rectangle paddleBounds = paddle.getBounds();
        float paddleCenterX = paddleBounds.x + paddleBounds.width / 2f;
        float paddleTop = paddleBounds.y + paddleBounds.height;
        reset(paddleCenterX, paddleTop + radius + 5);
        isStuckToPaddle = true;
        stuckOffsetX = 0f;
        velocity.set(0, 0);
    }

    public void launchFromPaddle(Paddle paddle, float maxBounceAngle) {
        if (isStuckToPaddle) {
            float paddleHalfWidth = paddle.getBounds().width / 2f;
            float offset = Math.max(-1.0f, Math.min(1.0f, stuckOffsetX / paddleHalfWidth));
            float angleFromVertical = offset * maxBounceAngle;
            float angleInRadians = (float) Math.toRadians(angleFromVertical);
            float speed = 350f * speedMultiplier;
            float vx = speed * (float) Math.sin(angleInRadians);
            float vy = Math.abs(speed * (float) Math.cos(angleInRadians));
            velocity.set(vx, vy);
            normalizeVelocity(speed);
            setStuckToPaddle(false);
        }
    }

    public void incrementCombo() {
        comboCount++;
    }

    public int getComboCount() {
        return comboCount;
    }

    public void resetCombo() {
        comboCount = 0;
    }

    public void setComboCount(int comboCount) {
        this.comboCount = comboCount;
    }

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
        if (active && !isStuckToPaddle) {
            float currentSpeed = getSpeed();
            if (currentSpeed > 0) {
                normalizeVelocity(350f * speedMultiplier);
            }
        }
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }
}
