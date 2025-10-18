package org.vibecoders.moongazer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.enums.State;
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
    public Scene loadScene;
    public Scene testInGameScene;
    public Scene currentDialogue;
    public Scene selectionScene;
    public Scene storyModeScene;
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
        // By the end of the intro, other secenes will be created and assigned to these
        // scenes
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
            case LOAD_GAME:
                currentScene = loadScene;
                break;
            case TEST_IN_GAME:
                currentScene = testInGameScene;
                break;
            case DIALOGUE:
                currentScene = currentDialogue;
                break;
            case SELECTION:
                currentScene = selectionScene;
                break;
            case STORY_MODE:
                currentScene = storyModeScene;
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

        batch.begin();
        currentScene.render(batch);
        batch.end();

        // Handle stage drawing for UI elements
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public <T extends Scene> T recreateScene(
            T oldScene,
            Supplier<T> constructor,
            Consumer<T> assign) {
        if (oldScene != null) {
            if (gameScenes.contains(oldScene)) {
                gameScenes.remove(oldScene);
            }
            oldScene.dispose();
        }
        T newScene = constructor.get();
        assign.accept(newScene);
        gameScenes.add(newScene);
        return newScene;
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
}