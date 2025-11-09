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
        float availableWidth = GAMEPLAY_AREA_WIDTH;
        float brickTotalWidth = BRICK_WIDTH + BRICK_PADDING;
        int maxCols = (int) (availableWidth / brickTotalWidth);
        cols = Math.min(cols, maxCols);
        int totalBricks = rows * cols;

        int[] powerUpCounts = {
            Math.min(2 + (currentWave / 10), 3),
            Math.min(2 + (currentWave / 8), 4),
            Math.min(3 + (currentWave / 6), 5),
            Math.min(3 + (currentWave / 5), 6),
            Math.min(2 + (currentWave / 7), 4),
            Math.min(2 + (currentWave / 7), 4)
        };
        
        int powerUpTotal = 0;
        for (int count : powerUpCounts) powerUpTotal += count;
        int unbreakableCount = (int) (totalBricks * unbreakableChance);
        int minNormalCount = totalBricks / 2;
        int normalCount = totalBricks - unbreakableCount - powerUpTotal;

        if (normalCount < minNormalCount) {
            normalCount = minNormalCount;
            float scale = (float)(totalBricks - unbreakableCount - normalCount) / powerUpTotal;
            for (int i = 0; i < powerUpCounts.length; i++) {
                powerUpCounts[i] = Math.max(1, (int)(powerUpCounts[i] * scale));
            }
        }

        List<Brick.PowerUpType> brickTypes = new ArrayList<>();
        Brick.PowerUpType[] types = {
            Brick.PowerUpType.SUPER_BALL,
            Brick.PowerUpType.MULTI_BALL,
            Brick.PowerUpType.EXTRA_LIFE,
            Brick.PowerUpType.EXPAND_PADDLE,
            Brick.PowerUpType.FAST_BALL,
            Brick.PowerUpType.SLOW_BALL
        };
        
        for (int i = 0; i < types.length; i++) {
            for (int j = 0; j < powerUpCounts[i]; j++) {
                brickTypes.add(types[i]);
            }
        }
        for (int i = 0; i < unbreakableCount; i++) brickTypes.add(null);
        while (brickTypes.size() < totalBricks) brickTypes.add(Brick.PowerUpType.NONE);
        Collections.shuffle(brickTypes);

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
                    brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, Brick.BrickType.UNBREAKABLE);
                } else if (powerUpType == Brick.PowerUpType.NONE) {
                    brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, Brick.BrickType.BREAKABLE);
                } else {
                    brick = new Brick(x, y, BRICK_WIDTH, BRICK_HEIGHT, Brick.BrickType.BREAKABLE, powerUpType);
                }
                bricks.add(brick);
            }
        }

        log.info("Brick grid created: {} rows x {} cols = {} bricks", rows, cols, totalBricks);
        log.info("Distribution - SuperBall: {}, MultiBall: {}, ExtraLife: {}, ExpandPaddle: {}, FastBall: {}, SlowBall: {}, Unbreakable: {}, Normal: {} ({}%)",
                 powerUpCounts[0], powerUpCounts[1], powerUpCounts[2], powerUpCounts[3],
                 powerUpCounts[4], powerUpCounts[5], unbreakableCount, normalCount,
                 (int)((float)normalCount / totalBricks * 100));

        // Ensure no breakable brick is completely surrounded by unbreakable bricks
        ensureNoTrappedBricks(rows, cols);
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
                boolean topBlocked = (row == 0) || (row > 0 && bricks.get((row - 1) * cols + col).getType() == Brick.BrickType.UNBREAKABLE);
                boolean bottomBlocked = (row == rows - 1) || (row < rows - 1 && bricks.get((row + 1) * cols + col).getType() == Brick.BrickType.UNBREAKABLE);
                boolean leftBlocked = (col == 0) || (col > 0 && bricks.get(row * cols + (col - 1)).getType() == Brick.BrickType.UNBREAKABLE);
                boolean rightBlocked = (col == cols - 1) || (col < cols - 1 && bricks.get(row * cols + (col + 1)).getType() == Brick.BrickType.UNBREAKABLE);

                // If completely surrounded, open one path
                if (topBlocked && bottomBlocked && leftBlocked && rightBlocked) {
                    // Try to open top first, then others
                    if (row > 0) {
                        Brick topBrick = bricks.get((row - 1) * cols + col);
                        convertToBreakable(topBrick);
                        fixed = true;
                        log.warn("Fixed trapped brick at ({}, {}) by opening TOP", row, col);
                    } else if (col > 0) {
                        Brick leftBrick = bricks.get(row * cols + (col - 1));
                        convertToBreakable(leftBrick);
                        fixed = true;
                        log.warn("Fixed trapped brick at ({}, {}) by opening LEFT", row, col);
                    } else if (col < cols - 1) {
                        Brick rightBrick = bricks.get(row * cols + (col + 1));
                        convertToBreakable(rightBrick);
                        fixed = true;
                        log.warn("Fixed trapped brick at ({}, {}) by opening RIGHT", row, col);
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
    private void convertToBreakable(Brick brick) {
        if (brick.getType() == Brick.BrickType.UNBREAKABLE) {
            // Replace the brick with a new breakable one at the same position
            int brickIndex = bricks.indexOf(brick);
            if (brickIndex >= 0) {
                float x = brick.getBounds().x;
                float y = brick.getBounds().y;
                float width = brick.getBounds().width;
                float height = brick.getBounds().height;
                Brick newBrick = new Brick(x, y, width, height, Brick.BrickType.BREAKABLE);
                bricks.set(brickIndex, newBrick);
            }
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

        // Stop heart blinking immediately
        heartBlinking = false;
        heartBlinkTimer = 0f;

        // Stop endless music and start game over music
        org.vibecoders.moongazer.managers.Audio.startGameOverMusic();

        // Show game over menu with final score
        gameOverMenu.show(score);

        // Reset game state for next play
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        currentWave = 1;
        unbreakableChance = 0.1f;
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
