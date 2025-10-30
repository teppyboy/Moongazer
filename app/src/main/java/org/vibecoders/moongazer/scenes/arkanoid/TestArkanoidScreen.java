package org.vibecoders.moongazer.scenes.arkanoid;

import com.badlogic.gdx.Gdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test screen for Arkanoid gameplay.
 * Use this for standalone testing before integrating with main game.
 */
public class TestArkanoidScreen extends Arkanoid {
    private static final Logger log = LoggerFactory.getLogger(TestArkanoidScreen.class);

    public TestArkanoidScreen() {
        super();
    }

    @Override
    protected void init() {
        super.init();
        // Create test level
        createBrickGrid(8, 30);
    }

    @Override
    protected void onLevelComplete() {
        log.info("Level Complete! Score: {}", score);
        // Restart for testing
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        initGameplay();
        createBrickGrid(8, 30);
    }

    @Override
    protected void onGameOver() {
        log.info("Game Over! Final Score: {}", score);
        // Restart for testing
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        initGameplay();
        createBrickGrid(8, 30);
    }

    @Override
    protected void onPausePressed() {
        log.info("ESC pressed - Exiting test...");
        Gdx.app.exit();
    }
}