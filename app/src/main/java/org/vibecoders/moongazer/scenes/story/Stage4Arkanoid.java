package org.vibecoders.moongazer.scenes.story;

import com.badlogic.gdx.graphics.Texture;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.managers.Assets;

import static org.vibecoders.moongazer.Constants.*;

public class Stage4Arkanoid extends StoryArkanoid {
    /**
     * Stage 4 Arkanoid level with a Fate Anchor brick pattern.
     *
     * @param game          Reference to the main Game object.
     * @param startingLives Number of lives the player starts with.
     */
    public Stage4Arkanoid(Game game, int startingLives) {
        super(game, 0, startingLives);
        setStageId(4);
        setBackground(Assets.getAsset("textures/stage/Bg4.png", Texture.class));
    }

    /**
     * Creates a Fate Anchor brick pattern in the gameplay area.
     *
     * @param rows Number of rows (not used in this pattern).
     * @param cols Number of columns (not used in this pattern).
     */
    @Override
    protected void createBrickGrid(int rows, int cols) {
        createFateAnchorPattern();
    }

    /**
     * Creates the Fate Anchor brick pattern.
     */
    private void createFateAnchorPattern() {
        bricks.clear();
        float centerX = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH / 2f;
        float centerY = WINDOW_HEIGHT / 2f + 50f;
        float scaledBrickWidth = BRICK_WIDTH * 0.75f;
        float scaledBrickHeight = BRICK_HEIGHT * 0.75f;
        float scaledPadding = BRICK_PADDING * 0.75f;

        // 10x10 Anchor/Cross pattern (Fated Anchor)
        int[][] anchorPattern = {
            {0,0,0,0,1,1,0,0,0,0},
            {0,0,0,1,1,1,1,0,0,0},
            {0,0,1,1,1,1,1,1,0,0},
            {0,1,1,1,1,1,1,1,1,0},
            {1,1,1,1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1,1,1,1},
            {0,1,1,1,1,1,1,1,1,0},
            {0,0,0,0,1,1,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0}
        };

        int patternRows = anchorPattern.length;
        int patternCols = anchorPattern[0].length;
        float totalWidth = patternCols * (scaledBrickWidth + scaledPadding);
        float totalHeight = patternRows * (scaledBrickHeight + scaledPadding);
        float startX = centerX - totalWidth / 2f;
        float startY = centerY + totalHeight / 2f;

        for (int row = 0; row < patternRows; row++) {
            for (int col = 0; col < patternCols; col++) {
                if (anchorPattern[row][col] == 1) {
                    float x = startX + col * (scaledBrickWidth + scaledPadding);
                    float y = startY - row * (scaledBrickHeight + scaledPadding);
                    Brick.BrickType type = getAnchorBrickType(row, col, patternRows, patternCols);

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
     * Returns a random power-up type based on defined probabilities.
     *
     * @return A randomly selected Brick.PowerUpType.
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
     * Determines the brick type for the Fate Anchor pattern.
     *
     * @param row        The row index of the brick.
     * @param col        The column index of the brick.
     * @param totalRows  Total number of rows in the pattern.
     * @param totalCols  Total number of columns in the pattern.
     * @return The Brick.BrickType for the specified position.
     */
    private Brick.BrickType getAnchorBrickType(int row, int col, int totalRows, int totalCols) {
        // Make the vertical line (stem) unbreakable
        int centerCol = totalCols / 2;
        if (col == centerCol && row >= 5) {
            return Brick.BrickType.UNBREAKABLE;
        }
        // Make the horizontal line (crossbar) unbreakable
        if (row == 5) {
            return Brick.BrickType.UNBREAKABLE;
        }
        // Scattered unbreakable bricks
        if ((row + col) % 6 == 0) {
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
        createFateAnchorPattern();
    }
}

