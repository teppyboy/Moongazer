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
import com.badlogic.gdx.utils.TimeUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.*;
import org.vibecoders.moongazer.arkanoid.powerups.*;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;
import org.vibecoders.moongazer.scenes.Scene;
import org.vibecoders.moongazer.ui.PauseMenu;
import org.vibecoders.moongazer.ui.GameOverMenu;

import java.util.ArrayList;
import java.util.List;

import static org.vibecoders.moongazer.Constants.*;

public abstract class Arkanoid extends Scene {
    protected static final Logger log = LoggerFactory.getLogger(Arkanoid.class);
    protected Paddle paddle;
    protected List<Ball> balls;
    protected List<Brick> bricks;
    protected BitmapFont font;
    protected BitmapFont fontUI30;
    protected int score = 0;
    protected int bestScore = 0;
    public int lives = 3;
    protected int bricksDestroyed = 0;
    protected int combo = 0;
    protected int maxCombo = 0;
    protected Brick lastHitBrick = null;
    protected float collisionCooldown = 0f;
    protected static final int MAX_BALLS = 3;

    private float stuckDetectionTimer = 0f;
    private float minBallY = Float.MAX_VALUE;
    private float maxBallY = Float.MIN_VALUE;
    private static final float STUCK_CHECK_DURATION = 5.0f;
    private static final float STUCK_Y_RANGE_THRESHOLD = 100f;
    private static final float MIN_HORIZONTAL_VELOCITY_THRESHOLD = 50f;
    private static final float MIN_HORIZONTAL_RATIO = 0.3f;

    private Texture pixelTexture;
    private Texture heartTexture;
    private Texture backgroundTexture = null;
    protected boolean heartBlinking = false;
    protected float heartBlinkTimer = 0f;
    private static final float HEART_BLINK_DURATION = 1.5f;
    private static final float HEART_BLINK_SPEED = 0.15f;
    protected ShapeRenderer shapeRenderer;
    protected boolean showHitboxes = false;
    protected PauseMenu pauseMenu;
    protected GameOverMenu gameOverMenu;
    private FrameBuffer gameFrameBuffer;
    private Texture gameSnapshot;
    protected List<PowerUp> activePowerUps;
    protected List<ActivePowerUpEffect> activePowerUpEffects;
    private float pauseCooldown = 0f;
    private static final float PAUSE_COOLDOWN_TIME = 0.2f;
    private InputMultiplexer inputMultiplexer;
    private InputAdapter gameInputAdapter;
    protected boolean gameInputEnabled = true;
    private boolean escKeyDownInGame = false;
    protected PaddleAI paddleAI;

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
        gameFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, WINDOW_WIDTH, WINDOW_HEIGHT, false);
        paddleAI = new ArkanoidAI();
        setupInputHandling();
        pauseMenu = new PauseMenu();
        setupPauseMenuCallbacks();

        gameOverMenu = new GameOverMenu();
        setupGameOverMenuCallbacks();

        initGameplay();
        log.info("Arkanoid gameplay initialized");
    }

    private void setupInputHandling() {
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
                        gameInputEnabled = false;
                        onPausePressed();
                        return true;
                    }
                    escKeyDownInGame = false;
                }
                return false;
            }
        };
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gameInputAdapter);
        inputMultiplexer.addProcessor(game.stage);

        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    protected void setupPauseMenuCallbacks() {
        pauseMenu.setOnResume(() -> {
            log.info("Resuming game from pause menu");
            pauseCooldown = PAUSE_COOLDOWN_TIME;
            gameInputEnabled = true;
            escKeyDownInGame = false;
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

    protected void setupGameOverMenuCallbacks() {
        gameOverMenu.setOnPlayAgain(() -> {
            log.info("Playing again from game over menu");
            Audio.stopGameOverMusic();
            gameInputEnabled = true;
            restartGame();
            restoreInputProcessor();
        });

        gameOverMenu.setOnMainMenu(() -> {
            log.info("Returning to main menu from game over");
            Audio.stopGameOverMusic();
            returnToMainMenu();
        });

        gameOverMenu.setOnQuit(() -> {
            log.info("Quitting game from game over");
            Gdx.app.exit();
        });
    }

    protected void restartGame() {
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        combo = 0;
        maxCombo = 0;
        heartBlinking = false;
        heartBlinkTimer = 0f;
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
        Ball mainBall = new Ball(SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH / 2f, paddleY + PADDLE_HEIGHT + ballRadius + 5,
                ballRadius);
        balls = new ArrayList<>();
        balls.add(mainBall);
        bricks = new ArrayList<>();
        activePowerUps = new ArrayList<>();
        activePowerUpEffects = new ArrayList<>();
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

    protected void setBackground(Texture texture) {
        this.backgroundTexture = texture;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!pauseMenu.isPaused() && !gameOverMenu.isVisible() &&
            Gdx.input.getInputProcessor() != inputMultiplexer) {
            restoreInputProcessor();
        }

        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (pauseCooldown > 0) {
            pauseCooldown -= delta;
        }

        if (!pauseMenu.isPaused() && !gameOverMenu.isVisible()) {
            handleInput(delta);
            updateGameplay(delta);
            handleCollisions();
        }

        renderGameplay(batch);
        renderUI(batch);

        if (pauseMenu.isPaused()) {
            if (gameSnapshot == null) {
                batch.end();
                gameFrameBuffer.begin();
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                renderGameplay(batch);
                renderUI(batch);
                batch.end();
                gameFrameBuffer.end();
                gameSnapshot = gameFrameBuffer.getColorBufferTexture();
                batch.begin();
            }
            pauseMenu.render(batch, gameSnapshot);
        }
        else if (gameOverMenu.isVisible()) {
            if (gameSnapshot == null) {
                batch.end();
                gameFrameBuffer.begin();
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                batch.begin();
                renderGameplay(batch);
                renderUI(batch);
                batch.end();
                gameFrameBuffer.end();
                gameSnapshot = gameFrameBuffer.getColorBufferTexture();
                batch.begin();
            }
            gameOverMenu.render(batch, gameSnapshot);
        }
        else {
            if (gameSnapshot != null) {
                gameSnapshot = null;
            }
        }
    }

    protected void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            boolean anyLaunched = false;
            for (Ball ball : balls) {
                if (!ball.isActive()) {
                    ball.launch();
                    anyLaunched = true;
                }
            }
            if (anyLaunched) {
                log.info("Ball(s) launched!");
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.F3) && Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            showHitboxes = !showHitboxes;
            log.info("Hitbox rendering: {}", showHitboxes ? "ON" : "OFF");
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            paddleAI.setEnabled(!paddleAI.isEnabled());
            log.info("AI mode: {}", paddleAI.isEnabled() ? "ENABLED" : "DISABLED");
        }
    }

    protected void updateGameplay(float delta) {
        // Update AI before paddle
        if (paddleAI.isEnabled()) {
            paddleAI.update(paddle, balls, activePowerUps);
        }

        paddle.update(delta, SIDE_PANEL_WIDTH, SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH);
        paddle.cleanupBullets(WINDOW_HEIGHT);

        for (Ball ball : balls) {
            ball.update(delta);
        }

        for (Brick brick : bricks) {
            brick.update(delta);
        }

        for (PowerUp powerUp : activePowerUps) {
            powerUp.update(delta);
        }
        for (int i = activePowerUpEffects.size() - 1; i >= 0; i--) {
            ActivePowerUpEffect activeEffect = activePowerUpEffects.get(i);
            if (activeEffect.hasExpired()) {
                activeEffect.removeEffect(this);
                activePowerUpEffects.remove(i);
                log.info("{} effect expired!", activeEffect.getEffectType());
            }
        }
        if (collisionCooldown > 0) {
            collisionCooldown -= delta;
        }
        if (heartBlinking) {
            heartBlinkTimer += delta;
            if (heartBlinkTimer >= HEART_BLINK_DURATION) {
                heartBlinking = false;
                heartBlinkTimer = 0f;
            }
        }

        if (balls.size() == 1 && !balls.get(0).isActive()) {
            Ball mainBall = balls.get(0);
            mainBall.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + mainBall.getRadius() + 5);
        }

        if (!balls.isEmpty() && balls.get(0).isActive()) {
            Ball mainBall = balls.get(0);
            float currentBallY = mainBall.getBounds().y;

            stuckDetectionTimer += delta;

            if (currentBallY < minBallY) minBallY = currentBallY;
            if (currentBallY > maxBallY) maxBallY = currentBallY;

            if (stuckDetectionTimer >= STUCK_CHECK_DURATION) {
                float yRange = maxBallY - minBallY;

                if (yRange < STUCK_Y_RANGE_THRESHOLD) {
                    log.warn("Ball stuck detected! Y range over {}s: {}px (threshold: {}px)",
                             STUCK_CHECK_DURATION, yRange, STUCK_Y_RANGE_THRESHOLD);
                    log.warn("Applying escape velocity...");

                    float currentVelX = mainBall.getVelocity().x;
                    float currentVelY = mainBall.getVelocity().y;
                    float speed = (float) Math.sqrt(currentVelX * currentVelX + currentVelY * currentVelY);

                    float escapeAngle = (float) Math.toRadians(30 + Math.random() * 30);
                    float directionX = currentVelX > 0 ? 1 : -1;

                    mainBall.setVelocity(
                        directionX * speed * (float) Math.cos(escapeAngle),
                        speed * (float) Math.sin(escapeAngle)
                    );
                }

                stuckDetectionTimer = 0f;
                minBallY = Float.MAX_VALUE;
                maxBallY = Float.MIN_VALUE;
            }
        } else {
            stuckDetectionTimer = 0f;
            minBallY = Float.MAX_VALUE;
            maxBallY = Float.MIN_VALUE;
        }
    }

    private void adjustBallVelocityIfTooVertical(Ball ball, boolean preserveHorizontalDirection) {
        if (Math.abs(ball.getVelocity().x) < MIN_HORIZONTAL_VELOCITY_THRESHOLD) {
            float currentVelX = ball.getVelocity().x;
            float currentVelY = ball.getVelocity().y;
            float speed = (float) Math.sqrt(currentVelX * currentVelX + currentVelY * currentVelY);
            float directionX = preserveHorizontalDirection ? (currentVelX >= 0 ? 1 : -1) : (Math.random() > 0.5 ? 1 : -1);
            float horizontalVel = speed * MIN_HORIZONTAL_RATIO * directionX;
            float verticalVel = (float) Math.sqrt(speed * speed - horizontalVel * horizontalVel) * (currentVelY >= 0 ? 1 : -1);
            ball.setVelocity(horizontalVel, verticalVel);
            log.debug("Adjusted velocity to prevent vertical stuck: ({}, {})", horizontalVel, verticalVel);
        }
    }

    protected void handleCollisions() {
        for (int ballIndex = balls.size() - 1; ballIndex >= 0; ballIndex--) {
            Ball ball = balls.get(ballIndex);

            if (!ball.isActive())
                continue;

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
                balls.remove(ballIndex);
                log.info("Ball lost! Remaining balls: {}", balls.size());
                if (balls.isEmpty()) {
                    onBallLost();
                }
                continue;
            }

            boolean brickHit = false;
            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && Intersector.overlaps(ballBounds, brick.getBounds())) {
                    if (collisionCooldown > 0 && brick == lastHitBrick) {
                        continue;
                    }

                    boolean isSuperBallMode = ball.isSuperBall();
                    boolean isBreakableBrick = brick.getType() == Brick.BrickType.BREAKABLE;

                    if (isSuperBallMode && isBreakableBrick) {
                        brick.hit();
                        lastHitBrick = brick;
                        collisionCooldown = COLLISION_COOLDOWN_TIME;

                        combo++;
                        if (combo > maxCombo) {
                            maxCombo = combo;
                        }

                        float multiplier = 1.0f + (combo * 0.03f);
                        int baseScore = 10;
                        int scoreGain = (int) (baseScore * multiplier);
                        score += scoreGain;
                        if (score > bestScore) {
                            bestScore = score;
                        }
                        log.debug("Breakable brick hit! Combo: {}x, Multiplier: {}x, Score gained: {}, Total: {}",
                                  combo, multiplier, scoreGain, score);

                        if (brick.isDestroyed()) {
                            lastHitBrick = null;
                            onBrickDestroyed(brick);

                            if (brick.getPowerUpType() != Brick.PowerUpType.NONE) {
                                spawnRandomPowerUp(brick);
                            }
                        }
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

                    boolean isUnbreakableBrick = brick.getType() == Brick.BrickType.UNBREAKABLE;

                    if (isUnbreakableBrick && combo > 0) {
                        log.info("Combo broken by unbreakable brick! Lost combo: {}x", combo);
                        combo = 0;
                    }

                    if (minOverlapX < minOverlapY) {
                        ball.reverseX();
                        if (overlapLeft < overlapRight) {
                            ball.getBounds().x = brickBounds.x - ballRadius - separationDistance;
                        } else {
                            ball.getBounds().x = brickBounds.x + brickBounds.width + ballRadius + separationDistance;
                        }

                        if (isUnbreakableBrick) {
                            adjustBallVelocityIfTooVertical(ball, true);
                        }
                    } else {
                        ball.reverseY();
                        if (overlapTop < overlapBottom) {
                            ball.getBounds().y = brickBounds.y + brickBounds.height + ballRadius + separationDistance;
                        } else {
                            ball.getBounds().y = brickBounds.y - ballRadius - separationDistance;
                        }

                        if (isUnbreakableBrick) {
                            adjustBallVelocityIfTooVertical(ball, true);
                        }
                    }
                    brick.hit();
                    lastHitBrick = brick;
                    collisionCooldown = COLLISION_COOLDOWN_TIME;

                    if (brick.getType() == Brick.BrickType.BREAKABLE) {
                        combo++;
                        if (combo > maxCombo) {
                            maxCombo = combo;
                        }

                        float multiplier = 1.0f + (combo * 0.03f);
                        int baseScore = 10;
                        int scoreGain = (int) (baseScore * multiplier);
                        score += scoreGain;
                        log.debug("Breakable brick hit! Combo: {}x, Multiplier: {}x, Score gained: {}, Total: {}",
                                  combo, multiplier, scoreGain, score);
                    }

                    if (brick.getType() == Brick.BrickType.BREAKABLE && brick.isDestroyed()) {
                        lastHitBrick = null;
                        onBrickDestroyed(brick);

                        if (brick.getPowerUpType() != Brick.PowerUpType.NONE) {
                            spawnRandomPowerUp(brick);
                        }
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
                    paddle.onBallHit(ball.getVelocity().y);
                    Audio.playSfxPaddleHit();
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
        }

        handlePowerUpCollisions();
        handleBulletCollisions();
        if (checkLevelComplete()) {
            onLevelComplete();
        }
    }

    private void handlePowerUpCollisions() {
        for (int i = activePowerUps.size() - 1; i >= 0; i--) {
            PowerUp powerUp = activePowerUps.get(i);

            if (powerUp.y < 0) {
                activePowerUps.remove(i);
                continue;
            }

            Rectangle paddleBounds = paddle.getBounds();
            if (powerUp.x < paddleBounds.x + paddleBounds.width &&
                    powerUp.x + powerUp.width > paddleBounds.x &&
                    powerUp.y < paddleBounds.y + paddleBounds.height &&
                    powerUp.y + powerUp.height > paddleBounds.y) {
                Audio.playSfxPowerupReceive();
                boolean canStack = canPowerUpStack(powerUp.getName());

                if (!canStack) {
                    log.info("{} cannot stack with active effects, ignored", powerUp.getName());
                    activePowerUps.remove(i);
                    continue;
                }

                boolean effectExists = false;
                for (ActivePowerUpEffect activeEffect : activePowerUpEffects) {
                    if (activeEffect.getEffectType().equals(powerUp.getName())) {
                        activeEffect.refreshDuration();
                        log.info("{} duration refreshed!", powerUp.getName());
                        effectExists = true;
                        break;
                    }
                }

                if (!effectExists) {
                    powerUp.applyEffect(this);

                    if (powerUp.getDuration() > 0) {
                        activePowerUpEffects.add(new ActivePowerUpEffect(powerUp));
                        log.info("{} activated for {} seconds",
                                powerUp.getName(),
                                powerUp.getDuration() / 1000f);
                    } else if (powerUp.getDuration() == -1) {
                        log.info("{} collected (permanent)", powerUp.getName());
                    }
                }

                activePowerUps.remove(i);
            }
        }
    }

    private void handleBulletCollisions() {
        List<Bullet> bullets = paddle.getBullets();

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            Rectangle bulletBounds = bullet.getBounds();

            for (Brick brick : bricks) {
                if (!brick.isDestroyed() && Intersector.overlaps(bulletBounds, brick.getBounds())) {
                    brick.hit();

                    if (brick.isDestroyed()) {
                        onBrickDestroyed(brick);

                        if (brick.getPowerUpType() != Brick.PowerUpType.NONE) {
                            spawnRandomPowerUp(brick);
                        }
                    }

                    bullet.setActive(false);
                    break;
                }
            }
        }
    }

    private void spawnRandomPowerUp(Brick brick) {
        PowerUpFactory factory = new ClassicPowerUpFactory();
        PowerUp powerUp = null;

        float powerUpX = brick.getX() + brick.getWidth() / 2f - 16;
        float powerUpY = brick.getY();
        float powerUpWidth = 32;
        float powerUpHeight = 32;

        if (brick.getPowerUpType() != Brick.PowerUpType.NONE) {
            switch (brick.getPowerUpType()) {
                case EXPAND_PADDLE:
                    powerUp = factory.createExpandPaddle(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
                    break;
                case EXTRA_LIFE:
                    powerUp = factory.createExtraLife(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
                    break;
                case FAST_BALL:
                    powerUp = factory.createFastBall(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
                    break;
                case SLOW_BALL:
                    powerUp = factory.createSlowBall(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
                    break;
                case MULTI_BALL:
                    powerUp = factory.createMultiBall(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
                    break;
                case SUPER_BALL:
                    powerUp = factory.createSuperBall(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
                    break;
                case BULLET:
                    powerUp = factory.createBulletPaddle(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
                    break;
                case EXPLOSIVE:
                    log.warn("Power-up type {} not yet implemented", brick.getPowerUpType());
                    return;
                case NONE:
                    return;
            }
        } else {
            double rand = Math.random();
            if (rand < 0.15) {
                powerUp = factory.createExpandPaddle(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
            } else if (rand < 0.30){
                powerUp = factory.createExtraLife(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
            } else if (rand < 0.45){
                powerUp = factory.createFastBall(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
            } else if (rand < 0.60){
                powerUp = factory.createSlowBall(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
            } else if (rand < 0.75){
                powerUp = factory.createMultiBall(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
            } else if (rand < 0.80){
                powerUp = factory.createSuperBall(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
            } else if (rand < 0.85){
                powerUp = factory.createBulletPaddle(powerUpX, powerUpY, powerUpWidth, powerUpHeight);
            }
        }

        if (powerUp != null) {
            activePowerUps.add(powerUp);
        }
    }

    private boolean canPowerUpStack(String powerUpName) {
        if (powerUpName.equals("Expand Paddle") ||
                powerUpName.equals("Multi Ball") ||
                powerUpName.equals("Extra Life") ||
                powerUpName.equals("Bullet Paddle")) {
            return true;
        }

        for (ActivePowerUpEffect activeEffect : activePowerUpEffects) {
            String activeEffectName = activeEffect.getEffectType();
            if (powerUpName.equals("Speed x2") && activeEffectName.equals("speed x0.5")) {
                return false;
            }
            if (powerUpName.equals("speed x0.5") && activeEffectName.equals("Speed x2")) {
                return false;
            }
        }

        return true;
    }

    protected void renderGameplay(SpriteBatch batch) {
        if (backgroundTexture != null) {
            batch.draw(backgroundTexture, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }
        batch.setColor(0f, 0f, 0f, 0.3f);
        batch.draw(pixelTexture, 0, 0, SIDE_PANEL_WIDTH, WINDOW_HEIGHT);
        batch.draw(pixelTexture, SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH, 0, SIDE_PANEL_WIDTH, WINDOW_HEIGHT);
        batch.setColor(1f, 1f, 1f, 1f);
        paddle.render(batch);

        for (Ball ball : balls) {
            ball.render(batch);
        }

        for (Brick brick : bricks) {
            brick.render(batch);
        }

        for (PowerUp powerUp : activePowerUps) {
            powerUp.render(batch);
        }

        if (showHitboxes) {
            renderHitboxes(batch);
        }
    }

    protected void renderHitboxes(SpriteBatch batch) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (Ball ball : balls) {
            Rectangle ballBounds = ball.getBounds();
            float ballRadius = ball.getRadius();
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.circle(ballBounds.x, ballBounds.y, ballRadius, 32);
            shapeRenderer.setColor(0, 1, 0, 0.5f);
            shapeRenderer.rect(ballBounds.x - ballRadius, ballBounds.y - ballRadius, ballRadius * 2, ballRadius * 2);
            shapeRenderer.setColor(1, 1, 0, 1);
            shapeRenderer.circle(ballBounds.x, ballBounds.y, 2, 8);
        }

        for (Bullet bullet : paddle.getBullets()) {
            Rectangle bulletBounds = bullet.getBounds();
            shapeRenderer.rect(bulletBounds.x, bulletBounds.y, bulletBounds.width, bulletBounds.height);
        }

        Rectangle paddleBounds = paddle.getBounds();
        shapeRenderer.setColor(0, 0.5f, 1, 1);
        shapeRenderer.rect(paddleBounds.x, paddleBounds.y, paddleBounds.width, paddleBounds.height);
        shapeRenderer.setColor(0, 1, 1, 1);
        shapeRenderer.line(paddleBounds.x, paddleBounds.y + paddleBounds.height,
                          paddleBounds.x + paddleBounds.width, paddleBounds.y + paddleBounds.height);
        for (Brick brick : bricks) {
            if (brick.isDestroyed()) continue;
            Rectangle brickBounds = brick.getBounds();
            shapeRenderer.setColor(brick.getType() == Brick.BrickType.UNBREAKABLE ?
                                  Color.RED : new Color(1, 0.5f, 0, 1));
            shapeRenderer.rect(brickBounds.x, brickBounds.y, brickBounds.width, brickBounds.height);
            shapeRenderer.setColor(1, 1, 1, 0.5f);
            shapeRenderer.circle(brickBounds.x + brickBounds.width / 2f,
                               brickBounds.y + brickBounds.height / 2f, 2, 8);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    private void drawUIBox(SpriteBatch batch, float x, float y, float width, float height) {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.3f);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 0.4f);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    protected void renderUI(SpriteBatch batch) {
        batch.setColor(1f, 1f, 1f, 1f);
        fontUI30.setColor(Color.WHITE);
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();

        float boxWidth = SIDE_PANEL_WIDTH - 20;
        float boxX = 10;

        String scoreLabel = "Score";
        String scoreValue = String.format("%d", score);
        layout.setText(fontUI30, scoreLabel);
        float scoreLabelHeight = layout.height;
        layout.setText(fontUI30, scoreValue);
        float scoreValueHeight = layout.height;
        float scoreBoxHeight = scoreLabelHeight + scoreValueHeight + 30;
        float scoreBoxY = WINDOW_HEIGHT - 50 - scoreBoxHeight + 10;

        drawUIBox(batch, boxX, scoreBoxY, boxWidth, scoreBoxHeight);

        layout.setText(fontUI30, scoreLabel);
        float scoreLabelX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, scoreLabel, scoreLabelX, WINDOW_HEIGHT - 50);
        layout.setText(fontUI30, scoreValue);
        float scoreValueX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, scoreValue, scoreValueX, WINDOW_HEIGHT - 60 - layout.height);

        String bestLabel = "Best";
        String bestValue = String.format("%d", bestScore);
        layout.setText(fontUI30, bestLabel);
        float bestLabelHeight = layout.height;
        layout.setText(fontUI30, bestValue);
        float bestValueHeight = layout.height;
        float bestBoxHeight = bestLabelHeight + bestValueHeight + 30;
        float bestBoxY = WINDOW_HEIGHT - 140 - bestBoxHeight + 10;

        drawUIBox(batch, boxX, bestBoxY, boxWidth, bestBoxHeight);

        layout.setText(fontUI30, bestLabel);
        float bestLabelX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, bestLabel, bestLabelX, WINDOW_HEIGHT - 140);
        layout.setText(fontUI30, bestValue);
        float bestValueX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, bestValue, bestValueX, WINDOW_HEIGHT - 150 - layout.height);

        String comboLabel = "Combo";
        String comboValue = String.format("%dx", combo);
        layout.setText(fontUI30, comboLabel);
        float comboLabelHeight = layout.height;
        layout.setText(fontUI30, comboValue);
        float comboValueHeight = layout.height;
        float comboBoxHeight = comboLabelHeight + comboValueHeight + 30;
        float comboBoxY = WINDOW_HEIGHT - 230 - comboBoxHeight + 10;

        drawUIBox(batch, boxX, comboBoxY, boxWidth, comboBoxHeight);

        layout.setText(fontUI30, comboLabel);
        float comboLabelX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, comboLabel, comboLabelX, WINDOW_HEIGHT - 230);

        Color comboColor = Color.WHITE;
        if (combo >= 50) {
            comboColor = new Color(1f, 0.84f, 0f, 1f);
        } else if (combo >= 20) {
            comboColor = new Color(0f, 1f, 0.5f, 1f);
        }
        Color originalComboColor = fontUI30.getColor().cpy();
        fontUI30.setColor(comboColor);
        layout.setText(fontUI30, comboValue);
        float comboValueX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, comboValue, comboValueX, WINDOW_HEIGHT - 240 - layout.height);
        fontUI30.setColor(originalComboColor);

        String maxComboLabel = "Max Combo";
        String maxComboValue = String.format("%dx", maxCombo);
        layout.setText(fontUI30, maxComboLabel);
        float maxComboLabelHeight = layout.height;
        layout.setText(fontUI30, maxComboValue);
        float maxComboValueHeight = layout.height;
        float maxComboBoxHeight = maxComboLabelHeight + maxComboValueHeight + 30;
        float maxComboBoxY = WINDOW_HEIGHT - 320 - maxComboBoxHeight + 10;

        drawUIBox(batch, boxX, maxComboBoxY, boxWidth, maxComboBoxHeight);

        layout.setText(fontUI30, maxComboLabel);
        float maxComboLabelX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, maxComboLabel, maxComboLabelX, WINDOW_HEIGHT - 320);

        Color originalMaxComboColor = fontUI30.getColor().cpy();
        fontUI30.setColor(new Color(1f, 0.84f, 0f, 1f));
        layout.setText(fontUI30, maxComboValue);
        float maxComboValueX = (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, maxComboValue, maxComboValueX, WINDOW_HEIGHT - 330 - layout.height);
        fontUI30.setColor(originalMaxComboColor);

        String livesText = " x " + lives;
        layout.setText(fontUI30, livesText);
        float heartAndTextWidth = HEART_ICON_SIZE + 5f + layout.width;
        float heartStartX = (SIDE_PANEL_WIDTH - heartAndTextWidth) / 2f;
        float heartStartY = 40f;
        float blinkAlpha = heartBlinking && (heartBlinkTimer % HEART_BLINK_SPEED) / HEART_BLINK_SPEED < 0.5f ? 0.2f : 1.0f;
        Color originalColor = fontUI30.getColor().cpy();
        batch.setColor(1f, 1f, 1f, blinkAlpha);
        batch.draw(heartTexture, heartStartX, heartStartY, HEART_ICON_SIZE, HEART_ICON_SIZE);
        fontUI30.setColor(originalColor.r, originalColor.g, originalColor.b, blinkAlpha);
        float textY = heartStartY + (HEART_ICON_SIZE + layout.height) / 2f;
        fontUI30.draw(batch, livesText, heartStartX + HEART_ICON_SIZE + 5f, textY);
        batch.setColor(Color.WHITE);
        fontUI30.setColor(originalColor);

        String powerupsText = "Powerups";
        layout.setText(fontUI30, powerupsText);
        float powerupsX = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH + (SIDE_PANEL_WIDTH - layout.width) / 2f;
        fontUI30.draw(batch, powerupsText, powerupsX, WINDOW_HEIGHT - 50);

        renderActivePowerups(batch, layout);
    }

    private void renderActivePowerups(SpriteBatch batch, com.badlogic.gdx.graphics.g2d.GlyphLayout layout) {
        float startY = WINDOW_HEIGHT - 100;
        float iconSize = 32;
        float lineHeight = 45;
        float rightPanelX = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH + 15;
        float textOffsetX = iconSize + 8;

        int index = 0;
        for (ActivePowerUpEffect effect : activePowerUpEffects) {
            float currentY = startY - (index * lineHeight);

            float remainingTime = effect.getRemainingTime();
            if (remainingTime < 0) continue;

            boolean isLowTime = remainingTime <= 3.0f;

            float alpha = 1.0f;
            Color textColor = Color.WHITE;
            if (isLowTime) {
                alpha = (TimeUtils.millis() / 250) % 2 == 0 ? 0.3f : 1.0f;
                textColor = Color.RED;
            }

            Texture powerupTexture = effect.getPowerUp().getTexture();
            if (powerupTexture != null) {
                batch.setColor(1f, 1f, 1f, alpha);
                batch.draw(powerupTexture, rightPanelX, currentY - iconSize, iconSize, iconSize);
                batch.setColor(Color.WHITE);
            }

            String effectText = effect.getEffectType() + ": " + String.format("%.1fs", remainingTime);
            layout.setText(font, effectText);

            float textY = currentY - (iconSize / 2f) + (layout.height / 2f);

            Color originalColor = font.getColor().cpy();
            font.setColor(textColor.r, textColor.g, textColor.b, alpha);
            font.draw(batch, effectText, rightPanelX + textOffsetX, textY);
            font.setColor(originalColor);

            index++;
        }
    }

    protected boolean checkLevelComplete() {
        if (bricksDestroyed == 0) return false;
        return bricks.stream().noneMatch(brick -> brick.getType() == Brick.BrickType.BREAKABLE && !brick.isDestroyed());
    }

    protected void onBrickDestroyed(Brick brick) {
        bricksDestroyed++;
        log.debug("Brick destroyed! Total bricks destroyed: {}", bricksDestroyed);
    }

    protected void onBallLost() {
        lives--;
        Audio.playSfxBallLoss();
        heartBlinking = true;
        heartBlinkTimer = 0f;

        if (combo > 0) {
            log.info("Ball lost! Combo reset from {}x to 0", combo);
            combo = 0;
        }

        clearAllActivePowerups();

        balls.clear();
        float ballRadius = 12f;
        Ball mainBall = new Ball(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ballRadius + 5, ballRadius);
        balls.add(mainBall);

        stuckDetectionTimer = 0f;
        minBallY = Float.MAX_VALUE;
        maxBallY = Float.MIN_VALUE;

        log.info("Ball lost! Lives remaining: {}", lives);
        if (lives <= 0) {
            onGameOver();
        }
    }

    private void clearAllActivePowerups() {
        if (activePowerUpEffects.isEmpty()) {
            return;
        }

        log.info("Clearing {} active powerup effects due to life loss", activePowerUpEffects.size());

        for (ActivePowerUpEffect effect : activePowerUpEffects) {
            effect.removeEffect(this);
            log.debug("Removed {} effect", effect.getEffectType());
        }

        activePowerUpEffects.clear();
        activePowerUps.clear();
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
        if (gameOverMenu != null) {
            gameOverMenu.dispose();
        }
        if (gameFrameBuffer != null) {
            gameFrameBuffer.dispose();
        }
        if (gameSnapshot != null) {
            gameSnapshot.dispose();
        }
    }

    public Paddle getPaddle() {
        return paddle;
    }

    public Brick getLastHitBrick() {
        return lastHitBrick;
    }

    public Ball getBall() {
        return balls.isEmpty() ? null : balls.get(0);
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public void spawnBalls(int count) {
        if (balls.isEmpty()) return;

        Ball mainBall = balls.get(0);
        if (!mainBall.isActive()) return;

        int ballsToSpawn = Math.min(count, MAX_BALLS - balls.size());

        for (int i = 0; i < ballsToSpawn; i++) {
            Ball newBall = new Ball(mainBall.getBounds().x, mainBall.getBounds().y, mainBall.getRadius());

            newBall.setSuperBall(mainBall.isSuperBall());
            newBall.setSpeedMultiplier(mainBall.getSpeedMultiplier());
            newBall.launch();

            float baseSpeed = mainBall.getVelocity().len();
            float angleOffset = (i + 1) * 30f;
            float angleInRadians = (float) Math.toRadians(90 + angleOffset - (ballsToSpawn * 15f));
            newBall.setVelocity(
                baseSpeed * (float) Math.cos(angleInRadians),
                baseSpeed * (float) Math.sin(angleInRadians)
            );

            balls.add(newBall);
        }

        log.info("Spawned {} additional ball(s). Total balls: {}", ballsToSpawn, balls.size());
    }
}
