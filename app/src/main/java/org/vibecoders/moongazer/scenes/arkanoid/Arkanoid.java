package org.vibecoders.moongazer.scenes.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.lwjgl.opengl.GL32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.*;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.Scene;
import org.vibecoders.moongazer.ui.PauseMenu;

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
    protected Brick lastHitBrick = null;
    protected float collisionCooldown = 0f;
    private Texture pixelTexture;
    private Texture heartTexture;
    private boolean heartBlinking = false;
    private float heartBlinkTimer = 0f;
    private static final float HEART_BLINK_DURATION = 1.5f;
    private static final float HEART_BLINK_SPEED = 0.15f;
    protected ShapeRenderer shapeRenderer;
    protected boolean showHitboxes = false;
    protected PauseMenu pauseMenu;
    private FrameBuffer gameFrameBuffer;
    private Texture gameSnapshot;
    private float pauseCooldown = 0f;
    private static final float PAUSE_COOLDOWN_TIME = 0.2f;
    private InputMultiplexer inputMultiplexer;
    private InputAdapter gameInputAdapter;
    private boolean gameInputEnabled = true;
    private boolean escKeyDownInGame = false;

    public Arkanoid(Game game) {
        super(game);
        init();
    }

    protected void init() {
        font = Assets.getFont("ui", 18);
        fontUI30 = Assets.getFont("ui", 30);
        pixelTexture = Assets.getBlackTexture();
        heartTexture = Assets.getAsset("textures/arkanoid/heart.png", Texture.class);
        shapeRenderer = new ShapeRenderer();

        // Initialize frame buffer for capturing game state
        gameFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, WINDOW_WIDTH, WINDOW_HEIGHT, false);

        // Initialize input handling
        setupInputHandling();

        // Initialize pause menu
        pauseMenu = new PauseMenu();
        setupPauseMenuCallbacks();

        initGameplay();
        log.info("Arkanoid gameplay initialized");
    }

    private void setupInputHandling() {
        // Create input adapter for game-specific controls (including ESC for pause)
        gameInputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (gameInputEnabled && !pauseMenu.isPaused() && pauseCooldown <= 0) {
                        escKeyDownInGame = true;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    if (gameInputEnabled && escKeyDownInGame && !pauseMenu.isPaused()) {
                        escKeyDownInGame = false;
                        gameInputEnabled = false; // Disable immediately to prevent re-triggering
                        onPausePressed();
                        return true;
                    }

                    if (keycode == Input.Keys.ESCAPE) {
                        escKeyDownInGame = false;
                    }
                }
                return false;
            }
        };

        // Create multiplexer to handle both game.stage and game input
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameInputAdapter);
        inputMultiplexer.addProcessor(game.stage);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    protected void setupPauseMenuCallbacks() {
        pauseMenu.setOnResume(() -> {
            log.info("Resuming game from pause menu");
            pauseCooldown = PAUSE_COOLDOWN_TIME;
            gameInputEnabled = true; // Re-enable game input
            escKeyDownInGame = false; // Reset ESC key state
            restoreInputProcessor();
        });

        pauseMenu.setOnRestart(() -> {
            log.info("Restarting game");
            pauseCooldown = 0;
            gameInputEnabled = true;
            escKeyDownInGame = false;
            restartGame();
            restoreInputProcessor();
        });

        pauseMenu.setOnMainMenu(() -> {
            log.info("Returning to main menu");
            returnToMainMenu();
        });

        pauseMenu.setOnQuit(() -> {
            log.info("Quitting game");
            Gdx.app.exit();
        });
    }

    protected void restartGame() {
        // Reset game state
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        initGameplay();
    }

    protected void restoreInputProcessor() {
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    protected abstract void returnToMainMenu();

    protected void initGameplay() {
        float paddleX = SIDE_PANEL_WIDTH + (GAMEPLAY_AREA_WIDTH - PADDLE_WIDTH) / 2f;
        float paddleY = 50f;
        paddle = new Paddle(paddleX, paddleY, PADDLE_WIDTH, PADDLE_HEIGHT);
        float ballRadius = 12f;
        ball = new Ball(SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH / 2f, paddleY + PADDLE_HEIGHT + ballRadius + 5,
                ballRadius);
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

        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update pause cooldown
        if (pauseCooldown > 0) {
            pauseCooldown -= delta;
        }

        // Only update game logic if not paused
        if (!pauseMenu.isPaused()) {
            handleInput(delta);
            updateGameplay(delta);
            handleCollisions();
        }

        // Render gameplay
        renderGameplay(batch);
        renderUI(batch);

        // If paused, capture game state and render pause menu
        if (pauseMenu.isPaused()) {
            // Capture the current screen to a texture for the blur effect
            if (gameSnapshot == null) {
                captureGameSnapshot(batch);
            }
            pauseMenu.render(batch, gameSnapshot);
        } else {
            // Clear snapshot reference when not paused (don't dispose - it's the framebuffer's texture)
            if (gameSnapshot != null) {
                gameSnapshot = null;
            }
        }
    }

    private void captureGameSnapshot(SpriteBatch batch) {
        batch.end();

        // Render game to framebuffer
        gameFrameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        renderGameplay(batch);
        renderUI(batch);
        batch.end();

        gameFrameBuffer.end();

        // Get the texture from framebuffer
        gameSnapshot = gameFrameBuffer.getColorBufferTexture();

        batch.begin();
    }

    protected void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !ball.isActive()) {
            ball.launch();
            log.info("Ball launched!");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F3) && Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showHitboxes = !showHitboxes;
            log.info("Hitbox rendering: {}", showHitboxes ? "ON" : "OFF");
        }
    }

    protected void updateGameplay(float delta) {
        paddle.update(delta, SIDE_PANEL_WIDTH, SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH);
        ball.update(delta);
        if (collisionCooldown > 0) {
            collisionCooldown -= delta;
        }

        // Update heart blink animation
        if (heartBlinking) {
            heartBlinkTimer += delta;
            if (heartBlinkTimer >= HEART_BLINK_DURATION) {
                heartBlinking = false;
                heartBlinkTimer = 0f;
            }
        }

        if (!ball.isActive()) {
            ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        }
    }

    protected void handleCollisions() {
        if (!ball.isActive())
            return;
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
                        finalSpeed * (float) Math.sin(angleInRadians));
            }
        }
        if (checkLevelComplete()) {
            onLevelComplete();
        }
    }

    protected void renderGameplay(SpriteBatch batch) {
        batch.setColor(0f, 0f, 0f, 0.3f);
        batch.draw(pixelTexture, 0, 0, SIDE_PANEL_WIDTH, WINDOW_HEIGHT);
        batch.draw(pixelTexture, SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH, 0, SIDE_PANEL_WIDTH, WINDOW_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
        paddle.render(batch);
        ball.render(batch);
        for (Brick brick : bricks) {
            brick.render(batch);
        }
        if (showHitboxes) {
            // batch.end();
            renderHitboxes(batch);
            // batch.begin();
        }
    }

    protected void renderHitboxes(SpriteBatch batch) {
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setColor(0, 1, 0, 1);
        Rectangle ballBounds = ball.getBounds();
        float ballCenterX = ballBounds.x;
        float ballCenterY = ballBounds.y;
        float ballRadius = ball.getRadius();
        shapeRenderer.circle(ballCenterX, ballCenterY, ballRadius, 32);
        shapeRenderer.setColor(0, 1, 0, 0.5f);
        shapeRenderer.rect(ballBounds.x - ballRadius, ballBounds.y - ballRadius, ballRadius * 2, ballRadius * 2);
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

        // Render hearts at bottom left as "heart icon x lives"
        float heartSize = 50f; // Size of heart icon
        float heartStartX = 30f; // Left margin
        float heartStartY = 40f; // Bottom margin

        // Calculate blink alpha for both heart icon and lives text
        float blinkAlpha = 1.0f;
        if (heartBlinking) {
            // Create a blinking effect by oscillating alpha
            float blinkCycle = (heartBlinkTimer % HEART_BLINK_SPEED) / HEART_BLINK_SPEED;
            blinkAlpha = blinkCycle < 0.5f ? 0.2f : 1.0f;
        }

        // Save original colors
        Color batchColor = batch.getColor().cpy();
        Color fontColor = fontUI30.getColor().cpy();

        // Draw heart icon with blinking effect
        batch.setColor(1f, 1f, 1f, blinkAlpha);
        batch.draw(heartTexture, heartStartX, heartStartY, heartSize, heartSize);

        // Draw "x lives" text with blinking effect
        fontUI30.setColor(fontColor.r, fontColor.g, fontColor.b, blinkAlpha);
        String livesText = " x " + lives;
        fontUI30.draw(batch, livesText, heartStartX + heartSize + 5f, heartStartY + heartSize - 5f);

        // IMPORTANT: Restore colors to full opacity before drawing anything else
        batch.setColor(1f, 1f, 1f, 1f);
        fontUI30.setColor(fontColor.r, fontColor.g, fontColor.b, 1f);

        String powerupsText = "Powerups";
        layout.setText(fontUI30, powerupsText);
        float powerupsX = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH + (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, powerupsText, powerupsX, WINDOW_HEIGHT - 50);
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
        heartBlinking = true;
        heartBlinkTimer = 0f;
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
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (bricks != null) {
            bricks.clear();
        }
        if (pauseMenu != null) {
            pauseMenu.dispose();
        }
        if (gameFrameBuffer != null) {
            gameFrameBuffer.dispose();
        }
        if (gameSnapshot != null) {
            gameSnapshot.dispose();
        }
    }
}