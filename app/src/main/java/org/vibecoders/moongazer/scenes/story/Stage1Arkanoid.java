package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.Brick;

import static org.vibecoders.moongazer.Constants.*;

public class Stage1Arkanoid extends StoryArkanoid {
    public Stage1Arkanoid(Game game, int startingLives) {
        super(game, 0, startingLives);
        setStageId(1);
    }
    @Override
    protected void createBrickGrid(int rows, int cols) {
        createMoonPattern();
    }
    private void createMoonPattern() {
        bricks.clear();
        float centerX = SIDE_PANEL_WIDTH + GAMEPLAY_AREA_WIDTH / 2f;
        float centerY = WINDOW_HEIGHT / 2f + 50f;
        float scaledBrickWidth = BRICK_WIDTH * 0.75f;
        float scaledBrickHeight = BRICK_HEIGHT * 0.75f;
        float scaledPadding = BRICK_PADDING * 0.75f;
        int[][] moonPattern = {
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0}
        };
        int patternRows = moonPattern.length;
        int patternCols = moonPattern[0].length;
        float totalWidth = patternCols * (scaledBrickWidth + scaledPadding);
        float totalHeight = patternRows * (scaledBrickHeight + scaledPadding);
        float startX = centerX - totalWidth / 2f;
        float startY = centerY + totalHeight / 2f;
        for (int row = 0; row < patternRows; row++) {
            for (int col = 0; col < patternCols; col++) {
                if (moonPattern[row][col] == 1) {
                    float x = startX + col * (scaledBrickWidth + scaledPadding);
                    float y = startY - row * (scaledBrickHeight + scaledPadding);
                    Brick.BrickType type = getMoonBrickType(row, col, patternRows, patternCols);
                    bricks.add(new Brick(x, y, scaledBrickWidth, scaledBrickHeight, type));
                }
            }
        }
    }
    private Brick.BrickType getMoonBrickType(int row, int col, int totalRows, int totalCols) {
        float centerRow = totalRows / 2f;
        float centerCol = totalCols / 2f;
        double distanceFromCenter = Math.sqrt(
            Math.pow(row - centerRow, 2) + Math.pow(col - centerCol, 2)
        );
        if (distanceFromCenter < 1.0) {
            return Brick.BrickType.UNBREAKABLE;
        }
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
        createMoonPattern();
        initGameplay();
    }
}

