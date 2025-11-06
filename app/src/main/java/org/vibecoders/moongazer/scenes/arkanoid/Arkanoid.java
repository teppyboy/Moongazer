package org.vibecoders.moongazer.scenes.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.lwjgl.opengl.GL32;
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
    protected Paddle paddle;
    protected Ball ball;
    protected List<Brick> bricks;
    protected BitmapFont font;
    protected BitmapFont fontUI30;
    protected int score = 0;
    protected int lives = 3;
    protected int bricksDestroyed = 0;
    protected boolean isPaused = false;
    protected Brick lastHitBrick = null;
    protected float collisionCooldown = 0f;
    private Texture pixelTexture;

    public Arkanoid(Game game) {
        super(game);
        init();
    }

    protected void init() {
        font = Assets.getFont("ui", 18);
        fontUI30 = Assets.getFont("ui", 30);
        pixelTexture = Assets.getBlackTexture();
        initGameplay();
        log.info("Arkanoid gameplay initialized");
    }

    protected void initGameplay() {
        float paddleX = SIDE_PANEL_WIDTH + (GAMEPLAY_AREA_WIDTH - PADDLE_WIDTH) / 2f;
        float paddleY = 50f;
        paddle = new Paddle(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
        float ballRadius = 12f;
        ball = new Ball(SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH / 2f, paddleY + PADDLE_HEIGHT + ballRadius + 5, ballRadius);
        bricks = new ArrayList<>();
    }

    protected void createBrickGrid(int rows, int cols) {
        bricks.clear();
        float availableWidth = GAMEPLAY_AREA_WIDTH;
        float brickTotalWidth = BRICK_WIDTH + BRICK_PADDING;
        int maxCols = (int) (availableWidth / brickTotalWidth);
        cols = Math.min(cols, maxCols);
        float gridWidth = cols * brickTotalWidth;
        float startX = SIDE_PANEL_WIDTH + (GAMEPLAY_AREA_WIDTH - gridWidth) / 2f;
        float startY = WINDOW_HEIGHT - 100f;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = startX + col * brickTotalWidth;
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
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!isPaused) {
            handleInput(delta);
            updateGameplay(delta);
            handleCollisions();
        }
        renderGameplay(batch);
        renderUI(batch);
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
        paddle.update(delta, SIDE_PANEL_WIDTH, SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH);
        ball.update(delta);
        if (collisionCooldown > 0) {
            collisionCooldown -= delta;
        }
        if (!ball.isActive()) {
            ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        }
    }

    protected void handleCollisions() {
        if (!ball.isActive()) return;
        Rectangle ballBounds = ball.getBounds();
        float ballX = ball.getBounds().x;
        float ballY = ball.getBounds().y;
        float ballRadius = ball.getRadius();
        if (ballX - ballRadius <= SIDE_PANEL_WIDTH) {
            ball.getBounds().x = SIDE_PANEL_WIDTH + ballRadius + 1f;
            ball.reverseX();
        }
        if (ballX + ballRadius >= SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH) {
            ball.getBounds().x = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH - ballRadius - 1f;
            ball.reverseX();
        }
        if (ballY + ballRadius >= WINDOW_HEIGHT) {
            ball.getBounds().y = WINDOW_HEIGHT - ballRadius - 1f;
            ball.reverseY();
        }
        if (ballY - ballRadius <= 0) {
            onBallLost();
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
                if (brick.getType() == Brick.BrickType.BREAKABLE && brick.isDestroyed()) {
                    lastHitBrick = null;
                    onBrickDestroyed(brick);
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
                float bounceAngle = -(hitPos - 0.5f) * 100f;
                float targetSpeed = 350f;
                float speedMultiplier = 1.0f + (bricksDestroyed * 0.002f);
                speedMultiplier = Math.min(speedMultiplier, 1.3f);
                float finalSpeed = targetSpeed * speedMultiplier;
                float angleInRadians = (float) Math.toRadians(90 + bounceAngle);
                ball.setVelocity(
                        finalSpeed * (float) Math.cos(angleInRadians),
                        finalSpeed * (float) Math.sin(angleInRadians)
                );
            }
        }
        if (checkLevelComplete()) {
            onLevelComplete();
        }
    }

    protected void renderGameplay(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.6f);
        batch.draw(pixelTexture, 0, 0, SIDE_PANEL_WIDTH, WINDOW_HEIGHT);
        batch.draw(pixelTexture, SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH, 0, SIDE_PANEL_WIDTH, WINDOW_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
        paddle.render(batch);
        ball.render(batch);
        for (Brick brick : bricks) {
            brick.render(batch);
        }
    }

    protected void renderUI(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, 1f);
        fontUI30.setColor(Color.WHITE);
        String scoreLabel = "Score";
        String scoreValue = String.format("%d", score);
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        layout.setText(fontUI30, scoreLabel);
        float scoreLabelX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, scoreLabel, scoreLabelX, WINDOW_HEIGHT - 50);
        layout.setText(fontUI30, scoreValue);
        float scoreValueX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, scoreValue, scoreValueX, WINDOW_HEIGHT - 60 - layout.height);
        String bestLabel = "Best";
        String bestValue = String.format("%d", score);
        layout.setText(fontUI30, bestLabel);
        float bestLabelX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, bestLabel, bestLabelX, WINDOW_HEIGHT - 140);
        layout.setText(fontUI30, bestValue);
        float bestValueX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, bestValue, bestValueX, WINDOW_HEIGHT - 150 - layout.height);
        String livesIcon = "";
        for (int i = 0; i < lives; i++) {
            livesIcon += "♥ ";
        }
        fontUI30.draw(batch, livesIcon, 30, WINDOW_HEIGHT - 190);
        fontUI30.draw(batch, "Powerups", SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH + 30, WINDOW_HEIGHT - 50);
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