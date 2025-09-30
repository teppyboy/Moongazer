package org.vibecoders.moongazer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.vibecoders.moongazer.Constants.*;

/**
 * Main entry point for the Moongazer game application.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Launches the game application with specified configuration.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setOpenGLEmulation(GLEmulation.GL32, 3, 2);
        cfg.setTitle(WINDOW_TITLE);
        cfg.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        cfg.useVsync(true);
        cfg.setIdleFPS(10);
        cfg.setWindowIcon("icons/logo.png");
        cfg.setResizable(false);
        log.info("Starting game client...");
        new Lwjgl3Application(new Game(), cfg);
    }
}
