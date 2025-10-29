package org.vibecoders.moongazer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.arkanoid.TestArkanoidScreen;

import static org.vibecoders.moongazer.Constants.*;

/**
 * Standalone launcher for testing Arkanoid gameplay.
 */
public class StandaloneGameplayTest extends Game {
    private static final Logger log = LoggerFactory.getLogger(StandaloneGameplayTest.class);

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("Moongazer - Gameplay Test");
        cfg.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        cfg.useVsync(true);
        cfg.setIdleFPS(60);
        cfg.setWindowIcon("icons/logo.png");
        cfg.setResizable(false);

        log.info("Starting Standalone Gameplay Test...");
        new Lwjgl3Application(new StandaloneGameplayTest(), cfg);
    }

    @Override
    public void create() {
        super.create(); // Initialize batch and stage

        log.info("Loading assets...");
        Assets.loadAll();
        Assets.waitUntilLoaded();
        log.info("Assets loaded.");

        // Create and set test screen as current scene
        gameplayTestScene = new TestArkanoidScreen();
        currentScene = gameplayTestScene;
        state = State.GAMEPLAY_TEST;

        log.info("Test ready! Press SPACE to launch, ESC to exit.");
    }
}