package org.vibecoders.moongazer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.scene.*;

public class Game extends ApplicationAdapter {
    private static final Logger log = LoggerFactory.getLogger(Game.class);
    public static State state = State.INTRO;
    SpriteBatch batch;
    Texture logo;
    Scene currentScene;
    Scene introScene;

    @Override
    public void create() {
        log.info("Loading assets...");
        Assets.loadAll();
        log.info("Assets loaded successfully.");
        batch = new SpriteBatch();
        currentScene = introScene = new Intro();
    }

    @Override
    public void render() {
        switch (Game.state) {
            case INTRO:
                currentScene = introScene;
                break;
            case MAIN_MENU:
                // Render main menu scene
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
        Assets.dispose();
        log.debug("Resources disposed");
    }
}
