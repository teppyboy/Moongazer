package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Intro scene displaying the game logo and handling asset loading.
 */
public class Intro extends Scene {
    private Texture logo;
    private Game game;
    private long startTime;
    private long endTime = 0;

    /**
     * Initializes the intro scene, starts loading assets.
     */
    public Intro(Game game) {
        this.game = game;
        logo = Assets.getAsset("icons/logo.png", Texture.class);
        startTime = System.currentTimeMillis() + 500;
        log.info("Starting to load all remaining assets...");
        Assets.loadAll();
        game.mainMenuScene = new MainMenu(game);
        game.gameScenes.add(game.mainMenuScene);
    }

    /**
     * Renders the intro scene.
     * @param batch The SpriteBatch to draw with.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (System.currentTimeMillis() > endTime + 2000 && endTime != 0) {
            if (game.transition == null) {
                Assets.waitUntilLoaded();
                log.info("All assets loaded successfully.");
                game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 1000);
            }
            batch.draw(Assets.getBlackTexture(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            return;
        }
        ScreenUtils.clear(Color.BLACK);
        // log.debug("Rendering logo at position: ({}, {})", WINDOW_WIDTH / 2 - logo.getWidth() / 4, WINDOW_HEIGHT / 2 - logo.getHeight() / 4);
        var currentOpacity = (float) (System.currentTimeMillis() - startTime) / 1000;
        if (currentOpacity > 1) {
            if (endTime == 0) {
                endTime = System.currentTimeMillis() + 2000;
            }
            currentOpacity = 1 - ((float) (System.currentTimeMillis() - endTime) / 1000);
        }
        // Multiply with any externally applied alpha (e.g., Transition)
        float externalAlpha = batch.getColor().a; // alpha set by Transition before calling render
        float finalAlpha = currentOpacity * externalAlpha;
        batch.setColor(1, 1, 1, finalAlpha);
        batch.draw(logo, WINDOW_WIDTH / 2 - logo.getWidth() / 4, WINDOW_HEIGHT / 2 - logo.getHeight() / 4,
                logo.getWidth() / 2, logo.getHeight() / 2);
        // Reset color for safety (Transition will set again anyway)
        batch.setColor(1,1,1,externalAlpha);
    }
}
