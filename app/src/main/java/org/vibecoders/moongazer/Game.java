package org.vibecoders.moongazer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.*;

public class Game extends ApplicationAdapter {
    private static final Logger log = LoggerFactory.getLogger(Game.class);
    public static State state = State.INTRO;
    public static Transition transition = null;
    SpriteBatch batch;
    Texture logo;
    Scene currentScene;
    Scene introScene;
    public static Scene mainMenuScene;

    @Override
    public void create() {
        log.info("Loading intro assets...");
        Assets.loadIntroAndWait();
        log.info("Intro assets loaded successfully.");
        batch = new SpriteBatch();
        currentScene = introScene = new Intro();
        mainMenuScene = new MainMenu();
    }

    @Override
    public void render() {
        // Handle transition if any
        if (transition != null) {
            batch.begin();
            transition.render(batch);
            batch.end();
            return;
        }
        switch (Game.state) {
            case INTRO:
                currentScene = introScene;
                break;
            case MAIN_MENU:
                currentScene = mainMenuScene;
                break;
            case IN_GAME:
                // Render in-game scene
                break;
            default:
                log.warn("Unknown state: {}", state);
        }
        batch.begin();
        currentScene.render(batch);
        batch.end();
    }

    @Override
    public void dispose() {
        introScene.dispose();
        mainMenuScene.dispose();
        Assets.dispose();
        log.debug("Resources disposed");
    }
}
