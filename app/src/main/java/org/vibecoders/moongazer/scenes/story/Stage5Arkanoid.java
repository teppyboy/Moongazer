package org.vibecoders.moongazer.scenes.story;

import com.badlogic.gdx.graphics.Texture;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.managers.Assets;

import static org.vibecoders.moongazer.Constants.*;

public class Stage5Arkanoid extends StoryArkanoid {
    public Stage5Arkanoid(Game game, int startingLives) {
        super(game, 0, startingLives);
        setStageId(5);
        setBackground(Assets.getAsset("textures/stage/Bg5.png", Texture.class));
    }

    @Override
    protected void createBrickGrid(int rows, int cols) {
        createSunflowerPattern();
    }

    private void createSunflowerPattern() {
        bricks.clear();
        float centerX = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH / 2f;
        float centerY = WINDOW_HEIGHT / 2f + 50f;
        float scaledBrickWidth = BRICK_WIDTH * 0.75f;
        float scaledBrickHeight = BRICK_HEIGHT * 0.75f;
        float scaledPadding = BRICK_PADDING * 0.75f;

        // 10x10 Sunflower pattern (hope and new beginning)
        int[][] sunflowerPattern = {
            {0,0,1,1,1,1,1,1,0,0},
            {0,1,1,1,1,1,1,1,1,0},
            {1,1,1,0,0,0,0,1,1,1},
            {1,1,0,0,1,1,0,0,1,1},
            {1,1,0,1,1,1,1,0,1,1},
            {1,1,0,1,1,1,1,0,1,1},
            {1,1,0,0,1,1,0,0,1,1},
            {1,1,1,0,0,0,0,1,1,1},
            {0,1,1,1,1,1,1,1,1,0},
            {0,0,1,1,1,1,1,1,0,0}
        };

        int patternRows = sunflowerPattern.length;
        int patternCols = sunflowerPattern[0].length;
        float totalWidth = patternCols * (scaledBrickWidth + scaledPadding);
        float totalHeight = patternRows * (scaledBrickHeight + scaledPadding);
        float startX = centerX - totalWidth / 2f;
        float startY = centerY + totalHeight / 2f;

        for (int row = 0; row < patternRows; row++) {
            for (int col = 0; col < patternCols; col++) {
                if (sunflowerPattern[row][col] == 1) {
                    float x = startX + col * (scaledBrickWidth + scaledPadding);
                    float y = startY - row * (scaledBrickHeight + scaledPadding);
                    Brick.BrickType type = getSunflowerBrickType(row, col, patternRows, patternCols);

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

    private Brick.BrickType getSunflowerBrickType(int row, int col, int totalRows, int totalCols) {
        float centerRow = totalRows / 2f;
        float centerCol = totalCols / 2f;
        double distanceFromCenter = Math.sqrt(
            Math.pow(row - centerRow, 2) + Math.pow(col - centerCol, 2)
        );

        // Make center (core of sunflower) unbreakable
        if (distanceFromCenter < 2.5) {
            return Brick.BrickType.UNBREAKABLE;
        }
        // Make petals outline unbreakable
        if (row <= 2 || row >= totalRows - 3) {
            if ((row + col) % 4 == 0) {
                return Brick.BrickType.UNBREAKABLE;
            }
        }
        if ((row + col) % 8 == 0) {
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
        createSunflowerPattern();
    }
}

