package org.vibecoders.moongazer.scenes.story;

import com.badlogic.gdx.graphics.Texture;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.managers.Assets;

import static org.vibecoders.moongazer.Constants.*;

public class Stage2Arkanoid extends StoryArkanoid {
    /**
     * Constructor for Stage 2 Arkanoid game scene.
     *
     * @param game          Reference to the main Game object.
     * @param startingLives Number of lives the player starts with.
     */
    public Stage2Arkanoid(Game game, int startingLives) {
        super(game, 0, startingLives);
        setStageId(2);
        setBackground(Assets.getAsset("textures/stage/Bg2.png", Texture.class));
    }

    /**
     * Creates a heart-shaped pattern of bricks for the level.
     *
     * The heart pattern consists of a combination of breakable and unbreakable bricks,
     * arranged to resemble a heart shape. Breakable bricks are assigned random levels
     * and power-ups.
     *
     * Overrides the default brick grid creation method to implement the custom pattern.
     *
     * @param rows Number of rows (not used in this implementation).
     * @param cols Number of columns (not used in this implementation).
     */
    @Override
    protected void createBrickGrid(int rows, int cols) {
        createHeartPattern();
    }

    /**
     * Generates a heart-shaped pattern of bricks.
     *
     * This method clears any existing bricks and populates the brick list with a
     * predefined heart pattern. The pattern includes both breakable and unbreakable bricks,
     * with breakable bricks assigned random levels and power-ups.
     */
    private void createHeartPattern() {
        bricks.clear();
        float centerX = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH / 2f;
        float centerY = WINDOW_HEIGHT / 2f + 50f;
        float scaledBrickWidth = BRICK_WIDTH * 0.75f;
        float scaledBrickHeight = BRICK_HEIGHT * 0.75f;
        float scaledPadding = BRICK_PADDING * 0.75f;

        // 10x10 Heart pattern
        int[][] heartPattern = {
            {0,0,1,1,0,0,1,1,0,0},
            {0,1,1,1,1,1,1,1,1,0},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {0,1,1,1,1,1,1,1,1,0},
            {0,0,1,1,1,1,1,1,0,0},
            {0,0,0,1,1,1,1,0,0,0},
            {0,0,0,0,1,1,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0}
        };

        int patternRows = heartPattern.length;
        int patternCols = heartPattern[0].length;
        float totalWidth = patternCols * (scaledBrickWidth + scaledPadding);
        float totalHeight = patternRows * (scaledBrickHeight + scaledPadding);
        float startX = centerX - totalWidth / 2f;
        float startY = centerY + totalHeight / 2f;

        for (int row = 0; row < patternRows; row++) {
            for (int col = 0; col < patternCols; col++) {
                if (heartPattern[row][col] == 1) {
                    float x = startX + col * (scaledBrickWidth + scaledPadding);
                    float y = startY - row * (scaledBrickHeight + scaledPadding);
                    Brick.BrickType type = getHeartBrickType(row, col, patternRows, patternCols);

                    if (type == Brick.BrickType.BREAKABLE) {
                        // Random level (1, 2, or 3) and powerUp for breakable bricks
                        int level = 1 + (int)(Math.random() * 3);
                        Brick.PowerUpType powerUp = getRandomPowerUp();
                        bricks.add(new Brick(x, y, scaledBrickWidth, scaledBrickHeight, type, level, powerUp));
                    } else {
                        bricks.add(new Brick(x, y, scaledBrickWidth, scaledBrickHeight, type));
                    }
                }
            }
        }
    }

    /**
     * Selects a random power-up type based on predefined probabilities.
     *
     * @return A randomly selected PowerUpType.
     */
    private Brick.PowerUpType getRandomPowerUp() {
        double rand = Math.random();
        if (rand < 0.70) {
            return Brick.PowerUpType.NONE;
        } else if (rand < 0.75) {
            return Brick.PowerUpType.EXPAND_PADDLE;
        } else if (rand < 0.80) {
            return Brick.PowerUpType.EXTRA_LIFE;
        } else if (rand < 0.85) {
            return Brick.PowerUpType.SLOW_BALL;
        } else if (rand < 0.90) {
            return Brick.PowerUpType.MULTI_BALL;
        } else if (rand < 0.95) {
            return Brick.PowerUpType.SUPER_BALL;
        } else {
            return Brick.PowerUpType.FAST_BALL;
        }
    }

    /**
     * Determines the brick type for a given position in the heart pattern.
     *
     * Unbreakable bricks are placed in an outline pattern, while the rest are breakable.
     *
     * @param row        The row index of the brick.
     * @param col        The column index of the brick.
     * @param totalRows  Total number of rows in the pattern.
     * @param totalCols  Total number of columns in the pattern.
     * @return The BrickType for the specified position.
     */
    private Brick.BrickType getHeartBrickType(int row, int col, int totalRows, int totalCols) {
        // Create unbreakable bricks in outline pattern
        if (row == 0 || row == totalRows - 1) {
            return Brick.BrickType.UNBREAKABLE;
        }
        if ((row + col) % 5 == 0) {
            return Brick.BrickType.UNBREAKABLE;
        }
        return Brick.BrickType.BREAKABLE;
    }

    @Override
    protected void onPausePressed() {
        pauseMenu.pause();
    }

    @Override
    protected void restartGame() {
        score = 0;
        lives = startingLives;
        bricksDestroyed = 0;
        initGameplay();
        createHeartPattern();
    }
}

