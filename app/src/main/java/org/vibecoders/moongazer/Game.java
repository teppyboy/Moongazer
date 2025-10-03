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
import org.vibecoders.moongazer.managers.Audio;
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
    public Scene settingsScene;
    public ArrayList<Scene> gameScenes;
    private boolean usingCustomScene = false;

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
        // By the end of the intro, other secenes will be created and assigned to Game.mainMenuScene
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

        // Only use state-based scene switching if not using custom scene
        if (!usingCustomScene) {
            switch (this.state) {
                case INTRO:
                    currentScene = introScene;
                    break;
                case MAIN_MENU:
                    currentScene = mainMenuScene;
                    break;
                case SETTINGS:
                    currentScene = settingsScene;
                    break;
                case IN_GAME:
                    // Render in-game scene
                    break;
                default:
                    log.warn("Unknown state: {}", state);
            }
        }

        for (var scene : gameScenes) {
            // log.trace("Checking scene visibility: {}", scene.getClass().getSimpleName());
            if (scene != currentScene && scene.root != null && scene.root.isVisible()) {
                log.trace("Hiding scene: {}", scene.getClass().getSimpleName());
                scene.root.setVisible(false);
            }
        }
        
        // Only render if currentScene is not null
        if (currentScene != null) {
            batch.begin();
            currentScene.render(batch);
            batch.end();
        }

        // Handle stage drawing for UI elements
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        // Save settings
        Settings.saveSettings();
        // Dispose scenes
        introScene.dispose();
        mainMenuScene.dispose();
        // Dispose resources
        Audio.dispose();
        Assets.dispose();
        // Dispose batch and stage
        batch.dispose();
        stage.dispose();
        log.debug("Resources disposed");
    }

    /**
     * Sets the current scene directly (for VN and other non-state-based scenes).
     *
     * @param scene the scene to switch to
     */
    public void setScene(Scene scene) {
        if (currentScene != null && currentScene.root != null) {
            currentScene.root.setVisible(false);
        }
        currentScene = scene;
        usingCustomScene = true;
        if (scene.root != null) {
            scene.root.setVisible(true);
            if (!gameScenes.contains(scene)) {
                gameScenes.add(scene);
                if (scene.root.getStage() == null) {
                    stage.addActor(scene.root);
                }
            }
        }
        log.debug("Scene switched to: {}", scene.getClass().getSimpleName());
    }

    /**
     * Sets the game state and returns to state-based scene switching.
     *
     * @param newState the new game state
     */
    public void setState(State newState) {
        this.state = newState;
        this.usingCustomScene = false;
        // Reset input processor to main stage
        Gdx.input.setInputProcessor(stage);
        log.debug("State changed to: {}", newState);
    }

    /**
     * Sets the current scene and state (used by transitions).
     *
     * @param scene the scene to set as current
     * @param newState the new state
     */
    public void setCurrentSceneAndState(Scene scene, State newState) {
        this.currentScene = scene;
        this.state = newState;
        this.usingCustomScene = false;
        if (scene.root != null) {
            scene.root.setVisible(true);
        }
        Gdx.input.setInputProcessor(stage);
        log.debug("Current scene set to: {}, state: {}", scene.getClass().getSimpleName(), newState);
    }

    /**
     * Returns to main menu from a custom scene.
     */
    public void returnToMainMenu() {
        // Create transition from DialogueScene to MainMenu instead of direct switch
        if (currentScene != null && usingCustomScene && currentScene instanceof DialogueScene) {
            log.debug("Creating transition from DialogueScene to MainMenu");
            DialogueScene dialogueScene = (DialogueScene) currentScene;
            dialogueScene.enterTransition(); // Mark as entering transition to stop rendering
            transition = new DialogueToMenuTransition(this, dialogueScene, mainMenuScene, 500);
            return;
        }

        // Fallback for other custom scenes
        if (currentScene != null && usingCustomScene) {
            log.debug("Disposing custom scene: {}", currentScene.getClass().getSimpleName());
            currentScene.dispose();
            currentScene = null;
        }

        setState(State.MAIN_MENU);
        currentScene = mainMenuScene;
        if (mainMenuScene.root != null) {
            mainMenuScene.root.setVisible(true);
        }
        Gdx.input.setInputProcessor(stage);
        log.debug("Returned to main menu, state={}, usingCustomScene={}", state, usingCustomScene);
    }
}