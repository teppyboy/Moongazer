package org.vibecoders.moongazer.scene;

import static org.vibecoders.moongazer.Constants.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.Assets;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Intro scene displaying the game logo and handling asset loading.
 */
public class Intro extends Scene {
    private Texture logo;
    private long startTime;
    private long endTime = 0;
    private static final Logger log = LoggerFactory.getLogger(Intro.class);

    /**
     * Initializes the intro scene, starts loading assets.
     */
    public Intro() {
        logo = Assets.getAsset("icons/logo.png", Texture.class);
        startTime = System.currentTimeMillis() + 500;
        log.info("Starting to load all remaining assets...");
        Assets.loadAll();
    }

    /**
     * Renders the intro scene.
     * @param batch The SpriteBatch to draw with.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (System.currentTimeMillis() > endTime + 2000 && endTime != 0) {
            Assets.waitUntilLoaded();
            if (Game.transition == null) {
                Game.transition = new Transition(this, Game.mainMenuScene, State.MAIN_MENU, 1000);
            }
            batch.draw(TEXTURE_BLACK, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
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
        batch.setColor(1, 1, 1, currentOpacity);
        batch.draw(logo, WINDOW_WIDTH / 2 - logo.getWidth() / 4, WINDOW_HEIGHT / 2 - logo.getHeight() / 4,
                logo.getWidth() / 2, logo.getHeight() / 2);
    }
}
