package org.vibecoders.moongazer.scenes.arkanoid;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.managers.Assets;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.vibecoders.moongazer.Constants.*;

public class ArkanoidEndless extends Arkanoid {

    private int currentWave = 1;
    private float unbreakableChance = 0.1f;

    public ArkanoidEndless(Game game) {
        super(game);
    }

    @Override
    protected void init() {
        super.init();
        setBackground(Assets.getAsset("textures/arkanoid/bg/endless.jpg", Texture.class));
        startWave(currentWave);
    }

    private void startWave(int wave) {
        bricksDestroyed = 0;
        int rows = Math.min(5 + (wave / 2), 10);
        unbreakableChance = Math.min(0.1f + (wave * 0.02f), 0.4f);
        createBrickGrid(rows, 30);
        log.info("=== WAVE {} STARTED === (Rows: {}, Unbreakable: {}%)",
                wave, rows, (int)(unbreakableChance * 100));
    }

    @Override
    protected Brick.BrickType getBrickType(int row, int col) {
        return (Math.random() < unbreakableChance)
                ? Brick.BrickType.UNBREAKABLE
                : Brick.BrickType.BREAKABLE;
    }

    /**
     * Creates a brick grid with evenly distributed power-ups for Endless mode.
     * Power-up distribution scales with wave difficulty (reduced percentages):
     * - Super Ball: 2-3 bricks (most powerful, rarest)
     * - Multi Ball: 2-4 bricks
     * - Extra Life: 3-5 bricks
     * - Expand Paddle: 3-6 bricks
     * - Fast Ball: 2-4 bricks
     * - Slow Ball: 2-4 bricks
     * - Unbreakable: based on unbreakableChance (scales with wave)
     * - Normal: remaining bricks (majority)
     */
    @Override
    protected void createBrickGrid(int rows, int cols) {
        bricks.clear();

        float availableWidth = GAMEPLAY_AREA_WIDTH;
        float brickTotalWidth = BRICK_WIDTH + BRICK_PADDING;
        int maxCols = (int) (availableWidth / brickTotalWidth);
        cols = Math.min(cols, maxCols);

        int totalBricks = rows * cols;

        // Calculate power-up distribution based on wave (reduced amounts)
        int superBallCount = Math.min(2 + (currentWave / 10), 3);      // 2-3 bricks max
        int multiBallCount = Math.min(2 + (currentWave / 8), 4);       // 2-4 bricks
        int extraLifeCount = Math.min(3 + (currentWave / 6), 5);       // 3-5 bricks
        int expandPaddleCount = Math.min(3 + (currentWave / 5), 6);    // 3-6 bricks
        int fastBallCount = Math.min(2 + (currentWave / 7), 4);        // 2-4 bricks
        int slowBallCount = Math.min(2 + (currentWave / 7), 4);        // 2-4 bricks

        // Calculate unbreakable brick count (scales with difficulty)
        int unbreakableCount = (int) (totalBricks * unbreakableChance);

        // Total power-up bricks
        int powerUpBricksCount = superBallCount + multiBallCount + extraLifeCount +
                                 expandPaddleCount + fastBallCount + slowBallCount;

        // Ensure we don't exceed total bricks - at least 50% should be normal bricks
        int minNormalCount = totalBricks / 2;
        int normalCount = totalBricks - unbreakableCount - powerUpBricksCount;

        if (normalCount < minNormalCount) {
            // Reduce power-ups to maintain at least 50% normal bricks
            normalCount = minNormalCount;
            powerUpBricksCount = totalBricks - unbreakableCount - normalCount;

            // Scale down power-ups proportionally
            int totalOriginalPowerUps = superBallCount + multiBallCount + extraLifeCount +
                                       expandPaddleCount + fastBallCount + slowBallCount;
            float scale = (float) powerUpBricksCount / totalOriginalPowerUps;

            superBallCount = Math.max(1, (int)(superBallCount * scale));
            multiBallCount = Math.max(1, (int)(multiBallCount * scale));
            extraLifeCount = Math.max(1, (int)(extraLifeCount * scale));
            expandPaddleCount = Math.max(1, (int)(expandPaddleCount * scale));
            fastBallCount = Math.max(1, (int)(fastBallCount * scale));
            slowBallCount = Math.max(1, (int)(slowBallCount * scale));
        }

        // Create a list of brick types to distribute
        List<Brick.PowerUpType> brickTypes = new ArrayList<>();

        // Add power-up bricks
        for (int i = 0; i < superBallCount; i++) brickTypes.add(Brick.PowerUpType.SUPER_BALL);
        for (int i = 0; i < multiBallCount; i++) brickTypes.add(Brick.PowerUpType.MULTI_BALL);
        for (int i = 0; i < extraLifeCount; i++) brickTypes.add(Brick.PowerUpType.EXTRA_LIFE);
        for (int i = 0; i < expandPaddleCount; i++) brickTypes.add(Brick.PowerUpType.EXPAND_PADDLE);
        for (int i = 0; i < fastBallCount; i++) brickTypes.add(Brick.PowerUpType.FAST_BALL);
        for (int i = 0; i < slowBallCount; i++) brickTypes.add(Brick.PowerUpType.SLOW_BALL);

        // Add unbreakable markers
        for (int i = 0; i < unbreakableCount; i++) brickTypes.add(null); // null = unbreakable

        // Fill remaining with NONE (normal bricks)
        while (brickTypes.size() < totalBricks) {
            brickTypes.add(Brick.PowerUpType.NONE);
        }

        // Shuffle to distribute evenly
        Collections.shuffle(brickTypes);

        // Create bricks
        float gridWidth = cols * brickTotalWidth;
        float startX = SIDE_PANEL_WIDTH + (GAMEPLAY_AREA_WIDTH - gridWidth) / 2f;
        float startY = WINDOW_HEIGHT - 100f;

        int brickIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = startX + col * brickTotalWidth;
                float y = startY - row * (BRICK_HEIGHT + BRICK_PADDING);

                Brick.PowerUpType powerUpType = brickTypes.get(brickIndex++);

                Brick brick;
                if (powerUpType == null) {
                    // Unbreakable brick
                    brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, Brick.BrickType.UNBREAKABLE);
                } else if (powerUpType == Brick.PowerUpType.NONE) {
                    // Normal breakable brick
                    brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, Brick.BrickType.BREAKABLE);
                } else {
                    // Power-up brick
                    brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, Brick.BrickType.BREAKABLE, powerUpType);
                }

                bricks.add(brick);
            }
        }

        log.info("Brick grid created: {} rows x {} cols = {} bricks", rows, cols, totalBricks);
        log.info("Distribution - SuperBall: {}, MultiBall: {}, ExtraLife: {}, ExpandPaddle: {}, FastBall: {}, SlowBall: {}, Unbreakable: {}, Normal: {} ({}%)",
                 superBallCount, multiBallCount, extraLifeCount, expandPaddleCount,
                 fastBallCount, slowBallCount, unbreakableCount, normalCount,
                 (int)((float)normalCount / totalBricks * 100));
    }

    @Override
    protected void onLevelComplete() {
        int previousWave = currentWave;
        currentWave++;
        int bonus = 100 * previousWave;
        score += bonus;
        log.info("Wave {} complete! Bonus: {}", previousWave, bonus);
        startWave(currentWave);
    }

    @Override
    protected void onGameOver() {
        log.info("Game Over! Final Score: {} (Wave: {})", score, currentWave);
        // Reset game
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        currentWave = 1;
        unbreakableChance = 0.1f;
        initGameplay();
        startWave(currentWave);
    }

    @Override
    protected void onPausePressed() {
        log.info("Pause requested");
        pauseMenu.pause();
    }

    @Override
    protected void restartGame() {
        // Reset game state
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        currentWave = 1;
        unbreakableChance = 0.1f;
        initGameplay();
        startWave(currentWave);
    }

    @Override
    protected void returnToMainMenu() {
        log.info("Returning to main menu from endless mode");
        // Unpause and restore input processor
        pauseMenu.resume();
        restoreInputProcessor();
        // Navigate back to main menu
        if (game.transition == null) {
            game.transition = new org.vibecoders.moongazer.scenes.Transition(
                game, this, game.mainMenuScene,
                org.vibecoders.moongazer.enums.State.MAIN_MENU, 500);
        }
    }
}
