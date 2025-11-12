package org.vibecoders.moongazer.scenes.story;

import com.badlogic.gdx.graphics.Texture;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.managers.Assets;

import static org.vibecoders.moongazer.Constants.*;

public class Stage3Arkanoid extends StoryArkanoid {
    public Stage3Arkanoid(Game game, int startingLives) {
        super(game, 0, startingLives);
        setStageId(3);
        setBackground(Assets.getAsset("textures/stage/Bg3.png", Texture.class));
    }

    @Override
    protected void createBrickGrid(int rows, int cols) {
        createMemoryFragmentsPattern();
    }

    private void createMemoryFragmentsPattern() {
        bricks.clear();
        float centerX = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH / 2f;
        float centerY = WINDOW_HEIGHT / 2f + 50f;
        float scaledBrickWidth = BRICK_WIDTH * 0.75f;
        float scaledBrickHeight = BRICK_HEIGHT * 0.75f;
        float scaledPadding = BRICK_PADDING * 0.75f;

        // 10x10 Seven fragments pattern (7 memory fragments)
        int[][] fragmentsPattern = {
            {1,1,0,0,1,1,0,0,1,1},
            {1,1,0,0,1,1,0,0,1,1},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,1,1,0,0,1,1,0,0},
            {0,0,1,1,0,0,1,1,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {1,1,0,0,1,1,0,0,1,1},
            {1,1,0,0,1,1,0,0,1,1},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,1,0,0,0,0}
        };

        int patternRows = fragmentsPattern.length;
        int patternCols = fragmentsPattern[0].length;
        float totalWidth = patternCols * (scaledBrickWidth + scaledPadding);
        float totalHeight = patternRows * (scaledBrickHeight + scaledPadding);
        float startX = centerX - totalWidth / 2f;
        float startY = centerY + totalHeight / 2f;

        for (int row = 0; row < patternRows; row++) {
            for (int col = 0; col < patternCols; col++) {
                if (fragmentsPattern[row][col] == 1) {
                    float x = startX + col * (scaledBrickWidth + scaledPadding);
                    float y = startY - row * (scaledBrickHeight + scaledPadding);
                    Brick.BrickType type = getFragmentBrickType(row, col);

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

    private Brick.BrickType getFragmentBrickType(int row, int col) {
        // Make center brick of each fragment unbreakable
        if ((row % 3 == 1) && (col % 4 == 1)) {
            return Brick.BrickType.UNBREAKABLE;
        }
        if ((row + col) % 7 == 0) {
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
        createMemoryFragmentsPattern();
    }
}

