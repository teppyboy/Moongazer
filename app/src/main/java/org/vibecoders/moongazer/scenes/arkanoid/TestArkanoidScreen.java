package org.vibecoders.moongazer.scenes.arkanoid;

import com.badlogic.gdx.Gdx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.Game;

/**
 * Test screen for Arkanoid gameplay with wave system.
 */
public class TestArkanoidScreen extends Arkanoid {
    private static final Logger log = LoggerFactory.getLogger(TestArkanoidScreen.class);
    private int currentWave = 1;
    private float unbreakableChance = 0.1f; // Initial chance for unbreakable bricks

    /**
     * Constructs a new test Arkanoid screen.
     *
     * @param game the main game instance
     */
    public TestArkanoidScreen(Game game) {
        super(game);
    }

    /**
     * Initializes the test screen and starts the first wave.
     */
    @Override
    protected void init() {
        super.init();
        startWave(currentWave);
    }

    /**
     * Starts a new wave with increasing difficulty.
     *
     * @param wave the wave number
     */
    private void startWave(int wave) {
        int rows = Math.min(5 + (wave / 2), 10);
        unbreakableChance = Math.min(0.1f + (wave * 0.02f), 0.4f);
        createBrickGrid(rows, 30);
        log.info("=== WAVE {} === (Rows: {}, Unbreakable: {}%)", 
                 wave, rows, (int)(unbreakableChance * 100));
    }

    /**
     * Called when a level is completed.
     * Advances to the next wave with bonus points.
     */
    @Override
    protected void onLevelComplete() {
        int waveBonus = 100 * currentWave;
        score += waveBonus;
        log.info("Wave {} complete! Bonus: {}", currentWave, waveBonus);
        currentWave++;
        bricksDestroyed = 0;
        initGameplay();
        startWave(currentWave);
    }

    /**
     * Called when the game is over.
     * Resets the game to wave 1.
     */
    @Override
    protected void onGameOver() {
        log.info("Game Over! Final Score: {} (Wave {})", score, currentWave);
        score = 0;
        lives = 3;
        currentWave = 1;
        bricksDestroyed = 0;
        initGameplay();
        startWave(currentWave);
    }

    /**
     * Called when the pause button is pressed.
     */
    @Override
    protected void onPausePressed() {
        pauseMenu.pause();
    }

    /**
     * Restarts the game from wave 1.
     */
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

    /**
     * Returns to the main menu by exiting the application.
     */
    @Override
    protected void returnToMainMenu() {
        log.info("Returning to main menu from test screen");
        Gdx.app.exit();
    }
}