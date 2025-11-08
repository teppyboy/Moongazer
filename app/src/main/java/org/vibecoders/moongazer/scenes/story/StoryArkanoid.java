package org.vibecoders.moongazer.scenes.story;

import com.badlogic.gdx.Gdx;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class StoryArkanoid extends Arkanoid {
    private Runnable onLevelCompleteCallback;
    private Runnable onGameOverCallback;
    private int requiredBricks;
    protected int startingLives;

    public StoryArkanoid(Game game, int rows, int startingLives) {
        super(game);
        this.requiredBricks = rows;
        this.startingLives = startingLives;
        // Set lives after parent init has completed
        this.lives = startingLives;
    }

    @Override
    protected void init() {
        super.init();
        createBrickGrid(requiredBricks, 30);
        log.info("Story Arkanoid initialized with {} rows and {} lives", requiredBricks, startingLives);
    }
    public void setOnLevelComplete(Runnable callback) {
        this.onLevelCompleteCallback = callback;
    }
    public void setOnGameOver(Runnable callback) {
        this.onGameOverCallback = callback;
    }
    @Override
    protected void onLevelComplete() {
        log.info("Story level complete!");
        if (onLevelCompleteCallback != null) {
            onLevelCompleteCallback.run();
        }
    }
    @Override
    protected void onGameOver() {
        log.info("Story game over!");
        if (onGameOverCallback != null) {
            onGameOverCallback.run();
        }
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
    }
    @Override
    protected void returnToMainMenu() {
        log.info("Returning to main menu from story mode");
        Gdx.app.exit();
    }
    @Override
    protected Brick.BrickType getBrickType(int row, int col) {
        return (row % 3 == 0) ? Brick.BrickType.UNBREAKABLE : Brick.BrickType.BREAKABLE;
    }
}
