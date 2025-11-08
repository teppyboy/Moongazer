package org.vibecoders.moongazer.scenes.arkanoid;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.arkanoid.Brick;
import org.vibecoders.moongazer.managers.Assets;
import com.badlogic.gdx.graphics.Texture;

public class ArkanoidEndless extends Arkanoid {

    private int currentWave = 1;
    private float unbreakableChance = 0.1f;

    public ArkanoidEndless(Game game) {
        super(game);
    }

    @Override
    protected void init() {
        super.init();
        setBackground(Assets.getAsset("textures/arkanoid/bg/endless.jpg", Texture.class));
        startWave(currentWave);
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
        log.info("Pause requested");
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
        log.info("Returning to main menu from endless mode");
        // Unpause and restore input processor
        pauseMenu.resume();
        restoreInputProcessor();
        // Navigate back to main menu
        if (game.transition == null) {
            game.transition = new org.vibecoders.moongazer.scenes.Transition(
                game, this, game.mainMenuScene,
                org.vibecoders.moongazer.enums.State.MAIN_MENU, 500);
        }
    }
}
