package org.vibecoders.moongazer.scenes.arkanoid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.arkanoid.*;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;
import org.vibecoders.moongazer.scenes.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.vibecoders.moongazer.Constants.*;

public class GameplayTestScene extends Arkanoid {
    private static final Logger log = LoggerFactory.getLogger(GameplayTestScene.class);
    private static final int BRICK_COLS = 30;
    private boolean initialized = false;
    private Label instructionLabel;
    private Label statsLabel;
    private boolean wasVisible = true;
    private ShapeRenderer shapeRenderer;
    private boolean showHitboxes = true;
    private SpriteBatch cachedBatch;
    private int currentWave = 1;
    private float unbreakableChance = 0.1f;

    public GameplayTestScene(Game game) {
        super(game);
        shapeRenderer = new ShapeRenderer();
        initUI();
        game.stage.addActor(root);
    }

    @Override
    protected void initGameplay() {
        super.initGameplay();
        float paddleWidth = 120f;
        float paddleHeight = 20f;
        float paddleX = (WINDOW_WIDTH - paddleWidth) / 2f;
        float paddleY = 50f;
        paddle = new Paddle(paddleX, paddleY, paddleWidth, paddleHeight);
        float ballRadius = 12f;
        ball = new Ball(WINDOW_WIDTH / 2f, paddleY + paddleHeight + ballRadius + 5, ballRadius);
        startWave(currentWave);
    }

    @Override
    protected Brick.BrickType getBrickType(int row, int col) {
        return (Math.random() < unbreakableChance)
            ? Brick.BrickType.UNBREAKABLE
            : Brick.BrickType.BREAKABLE;
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
        if (!initialized) {
            init();
            initialized = true;
        }
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
        handleInput(delta);
        updateGameplay(delta);
        handleCollisions();
        renderGameplay(batch);
        batch.end();
        if (showHitboxes) {
            renderHitboxes();
        }
        batch.begin();
        updateStats();
    }

    @Override
    protected void handleInput(float delta) {
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

    @Override
    protected void onBrickDestroyed(Brick brick) {
        score += 10;
        bricksDestroyed++;
        if (checkLevelComplete()) {
            onWaveComplete();
        }
    }

    @Override
    protected void onBallLost() {
        ball.reset(paddle.getCenterX(), paddle.getBounds().y + paddle.getBounds().height + ball.getRadius() + 5);
        log.info("Ball lost! Resetting...");
    }

    @Override
    protected boolean checkLevelComplete() {
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

    @Override
    protected void onLevelComplete() {
        onWaveComplete();
    }

    @Override
    protected void onGameOver() {
        log.info("Game Over! Final Score: {}", score);
        reinitGameplay();
    }

    @Override
    protected void onPausePressed() {
        Audio.playSfxReturn();
        if (game.transition == null) {
            game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 350);
        }
    }
}
