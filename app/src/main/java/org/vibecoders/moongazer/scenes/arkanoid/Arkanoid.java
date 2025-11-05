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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.*;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.Scene;

import java.util.ArrayList;
import java.util.List;

import static org.vibecoders.moongazer.Constants.*;

/**
 * Base class for Arkanoid gameplay.
 * Contains all core game mechanics: paddle, ball, bricks, collisions.
 */
public abstract class Arkanoid extends Scene {
    protected static final Logger log = LoggerFactory.getLogger(Arkanoid.class);

    // Game objects
    protected Paddle paddle;
    protected Ball ball;
    protected List<Brick> bricks;
    protected BitmapFont font;
    protected SpriteBatch batch;

    // Game state
    protected int score = 0;
    protected int lives = 3;
    protected int bricksDestroyed = 0;
    protected boolean isPaused = false;
    protected boolean initialized = false;

    // Brick configuration
    protected static final float BRICK_WIDTH = 40f;
    protected static final float BRICK_HEIGHT = 40f;
    protected static final float BRICK_PADDING = 2f;

    public Arkanoid(Game game) {
        super(game);
    }

    /**
     * Initialize the scene - called once before first render.
     */
    protected void init() {
        font = Assets.getFont("ui", 18);
        initGameplay();
        log.info("Arkanoid gameplay initialized");
    }

    /**
     * Initialize gameplay - create paddle, ball, and bricks.
     * Can be overridden by subclasses for different initialization.
     */
    protected void initGameplay() {
        // Create paddle
        float paddleWidth = 150f;
        float paddleHeight = 50f;
        float paddleX = (WINDOW_WIDTH - paddleWidth) / 2f;
        float paddleY = 50f;
        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight);

        // Create ball
        float ballRadius = 8f;
        ball = new Ball(WINDOW_WIDTH / 2f, paddleY + paddleHeight + ballRadius + 5, ballRadius);

        // Bricks will be initialized by subclasses
        bricks = new ArrayList<>();
    }

    /**
     * Create a grid of bricks. Called by subclasses.
     */
    protected void createBrickGrid(int rows, int cols) {
        bricks.clear();
        float startX = (WINDOW_WIDTH - (cols * (BRICK_WIDTH + BRICK_PADDING))) / 2f;
        float startY = WINDOW_HEIGHT - 100f;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = startX + col * (BRICK_WIDTH + BRICK_PADDING);
                float y = startY - row * (BRICK_HEIGHT + BRICK_PADDING);

                Brick.BrickType type = getBrickType(row, col);
                bricks.add(new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, type));
            }
        }
    }

    /**
     * Determine brick type for given position.
     * Override this for custom patterns.
     */
    protected Brick.BrickType getBrickType(int row, int col) {
        // Default: every 3rd row is unbreakable
        return (row % 3 == 0) ? Brick.BrickType.UNBREAKABLE : Brick.BrickType.BREAKABLE;
    }

    @Override
    public void render(SpriteBatch batch) {
        // Initialize on first render
        if (!initialized) {
            init();
            initialized = true;
        }

        this.batch = batch;
        float delta = Gdx.graphics.getDeltaTime();

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isPaused) {
            // Handle input
            handleInput(delta);

            // Update game objects
            updateGameplay(delta);

            // Collision detection
            handleCollisions();
        }

        // Render everything
        renderGameplay();
        renderUI();
    }

    /**
     * Handle input - can be overridden for different controls.
     */
    protected void handleInput(float delta) {
        // Launch ball
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !ball.isActive()) {
            ball.launch();
            log.info("Ball launched!");
        }

        // Pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            onPausePressed();
        }
    }

    /**
     * Update game objects.
     */
    protected void updateGameplay(float delta) {
        paddle.update(delta, WINDOW_WIDTH);
        ball.update(delta);

        // Keep ball on paddle if not active
        if (!ball.isActive()) {
            ball.reset(paddle.getCenterX(),
                    paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        }
    }

    /**
     * Handle all collision detection.
     */
    protected void handleCollisions() {
        Rectangle ballBounds = ball.getBounds();
        float ballX = ball.getBounds().x;
        float ballY = ball.getBounds().y;
        float ballRadius = ball.getRadius();

        // Wall collisions
        if (ballX - ballRadius <= 0) {
            ball.getBounds().x = ballRadius + 1f;
            ball.reverseX();
        }
        if (ballX + ballRadius >= WINDOW_WIDTH) {
            ball.getBounds().x = WINDOW_WIDTH - ballRadius - 1f;
            ball.reverseX();
        }
        if (ballY + ballRadius >= WINDOW_HEIGHT) {
            ball.getBounds().y = WINDOW_HEIGHT - ballRadius - 1f;
            ball.reverseY();
        }

        // Bottom boundary (ball lost)
        if (ballY - ballRadius <= 0) {
            onBallLost();
            return;
        }

        // Brick collisions
        boolean brickHit = false;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && Intersector.overlaps(ballBounds, brick.getBounds())) {
                handleBrickCollision(brick, ballX, ballY, ballRadius);
                brickHit = true;
                break;
            }
        }

        // Paddle collision
        if (!brickHit && ball.isActive()) {
            handlePaddleCollision(ballX, ballY, ballRadius);
        }

        // Check win condition
        if (checkLevelComplete()) {
            onLevelComplete();
        }
    }

    /**
     * Handle collision with a brick.
     */
    protected void handleBrickCollision(Brick brick, float ballX, float ballY, float ballRadius) {
        Rectangle brickBounds = brick.getBounds();

        // Calculate overlap on each axis
        float overlapLeft = (ballX + ballRadius) - brickBounds.x;
        float overlapRight = (brickBounds.x + brickBounds.width) - (ballX - ballRadius);
        float overlapTop = (brickBounds.y + brickBounds.height) - (ballY - ballRadius);
        float overlapBottom = (ballY + ballRadius) - brickBounds.y;

        float minOverlapX = Math.min(overlapLeft, overlapRight);
        float minOverlapY = Math.min(overlapTop, overlapBottom);

        // Reflect ball based on collision axis
        if (minOverlapX < minOverlapY) {
            ball.reverseX();
            if (overlapLeft < overlapRight) {
                ball.getBounds().x = brickBounds.x - ballRadius - 2.0f;
            } else {
                ball.getBounds().x = brickBounds.x + brickBounds.width + ballRadius + 2.0f;
            }
        } else {
            ball.reverseY();
            if (overlapTop < overlapBottom) {
                ball.getBounds().y = brickBounds.y + brickBounds.height + ballRadius + 2.0f;
            } else {
                ball.getBounds().y = brickBounds.y - ballRadius - 2.0f;
            }
        }

        // Damage brick
        brick.hit();
        if (brick.getType() == Brick.BrickType.BREAKABLE && brick.isDestroyed()) {
            onBrickDestroyed(brick);
        }
    }

    /**
     * Handle collision with paddle.
     */
    protected void handlePaddleCollision(float ballX, float ballY, float ballRadius) {
        Rectangle paddleBounds = paddle.getBounds();
        Rectangle ballBounds = ball.getBounds();

        boolean ballIsAbovePaddle = ballY - ballRadius > paddleBounds.y;

        if (Intersector.overlaps(ballBounds, paddleBounds) &&
                ball.getVelocity().y < 0 &&
                ballIsAbovePaddle) {

            // Position correction
            ball.getBounds().y = paddleBounds.y + paddleBounds.height + ballRadius + 2f;

            // Calculate hit position (0 = left, 1 = right)
            float hitPos = (ballX - paddleBounds.x) / paddleBounds.width;
            hitPos = Math.max(0.1f, Math.min(0.9f, hitPos));

            // Bounce angle
            float bounceAngle = -(hitPos - 0.5f) * 100f;

            // Speed calculation
            float targetSpeed = 350f;
            float speedMultiplier = 1.0f + (bricksDestroyed * 0.002f);
            speedMultiplier = Math.min(speedMultiplier, 1.3f);
            float finalSpeed = targetSpeed * speedMultiplier;

            // Paddle influence
            float paddleVelocity = paddle.getVelocityX();
            if (Math.abs(paddleVelocity) > 0.5f) {
                float paddleInfluence = MathUtils.clamp(paddleVelocity / 50f, -1f, 1f);
                bounceAngle -= paddleInfluence * 15f;

                if ((paddleInfluence > 0 && ball.getVelocity().x > 0) ||
                        (paddleInfluence < 0 && ball.getVelocity().x < 0)) {
                    finalSpeed *= 1.05f;
                }
            }

            // Apply velocity
            float angleInRadians = (float) Math.toRadians(90 + bounceAngle);
            ball.setVelocity(
                    finalSpeed * (float) Math.cos(angleInRadians),
                    finalSpeed * (float) Math.sin(angleInRadians)
            );
        }
    }

    /**
     * Render game objects.
     */
    protected void renderGameplay() {
        paddle.render(batch);
        ball.render(batch);
        for (Brick brick : bricks) {
            brick.render(batch);
        }
    }

    /**
     * Render UI - override for custom UI.
     */
    protected void renderUI() {
        font.setColor(Color.WHITE);
        font.draw(batch, String.format("Score: %d | Lives: %d", score, lives), 10, WINDOW_HEIGHT - 10);
    }

    /**
     * Check if level is complete (all breakable bricks destroyed).
     */
    protected boolean checkLevelComplete() {
        for (Brick brick : bricks) {
            if (brick.getType() == Brick.BrickType.BREAKABLE && !brick.isDestroyed()) {
                return false;
            }
        }
        return bricksDestroyed > 0; // At least one brick was destroyed
    }

    // ===== Abstract methods - must be implemented by subclasses =====

    /**
     * Called when a brick is destroyed.
     */
    protected void onBrickDestroyed(Brick brick) {
        score += 10;
        bricksDestroyed++;
        log.debug("Brick destroyed! Score: {}", score);
    }

    /**
     * Called when ball is lost.
     */
    protected void onBallLost() {
        lives--;
        ball.reset(paddle.getCenterX(),
                paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        log.info("Ball lost! Lives remaining: {}", lives);

        if (lives <= 0) {
            onGameOver();
        }
    }

    /**
     * Called when level is completed.
     */
    protected abstract void onLevelComplete();

    /**
     * Called when game is over.
     */
    protected abstract void onGameOver();

    /**
     * Called when pause is pressed.
     */
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