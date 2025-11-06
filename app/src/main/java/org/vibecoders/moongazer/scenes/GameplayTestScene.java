package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.arkanoid.*;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private ShapeRenderer shapeRenderer;
    private boolean showHitboxes = true;
    private SpriteBatch cachedBatch;
    private int currentWave = 1;
    private float unbreakableChance = 0.1f;
    private Brick lastHitBrick = null;
    private float collisionCooldown = 0f;
    private static final float COLLISION_COOLDOWN_TIME = 0.05f;
    private static final Logger log = LoggerFactory.getLogger(GameplayTestScene.class);
    private static final float BRICK_WIDTH = 60f;
    private static final float BRICK_HEIGHT = 60f;
    private static final float BRICK_PADDING = 2f;
    private static final int BRICK_COLS = 30;

    public GameplayTestScene(Game game) {
        super(game);
        shapeRenderer = new ShapeRenderer();
        initGameplay();
        initUI();
        game.stage.addActor(root);
    }

    private void initGameplay() {
        float paddleWidth = 120f;
        float paddleHeight = 20f;
        float paddleX = (WINDOW_WIDTH - paddleWidth) / 2f;
        float paddleY = 50f;
        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight);
        float ballRadius = 12f;
        ball = new Ball(WINDOW_WIDTH / 2f, paddleY + paddleHeight + ballRadius + 5, ballRadius);
        bricks = new ArrayList<>();
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
        lastHitBrick = null;
        collisionCooldown = 0f;
        initGameplay();
        log.info("GameplayTestScene reinitialized");
    }

    private void startWave(int wave) {
        bricksDestroyed = 0;
        int rows = Math.min(5 + (wave / 2), 10);
        unbreakableChance = Math.min(0.1f + (wave * 0.02f), 0.4f);
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
            "GAMEPLAY TEST - SPACE: launch | A/D or ARROWS: move | H: toggle hitboxes | ESC: return",
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
        this.cachedBatch = batch;
        batch.setColor(1, 1, 1, 1);
        boolean isVisible = root.isVisible();
        if (isVisible && !wasVisible) {
            reinitGameplay();
        }
        wasVisible = isVisible;
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        paddle.update(delta, WINDOW_WIDTH);
        ball.update(delta);
        if (collisionCooldown > 0) {
            collisionCooldown -= delta;
        }
        if (!ball.isActive()) {
            ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        }
        handleCollisions();
        paddle.render(batch);
        ball.render(batch);
        for (Brick brick : bricks) {
            brick.render(batch);
        }
        batch.end();
        if (showHitboxes) {
            renderHitboxes();
        }
        batch.begin();
        updateStats();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !ball.isActive()) {
            ball.launch();
            Audio.playSfxConfirm();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            showHitboxes = !showHitboxes;
            log.info("Hitbox rendering: {}", showHitboxes ? "ON" : "OFF");
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
        if (ballY - ballRadius <= 0) {
            ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ballRadius + 5);
            log.info("Ball lost! Resetting...");
            return;
        }
        boolean brickHit = false;
        for (Brick brick : bricks) {
            if (!brick.isDestroyed() && Intersector.overlaps(ballBounds, brick.getBounds())) {
                if (collisionCooldown > 0 && brick == lastHitBrick) {
                    continue;
                }
                Rectangle brickBounds = brick.getBounds();
                float overlapLeft = (ballX + ballRadius) - brickBounds.x;
                float overlapRight = (brickBounds.x + brickBounds.width) - (ballX - ballRadius);
                float overlapTop = (brickBounds.y + brickBounds.height) - (ballY - ballRadius);
                float overlapBottom = (ballY + ballRadius) - brickBounds.y;
                float minOverlapX = Math.min(overlapLeft, overlapRight);
                float minOverlapY = Math.min(overlapTop, overlapBottom);
                float separationDistance = ballRadius + 1.0f;
                if (minOverlapX < minOverlapY) {
                    ball.reverseX();
                    if (overlapLeft < overlapRight) {
                        ball.getBounds().x = brickBounds.x - ballRadius - separationDistance;
                    } else {
                        ball.getBounds().x = brickBounds.x + brickBounds.width + ballRadius + separationDistance;
                    }
                } else {
                    ball.reverseY();
                    if (overlapTop < overlapBottom) {
                        ball.getBounds().y = brickBounds.y + brickBounds.height + ballRadius + separationDistance;
                    } else {
                        ball.getBounds().y = brickBounds.y - ballRadius - separationDistance;
                    }
                }
                brick.hit();
                lastHitBrick = brick;
                collisionCooldown = COLLISION_COOLDOWN_TIME;
                log.info("BALL HIT BRICK - Ball pos: ({}, {}), Velocity: ({}, {}), Collision: {}, Brick pos: ({}, {})",
                    String.format("%.2f", ballX),
                    String.format("%.2f", ballY),
                    String.format("%.2f", ball.getVelocity().x),
                    String.format("%.2f", ball.getVelocity().y),
                    (minOverlapX < minOverlapY) ? "X-axis" : "Y-axis",
                    String.format("%.2f", brickBounds.x),
                    String.format("%.2f", brickBounds.y)
                );
                if (brick.getType() == Brick.BrickType.BREAKABLE && brick.isDestroyed()) {
                    score += 10;
                    bricksDestroyed++;
                    lastHitBrick = null;
                    // log.debug("Brick destroyed! Score: {}", score);
                }
                brickHit = true;
                break;
            }
        }
        if (!brickHit && ball.isActive()) {
            Rectangle paddleBounds = paddle.getBounds();
            boolean ballIsAbovePaddle = ballY - ballRadius > paddleBounds.y;
            if (Intersector.overlaps(ballBounds, paddleBounds) &&
                    ball.getVelocity().y < 0 &&
                    ballIsAbovePaddle) {
                ball.getBounds().y = paddleBounds.y + paddleBounds.height + ballRadius + 2f;
                float hitPos = (ballX - paddleBounds.x) / paddleBounds.width;
                hitPos = Math.max(0.1f, Math.min(0.9f, hitPos));
                float bounceAngle = (hitPos - 0.5f) * 100f;
                float targetSpeed = 350f;
                float speedMultiplier = 1.0f + (bricksDestroyed * 0.002f);
                speedMultiplier = Math.min(speedMultiplier, 1.3f);
                float finalSpeed = targetSpeed * speedMultiplier;
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
        lastHitBrick = null;
        collisionCooldown = 0f;
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
    
    private void renderHitboxes() {
        shapeRenderer.setProjectionMatrix(cachedBatch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(0, 1, 0, 1);
        Rectangle ballBounds = ball.getBounds();
        float ballCenterX = ballBounds.x + ballBounds.width / 2f;
        float ballCenterY = ballBounds.y + ballBounds.height / 2f;
        float ballRadius = ball.getRadius();
        shapeRenderer.circle(ballCenterX, ballCenterY, ballRadius, 32);
        shapeRenderer.setColor(0, 1, 0, 0.5f);
        shapeRenderer.rect(ballBounds.x, ballBounds.y, ballBounds.width, ballBounds.height);
        shapeRenderer.setColor(1, 1, 0, 1);
        shapeRenderer.circle(ballCenterX, ballCenterY, 2, 8);
        shapeRenderer.setColor(0, 0.5f, 1, 1);
        Rectangle paddleBounds = paddle.getBounds();
        shapeRenderer.rect(paddleBounds.x, paddleBounds.y, paddleBounds.width, paddleBounds.height);
        shapeRenderer.setColor(0, 1, 1, 1);
        float paddleTop = paddleBounds.y + paddleBounds.height;
        shapeRenderer.line(paddleBounds.x, paddleTop, paddleBounds.x + paddleBounds.width, paddleTop);
        for (Brick brick : bricks) {
            if (!brick.isDestroyed()) {
                Rectangle brickBounds = brick.getBounds();
                if (brick.getType() == Brick.BrickType.UNBREAKABLE) {
                    shapeRenderer.setColor(1, 0, 0, 1);
                } else {
                    shapeRenderer.setColor(1, 0.5f, 0, 1);
                }
                shapeRenderer.rect(brickBounds.x, brickBounds.y, brickBounds.width, brickBounds.height);
                shapeRenderer.setColor(1, 1, 1, 0.5f);
                float brickCenterX = brickBounds.x + brickBounds.width / 2f;
                float brickCenterY = brickBounds.y + brickBounds.height / 2f;
                shapeRenderer.circle(brickCenterX, brickCenterY, 2, 8);
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        paddle.dispose();
        ball.dispose();
        for (Brick brick : bricks) {
            brick.dispose();
        }
        bricks.clear();
    }
}
