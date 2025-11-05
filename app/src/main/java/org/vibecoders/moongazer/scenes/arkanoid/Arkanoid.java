package org.vibecoders.moongazer.scenes.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.*;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.vibecoders.moongazer.Constants.*;

public abstract class Arkanoid extends Scene {
    protected static final Logger log = LoggerFactory.getLogger(Arkanoid.class);
    protected static final float BRICK_WIDTH = 40f;
    protected static final float BRICK_HEIGHT = 40f;
    protected static final float BRICK_PADDING = 2f;
    protected Paddle paddle;
    protected Ball ball;
    protected List<Brick> bricks;
    protected BitmapFont font;
    protected SpriteBatch batch;
    protected int score = 0;
    protected int lives = 3;
    protected int bricksDestroyed = 0;
    protected boolean isPaused = false;
    protected boolean initialized = false;

    public Arkanoid(Game game) {
        super(game);
    }

    protected void init() {
        font = Assets.getFont("ui", 18);
        initGameplay();
        log.info("Arkanoid gameplay initialized");
    }

    protected void initGameplay() {
        float paddleWidth = 150f;
        float paddleHeight = 50f;
        float paddleX = (WINDOW_WIDTH - paddleWidth) / 2f;
        float paddleY = 50f;
        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight);
        float ballRadius = 8f;
        ball = new Ball(WINDOW_WIDTH / 2f, paddleY + paddleHeight + ballRadius + 5, ballRadius);
        bricks = new ArrayList<>();
    }

    protected void createBrickGrid(int rows, int cols) {
        bricks.clear();
        float startX = (WINDOW_WIDTH - (cols * (BRICK_WIDTH + BRICK_PADDING))) / 2f;
        float startY = WINDOW_HEIGHT - 100f;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = startX + col * (BRICK_WIDTH + BRICK_PADDING);
                float y = startY - row * (BRICK_HEIGHT + BRICK_PADDING);
                bricks.add(new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, getBrickType(row, col)));
            }
        }
    }

    protected Brick.BrickType getBrickType(int row, int col) {
        return (row % 3 == 0) ? Brick.BrickType.UNBREAKABLE : Brick.BrickType.BREAKABLE;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!initialized) {
            init();
            initialized = true;
        }
        this.batch = batch;
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!isPaused) {
            handleInput(delta);
            updateGameplay(delta);
            handleCollisions();
        }
        renderGameplay();
        renderUI();
    }

    protected void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !ball.isActive()) {
            ball.launch();
            log.info("Ball launched!");
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            onPausePressed();
        }
    }

    protected void updateGameplay(float delta) {
        paddle.update(delta, WINDOW_WIDTH);
        ball.update(delta);
        if (!ball.isActive()) {
            ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        }
    }

    protected void handleCollisions() {
        if (!ball.isActive()) return;
        float ballX = ball.getBounds().x;
        float ballY = ball.getBounds().y;
        float ballRadius = ball.getRadius();
        Vector2 velocity = ball.getVelocity();
        float speed = velocity.len();
        if (speed > 800f) {
            velocity.scl(800f / speed);
        }
        boolean collisionOccurred = false;
        if (ballX - ballRadius <= 0) {
            ball.getBounds().x = ballRadius + 1f;
            ball.reverseX();
            collisionOccurred = true;
        } else if (ballX + ballRadius >= WINDOW_WIDTH) {
            ball.getBounds().x = WINDOW_WIDTH - ballRadius - 1f;
            ball.reverseX();
            collisionOccurred = true;
        }
        if (ballY + ballRadius >= WINDOW_HEIGHT) {
            ball.getBounds().y = WINDOW_HEIGHT - ballRadius - 1f;
            ball.reverseY();
            collisionOccurred = true;
        } else if (ballY - ballRadius <= 0) {
            onBallLost();
            return;
        }
        if (collisionOccurred) return;
        List<Brick> bricksToCheck = new ArrayList<>();
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && Intersector.overlaps(ball.getBounds(), brick.getBounds())) {
                bricksToCheck.add(brick);
            }
        }
        if (!bricksToCheck.isEmpty()) {
            for (Brick brick : bricksToCheck) {
                handleBrickCollision(brick, ballX, ballY, ballRadius);
            }
        } else {
            handlePaddleCollision(ballX, ballY, ballRadius);
        }
        if (checkLevelComplete()) {
            onLevelComplete();
        }
    }

    protected void handleBrickCollision(Brick brick, float ballX, float ballY, float ballRadius) {
        Rectangle brickBounds = brick.getBounds();
        float brickCenterX = brickBounds.x + brickBounds.width / 2f;
        float brickCenterY = brickBounds.y + brickBounds.height / 2f;
        float dx = ballX - brickCenterX;
        float dy = ballY - brickCenterY;
        float overlapX = (brickBounds.width / 2f + ballRadius) - Math.abs(dx);
        float overlapY = (brickBounds.height / 2f + ballRadius) - Math.abs(dy);
        if (overlapX <= 0 || overlapY <= 0) return;
        if (overlapX < overlapY) {
            ball.reverseX();
            ball.getBounds().x = (dx > 0)
                    ? brickBounds.x + brickBounds.width + ballRadius + 2f
                    : brickBounds.x - ballRadius - 2f;
        } else {
            ball.reverseY();
            ball.getBounds().y = (dy > 0)
                    ? brickBounds.y + brickBounds.height + ballRadius + 2f
                    : brickBounds.y - ballRadius - 2f;
        }
        brick.hit();
        if (brick.getType() == Brick.BrickType.BREAKABLE && brick.isDestroyed()) {
            onBrickDestroyed(brick);
        }
    }

    protected void handlePaddleCollision(float ballX, float ballY, float ballRadius) {
        Rectangle paddleBounds = paddle.getBounds();
        Rectangle ballBounds = ball.getBounds();
        if (Intersector.overlaps(ballBounds, paddleBounds) && ball.getVelocity().y < 0) {
            float paddleTop = paddleBounds.y + paddleBounds.height;
            if (ballY - ballRadius < paddleTop + 10f) {
                ballBounds.y = paddleTop + ballRadius + 2f;
                float hitPos = MathUtils.clamp((ballX - paddleBounds.x) / paddleBounds.width, 0.1f, 0.9f);
                float bounceAngle = -(hitPos - 0.5f) * 100f;
                float speedMultiplier = Math.min(1.0f + (bricksDestroyed * 0.002f), 1.3f);
                float finalSpeed = 350f * speedMultiplier;
                float paddleVelocity = paddle.getVelocityX();
                if (Math.abs(paddleVelocity) > 0.5f) {
                    float paddleInfluence = MathUtils.clamp(paddleVelocity / 50f, -1f, 1f);
                    bounceAngle -= paddleInfluence * 15f;
                    if ((paddleInfluence > 0 && ball.getVelocity().x > 0) || (paddleInfluence < 0 && ball.getVelocity().x < 0)) {
                        finalSpeed *= 1.05f;
                    }
                }
                bounceAngle = MathUtils.clamp(bounceAngle, -70f, 70f);
                float angleInRadians = (float) Math.toRadians(90 + bounceAngle);
                ball.setVelocity(finalSpeed * (float) Math.cos(angleInRadians), finalSpeed * (float) Math.sin(angleInRadians));
            }
        }
    }

    protected void renderGameplay() {
        paddle.render(batch);
        ball.render(batch);
        for (Brick brick : bricks) {
            brick.render(batch);
        }
    }

    protected void renderUI() {
        font.setColor(Color.WHITE);
        font.draw(batch, String.format("Score: %d | Lives: %d", score, lives), 10, WINDOW_HEIGHT - 10);
    }

    protected boolean checkLevelComplete() {
        for (Brick brick : bricks) {
            if (brick.getType() == Brick.BrickType.BREAKABLE && !brick.isDestroyed()) {
                return false;
            }
        }
        return bricksDestroyed > 0;
    }

    protected void onBrickDestroyed(Brick brick) {
        score += 10;
        bricksDestroyed++;
        log.debug("Brick destroyed! Score: {}", score);
    }

    protected void onBallLost() {
        lives--;
        ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        log.info("Ball lost! Lives remaining: {}", lives);
        if (lives <= 0) {
            onGameOver();
        }
    }

    protected abstract void onLevelComplete();
    protected abstract void onGameOver();
    protected abstract void onPausePressed();

    @Override
    public void dispose() {
        if (paddle != null) paddle.dispose();
        if (ball != null) ball.dispose();
        if (bricks != null) {
            for (Brick brick : bricks) {
                brick.dispose();
            }
            bricks.clear();
        }
    }
}