package org.vibecoders.moongazer.scenes.arkanoid;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.*;
import org.vibecoders.moongazer.managers.Assets;
import com.badlogic.gdx.graphics.Texture;
import static org.vibecoders.moongazer.Constants.*;

public class ArkanoidEndless extends Arkanoid {

    private int currentWave = 1;
    private float unbreakableChance = 0.1f;
    private Texture backgroundTexture;

    public ArkanoidEndless(Game game) {
        super(game);
        backgroundTexture = Assets.getAsset("textures/arkanoid/bg/endless.jpg", Texture.class);
    }

    @Override
    protected void init() {
        super.init();
        startWave(currentWave);
    }

    @Override
    protected void renderGameplay(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        batch.draw(backgroundTexture, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        super.renderGameplay(batch);
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
        // Reset game
        score = 0;
        lives = 3;
        bricksDestroyed = 0;
        currentWave = 1;
        unbreakableChance = 0.1f;
        initGameplay();
        startWave(currentWave);
    }

    @Override
    protected void onPausePressed() {
        isPaused = !isPaused;
        log.info("Game {}", isPaused ? "paused" : "resumed");
    }
}
