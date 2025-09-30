package org.vibecoders.moongazer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.*;

public class Game extends ApplicationAdapter {
    private static final Logger log = LoggerFactory.getLogger(Game.class);
    public State state = State.INTRO;
    public Transition transition = null;
    SpriteBatch batch;
    // UI stage
    public Stage stage;
    public Table root;
    // Scenes
    Scene currentScene;
    Scene introScene;
    public Scene mainMenuScene;
    public ArrayList<Scene> gameScenes;

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
        gameScenes = new ArrayList<>();
        currentScene = introScene = new Intro(this);
        gameScenes.add(introScene);
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
            stage.draw();
            return;
        }
        switch (this.state) {
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

        for (var scene : gameScenes) {
            // log.trace("Checking scene visibility: {}", scene.getClass().getSimpleName());
            if (scene != currentScene && scene.root.isVisible()) {
                log.trace("Hiding scene: {}", scene.getClass().getSimpleName());
                scene.root.setVisible(false);
            }
        }

        if (!currentScene.root.isVisible()) {
            log.trace("Showing current scene: {}", currentScene.getClass().getSimpleName());
            currentScene.root.setVisible(true);
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
