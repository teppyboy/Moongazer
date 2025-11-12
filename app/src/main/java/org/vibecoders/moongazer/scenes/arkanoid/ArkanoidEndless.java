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

        // Stop menu music and start endless music
        org.vibecoders.moongazer.managers.Audio.menuMusicStop();
        org.vibecoders.moongazer.managers.Audio.startEndlessMusic();

        startWave(currentWave);
    }

    @Override
    protected void setupGameOverMenuCallbacks() {
        gameOverMenu.setOnPlayAgain(() -> {
            log.info("Playing again - Starting from Wave 1");

            // Stop game over music and restart endless music
            org.vibecoders.moongazer.managers.Audio.stopGameOverMusic();
            org.vibecoders.moongazer.managers.Audio.startEndlessMusic();

            gameInputEnabled = true;
            restartGame(); // This will reset heartBlinking and all game state
            restoreInputProcessor();
        });

        gameOverMenu.setOnMainMenu(() -> {
            log.info("Returning to main menu from game over");

            // Stop game over music and restart menu music
            org.vibecoders.moongazer.managers.Audio.stopGameOverMusic();
            org.vibecoders.moongazer.managers.Audio.menuMusicPlay();

            returnToMainMenu();
        });

        gameOverMenu.setOnQuit(() -> {
            log.info("Quitting game from game over");
            com.badlogic.gdx.Gdx.app.exit();
        });
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

    @Override
    protected void createBrickGrid(int rows, int cols) {
        bricks.clear();
        float brickTotalWidth = BRICK_WIDTH + BRICK_PADDING;
        int maxCols = (int) (GAMEPLAY_AREA_WIDTH / brickTotalWidth);
        cols = Math.min(cols, maxCols);
        int totalBricks = rows * cols;

        // Reduce power-up counts - more rare
        int[] powerUpCounts = {
                Math.min(1 + (currentWave / 15), 2),  // Super Ball: 1-2
                Math.min(1 + (currentWave / 12), 2),  // Multi Ball: 1-2
                Math.min(1 + (currentWave / 10), 3),  // Extra Life: 1-3
                Math.min(2 + (currentWave / 8), 3),   // Expand Paddle: 2-3
                Math.min(1 + (currentWave / 10), 2),  // Fast Ball: 1-2
                Math.min(1 + (currentWave / 10), 2),  // Slow Ball: 1-2
                Math.min(1 + (currentWave / 12), 3)   // Bullet Paddle: 1-3
        };
        
        int powerUpTotal = 0;
        for (int count : powerUpCounts) powerUpTotal += count;

        int unbreakableCount = (int) (totalBricks * unbreakableChance);

        // Calculate breakable brick distribution by level
        int breakableTotal = totalBricks - unbreakableCount - powerUpTotal;

        // Ensure at least one breakable brick
        if (breakableTotal <= 0) {
            System.out.println("Warning: Not enough space for breakable bricks. Reducing power-ups and unbreakable bricks.");
            // First, try reducing power-ups
            int excess = unbreakableCount + powerUpTotal - (totalBricks - 1);
            int powerUpExcess = Math.min(powerUpTotal, excess);
            powerUpTotal -= powerUpExcess;
            // Reduce individual powerUpCounts proportionally
            int toRemove = powerUpExcess;
            for (int i = powerUpCounts.length - 1; i >= 0 && toRemove > 0; i--) {
                int remove = Math.min(powerUpCounts[i], toRemove);
                powerUpCounts[i] -= remove;
                toRemove -= remove;
            }
            // If still not enough, reduce unbreakable bricks
            excess = unbreakableCount + powerUpTotal - (totalBricks - 1);
            if (excess > 0) {
                unbreakableCount -= Math.min(unbreakableCount, excess);
            }
            breakableTotal = totalBricks - unbreakableCount - powerUpTotal;
            if (breakableTotal <= 0) {
                // As a last resort, set breakableTotal to 1 and adjust others
                breakableTotal = 1;
                powerUpTotal = 0;
                unbreakableCount = totalBricks - 1;
                for (int i = 0; i < powerUpCounts.length; i++) powerUpCounts[i] = 0;
            }
        }

        // Level distribution: 50% level 1, 30% level 2, 20% level 3
        int level1Count = (int) (breakableTotal * 0.50f);
        int level2Count = (int) (breakableTotal * 0.30f);
        int level3Count = breakableTotal - level1Count - level2Count;

        // Adjust for wave difficulty - more high level bricks as waves progress
        float difficultyFactor = Math.min(currentWave / 20f, 0.5f); // Max 50% shift
        int shiftFromLevel1 = (int) (level1Count * difficultyFactor);
        level1Count = Math.max(0, level1Count - shiftFromLevel1);
        int shiftToLevel2 = shiftFromLevel1 / 2 + shiftFromLevel1 % 2;
        int shiftToLevel3 = shiftFromLevel1 / 2;
        level2Count += shiftToLevel2;
        level3Count += shiftToLevel3;

        // Create brick data list
        List<BrickData> brickDataList = new ArrayList<>();

        // Add power-up bricks (all level 1)
        Brick.PowerUpType[] types = {
            Brick.PowerUpType.SUPER_BALL,
            Brick.PowerUpType.MULTI_BALL,
            Brick.PowerUpType.EXTRA_LIFE,
            Brick.PowerUpType.EXPAND_PADDLE,
            Brick.PowerUpType.FAST_BALL,
            Brick.PowerUpType.SLOW_BALL,
            Brick.PowerUpType.BULLET,
        };
        
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < powerUpCounts[i]; j++) {
                brickDataList.add(new BrickData(Brick.BrickType.BREAKABLE, 1, types[i]));
            }
        }

        // Add unbreakable bricks
        for (int i = 0; i < unbreakableCount; i++) {
            brickDataList.add(new BrickData(Brick.BrickType.UNBREAKABLE, -1, Brick.PowerUpType.NONE));
        }

        // Add level 1 breakable bricks
        for (int i = 0; i < level1Count; i++) {
            brickDataList.add(new BrickData(Brick.BrickType.BREAKABLE, 1, Brick.PowerUpType.NONE));
        }

        // Add level 2 breakable bricks
        for (int i = 0; i < level2Count; i++) {
            brickDataList.add(new BrickData(Brick.BrickType.BREAKABLE, 2, Brick.PowerUpType.NONE));
        }

        // Add level 3 breakable bricks
        for (int i = 0; i < level3Count; i++) {
            brickDataList.add(new BrickData(Brick.BrickType.BREAKABLE, 3, Brick.PowerUpType.NONE));
        }

        Collections.shuffle(brickDataList);

        float gridWidth = cols * brickTotalWidth;
        float startX = SIDE_PANEL_WIDTH + (GAMEPLAY_AREA_WIDTH - gridWidth) / 2f;
        float startY = WINDOW_HEIGHT - 100f;

        int brickIndex = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = startX + col * brickTotalWidth;
                float y = startY - row * (BRICK_HEIGHT + BRICK_PADDING);
                BrickData data = brickDataList.get(brickIndex++);

                Brick brick;
                if (data.type == Brick.BrickType.UNBREAKABLE) {
                    brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, Brick.BrickType.UNBREAKABLE);
                } else if (data.powerUpType == Brick.PowerUpType.NONE) {
                    brick = Brick.createBreakableBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT, data.level);
                } else {
                    brick = Brick.createBreakableBrick(x, y, BRICK_WIDTH, BRICK_HEIGHT, data.level, data.powerUpType);
                }
                bricks.add(brick);
            }
        }

        log.info("Brick grid created: {} rows x {} cols = {} bricks", rows, cols, totalBricks);
        log.info("Distribution - PowerUps: {}, Unbreakable: {}, Level1: {}, Level2: {}, Level3: {}",
                 powerUpTotal, unbreakableCount, level1Count, level2Count, level3Count);
        log.info("PowerUp breakdown - SuperBall: {}, MultiBall: {}, ExtraLife: {}, ExpandPaddle: {}, FastBall: {}, SlowBall: {}, BulletPaddle: {}",
                 powerUpCounts[0], powerUpCounts[1], powerUpCounts[2], powerUpCounts[3], powerUpCounts[4], powerUpCounts[5], powerUpCounts[6]);

        // Ensure no trapped bricks
        ensureNoTrappedBricks(rows, cols);
    }

    /**
     * Helper class to store brick data during generation
     */
    private static class BrickData {
        final Brick.BrickType type;
        final int level;
        final Brick.PowerUpType powerUpType;

        BrickData(Brick.BrickType type, int level, Brick.PowerUpType powerUpType) {
            this.type = type;
            this.level = level;
            this.powerUpType = powerUpType;
        }
    }

    /**
     * Ensures that no breakable brick is completely surrounded by unbreakable bricks.
     * If found, converts one neighboring unbreakable brick to breakable.
     */
    private void ensureNoTrappedBricks(int rows, int cols) {
        boolean fixed = false;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                Brick brick = bricks.get(index);

                // Only check breakable bricks
                if (brick.getType() != Brick.BrickType.BREAKABLE) continue;

                // Check all 4 directions (top, bottom, left, right)
                boolean topBlocked = (row == 0) || (bricks.get((row - 1) * cols + col).getType() == Brick.BrickType.UNBREAKABLE);
                boolean bottomBlocked = (row == rows - 1) || (bricks.get((row + 1) * cols + col).getType() == Brick.BrickType.UNBREAKABLE);
                boolean leftBlocked = (col == 0) || (bricks.get(row * cols + (col - 1)).getType() == Brick.BrickType.UNBREAKABLE);
                boolean rightBlocked = (col == cols - 1) || (bricks.get(row * cols + (col + 1)).getType() == Brick.BrickType.UNBREAKABLE);

                // If completely surrounded, open one path
                if (topBlocked && bottomBlocked && leftBlocked && rightBlocked) {
                    if (row > 0) {
                        int topIndex = (row - 1) * cols + col;
                        convertToBreakable(bricks.get(topIndex), topIndex);
                        fixed = true;
                        log.warn("Fixed trapped brick at ({}, {}) by opening TOP", row, col);
                    } else if (col > 0) {
                        int leftIndex = row * cols + (col - 1);
                        convertToBreakable(bricks.get(leftIndex), leftIndex);
                        fixed = true;
                        log.warn("Fixed trapped brick at ({}, {}) by opening LEFT", row, col);
                    } else if (col < cols - 1) {
                        int rightIndex = row * cols + (col + 1);
                        convertToBreakable(bricks.get(rightIndex), rightIndex);
                        fixed = true;
                        log.warn("Fixed trapped brick at ({}, {}) by opening RIGHT", row, col);
                    } else if (row < rows - 1) {
                        int bottomIndex = (row + 1) * cols + col;
                        convertToBreakable(bricks.get(bottomIndex), bottomIndex);
                        fixed = true;
                        log.warn("Fixed trapped brick at ({}, {}) by opening BOTTOM", row, col);
                    }
                }
            }
        }
        if (!fixed) {
            log.info("No trapped bricks found - generation is valid!");
        }
    }

    /**
     * Converts an unbreakable brick to a normal breakable brick
     */
    private void convertToBreakable(Brick brick, int brickIndex) {
        if (brick.getType() == Brick.BrickType.UNBREAKABLE) {
            float x = brick.getBounds().x;
            float y = brick.getBounds().y;
            float width = brick.getBounds().width;
            float height = brick.getBounds().height;
            Brick newBrick = new Brick(x, y, width, height, Brick.BrickType.BREAKABLE);
            bricks.set(brickIndex, newBrick);
        }
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
        heartBlinking = false;
        heartBlinkTimer = 0f;
        org.vibecoders.moongazer.SaveGameManager.saveEndlessScore(score, currentWave);
        org.vibecoders.moongazer.SaveGameManager.updateHighScore(score, currentWave);
        org.vibecoders.moongazer.managers.Audio.startGameOverMusic();
        gameOverMenu.show(score);
    }

    @Override
    protected void onPausePressed() {
        log.info("Pause requested");
        pauseMenu.pause();
    }

    @Override
    protected void restartGame() {
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        currentWave = 1;
        unbreakableChance = 0.1f;
        heartBlinking = false;
        heartBlinkTimer = 0f;
        initGameplay();
        startWave(currentWave);
    }

    @Override
    protected void returnToMainMenu() {
        log.info("Returning to main menu from endless mode");

        // Stop endless music and restart menu music
        org.vibecoders.moongazer.managers.Audio.stopEndlessMusic();
        org.vibecoders.moongazer.managers.Audio.menuMusicPlay();

        pauseMenu.resume();
        restoreInputProcessor();
        if (game.transition == null) {
            game.transition = new org.vibecoders.moongazer.scenes.Transition(
                game, this, game.mainMenuScene,
                org.vibecoders.moongazer.enums.State.MAIN_MENU, 500);
        }
    }
}
