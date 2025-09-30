package org.vibecoders.moongazer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.*;

public class Game extends ApplicationAdapter {
    private static final Logger log = LoggerFactory.getLogger(Game.class);
    public static State state = State.INTRO;
    public static Transition transition = null;
    SpriteBatch batch;
    // UI stage
    public Stage stage;
    public Table root;
    // Scenes
    Scene currentScene;
    Scene introScene;
    public static Scene mainMenuScene;

    @Override
    public void create() {
        log.info("Loading intro assets...");
        Assets.loadIntroAndWait();
        log.info("Intro assets loaded successfully.");
        batch = new SpriteBatch();
        // Stage for UI elements
        stage = new Stage(new ScreenViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
        // Scene initialization
        currentScene = introScene = new Intro(this);
        // By the end of the intro, the main menu scene will be created and assigned to Game.mainMenuScene
    }

    @Override
    public void render() {
        // Handle transition if any
        if (transition != null) {
            batch.begin();
            transition.render(batch);
            batch.end();
            // Handle stage drawing for UI elements
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
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
        // Handle stage drawing for UI elements
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        introScene.dispose();
        mainMenuScene.dispose();
        Assets.dispose();
        batch.dispose();
        stage.dispose();
        log.debug("Resources disposed");
    }
}
