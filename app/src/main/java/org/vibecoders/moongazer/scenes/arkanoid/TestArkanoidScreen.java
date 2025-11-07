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

    public TestArkanoidScreen(Game game) {
        super(game);
    }

    @Override
    protected void init() {
        super.init();
        startWave(currentWave);
    }

    private void startWave(int wave) {
        int rows = Math.min(5 + (wave / 2), 10);
        unbreakableChance = Math.min(0.1f + (wave * 0.02f), 0.4f);
        createBrickGrid(rows, 30);
        log.info("=== WAVE {} === (Rows: {}, Unbreakable: {}%)", 
                 wave, rows, (int)(unbreakableChance * 100));
    }

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

    @Override
    protected void onPausePressed() {
        pauseMenu.pause();
    }

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

    @Override
    protected void returnToMainMenu() {
        log.info("Returning to main menu from test screen");
        Gdx.app.exit();
    }
}