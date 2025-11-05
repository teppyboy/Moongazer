package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.arkanoid.*;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;

import java.util.ArrayList;
import java.util.List;

import static org.vibecoders.moongazer.Constants.*;

public class GameplayTestScene extends Scene {
    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private Label instructionLabel;
    private Label statsLabel;
    private int score = 0;
    private int bricksDestroyed = 0;
    private boolean wasVisible = true;

    // Wave system for endless mode
    private int currentWave = 1;
    private float unbreakableChance = 0.1f;

    // Brick configuration - можно легко изменить
    private static final float BRICK_WIDTH = 40f;
    private static final float BRICK_HEIGHT = 40f;
    private static final float BRICK_PADDING = 2f;
    private static final int BRICK_COLS = 30; // Increase columns since bricks are now square

    public GameplayTestScene(Game game) {
        super(game);
        initGameplay();
        initUI();
        game.stage.addActor(root);
    }

    private void initGameplay() {
        // Create paddle
        float paddleWidth = 120f;
        float paddleHeight = 20f;
        float paddleX = (WINDOW_WIDTH - paddleWidth) / 2f;
        float paddleY = 50f;
        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight);

        // Create ball
        float ballRadius = 8f;
        ball = new Ball(WINDOW_WIDTH / 2f, paddleY + paddleHeight + ballRadius + 5, ballRadius);

        // Initialize bricks list
        bricks = new ArrayList<>();

        // Start first wave
        startWave(currentWave);
    }

    private void reinitGameplay() {
        if (paddle != null) paddle.dispose();
        if (ball != null) ball.dispose();
        if (bricks != null) {
            for (Brick brick : bricks) {
                brick.dispose();
            }
            bricks.clear();
        }
        score = 0;
        bricksDestroyed = 0;
        currentWave = 1;
        unbreakableChance = 0.1f;
        initGameplay();
        log.info("GameplayTestScene reinitialized");
    }

    private void startWave(int wave) {
        bricksDestroyed = 0;

        // Calculate difficulty based on wave
        int rows = Math.min(5 + (wave / 2), 10);
        unbreakableChance = Math.min(0.1f + (wave * 0.02f), 0.4f);

        // Create brick grid
        createBrickGrid(rows, BRICK_COLS);

        log.info("=== WAVE {} STARTED === (Rows: {}, Unbreakable Chance: {}%)",
                 wave, rows, (int)(unbreakableChance * 100));
    }

    private void createBrickGrid(int rows, int cols) {
        bricks.clear();

        float startX = (WINDOW_WIDTH - (cols * (BRICK_WIDTH + BRICK_PADDING))) / 2f;
        float startY = WINDOW_HEIGHT - 100f;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = startX + col * (BRICK_WIDTH + BRICK_PADDING);
                float y = startY - row * (BRICK_HEIGHT + BRICK_PADDING);

                Brick.BrickType type = (Math.random() < unbreakableChance)
                    ? Brick.BrickType.UNBREAKABLE
                    : Brick.BrickType.BREAKABLE;

                bricks.add(new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, type));
            }
        }
    }

    private void initUI() {
        BitmapFont font = Assets.getFont("ui", 18);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        instructionLabel = new Label(
            "GAMEPLAY TEST - Press SPACE to launch ball | A/D or LEFT/RIGHT to move | ESC to return",
            labelStyle
        );
        instructionLabel.setPosition(10, WINDOW_HEIGHT - 30);

        statsLabel = new Label("Score: 0 | Bricks: 0", labelStyle);
        statsLabel.setPosition(10, WINDOW_HEIGHT - 55);

        root.addActor(instructionLabel);
        root.addActor(statsLabel);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(1, 1, 1, 1);
        boolean isVisible = root.isVisible();
        if (isVisible && !wasVisible) {
            reinitGameplay();
        }
        wasVisible = isVisible;
        float delta = Gdx.graphics.getDeltaTime();

        // Clear screen with black background to hide main menu
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input
        handleInput();

        // Update game objects
        paddle.update(delta, WINDOW_WIDTH);
        ball.update(delta);

        // If ball is not active, keep it on paddle
        if (!ball.isActive()) {
            ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        }

        // Collision detection
        handleCollisions();

        // Render game objects
        paddle.render(batch);
        ball.render(batch);
        for (Brick brick : bricks) {
            brick.render(batch);
        }

        // Update UI
        updateStats();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !ball.isActive()) {
            ball.launch();
            Audio.playSfxConfirm();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Audio.playSfxReturn();
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 350);
            }
        }
    }

    private void handleCollisions() {
        Rectangle ballBounds = ball.getBounds();
        float ballX = ball.getBounds().x;
        float ballY = ball.getBounds().y;
        float ballRadius = ball.getRadius();

        // Wall collisions - increase position correction
        if (ballX - ballRadius <= 0) {
            ball.getBounds().x = ballRadius + 1f; // Stronger correction
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
            ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ballRadius + 5);
            log.info("Ball lost! Resetting...");
            return;
        }

        // Brick collisions FIRST (prioritize brick hits over paddle)
        boolean brickHit = false;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && Intersector.overlaps(ballBounds, brick.getBounds())) {
                Rectangle brickBounds = brick.getBounds();

                // Calculate overlap on each axis
                float overlapLeft = (ballX + ballRadius) - brickBounds.x;
                float overlapRight = (brickBounds.x + brickBounds.width) - (ballX - ballRadius);
                float overlapTop = (brickBounds.y + brickBounds.height) - (ballY - ballRadius);
                float overlapBottom = (ballY + ballRadius) - brickBounds.y;

                float minOverlapX = Math.min(overlapLeft, overlapRight);
                float minOverlapY = Math.min(overlapTop, overlapBottom);

                // Stronger position correction (2.0f instead of 0.1f)
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

                brick.hit();
                if (brick.getType() == Brick.BrickType.BREAKABLE && brick.isDestroyed()) {
                    score += 10;
                    bricksDestroyed++;
                    log.debug("Brick destroyed! Score: {}", score);
                }

                brickHit = true;
                break;
            }
        }

        // Paddle collision - only check if no brick was hit this frame
        if (!brickHit && ball.isActive()) {
            Rectangle paddleBounds = paddle.getBounds();

            // Additional check: ball must be above paddle (not deep inside)
            boolean ballIsAbovePaddle = ballY - ballRadius > paddleBounds.y;

            if (Intersector.overlaps(ballBounds, paddleBounds) &&
                    ball.getVelocity().y < 0 &&
                    ballIsAbovePaddle) {

                // Stronger position correction - place ball well above paddle
                ball.getBounds().y = paddleBounds.y + paddleBounds.height + ballRadius + 2f;

                // Calculate hit position (0 = left edge, 1 = right edge)
                float hitPos = (ballX - paddleBounds.x) / paddleBounds.width;
                hitPos = Math.max(0.1f, Math.min(0.9f, hitPos)); // Clamp to prevent extreme angles

                // Convert to angle: -50° to +50° (narrower range feels better)
                float bounceAngle = (hitPos - 0.5f) * 100f; // -50 to +50 degrees

                // FIXED SPEED instead of accelerating - more predictable gameplay
                float targetSpeed = 350f; // Constant speed

                // Optional: slight speed increase based on how many bricks destroyed
                // More bricks = slightly faster (max +30% speed)
                float speedMultiplier = 1.0f + (bricksDestroyed * 0.002f);
                speedMultiplier = Math.min(speedMultiplier, 1.3f); // Cap at 130%
                float finalSpeed = targetSpeed * speedMultiplier;

                // Convert angle to velocity
                float angleInRadians = (float) Math.toRadians(90 + bounceAngle);
                ball.setVelocity(
                        finalSpeed * (float) Math.cos(angleInRadians),
                        finalSpeed * (float) Math.sin(angleInRadians)
                );

                log.debug("Paddle hit at {}, angle {}°, speed {}",
                        String.format("%.2f", hitPos),
                        String.format("%.1f", bounceAngle),
                        String.format("%.1f", finalSpeed));
            }
        }

        // Check if wave is complete
        if (isWaveComplete()) {
            onWaveComplete();
        }
    }

    private boolean isWaveComplete() {
        for (Brick brick : bricks) {
            if (brick.getType() == Brick.BrickType.BREAKABLE && !brick.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    private void onWaveComplete() {
        int waveBonus = 100 * currentWave;
        score += waveBonus;
        log.info("Wave {} complete! Bonus: {}", currentWave, waveBonus);

        currentWave++;
        ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);

        startWave(currentWave);
    }

    private void updateStats() {
        statsLabel.setText(String.format("Wave: %d | Score: %d | Bricks: %d/%d",
            currentWave, score, bricksDestroyed, countBreakableBricks()));
    }

    private int countBreakableBricks() {
        int count = 0;
        for (Brick brick : bricks) {
            if (brick.getType() == Brick.BrickType.BREAKABLE) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void dispose() {
        super.dispose();
        paddle.dispose();
        ball.dispose();
        for (Brick brick : bricks) {
            brick.dispose();
        }
        bricks.clear();
    }
}
