package org.vibecoders.moongazer;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.vibecoders.moongazer.Constants.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle(WINDOW_TITLE);
        cfg.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
        cfg.useVsync(true);
        cfg.setIdleFPS(10);
        cfg.setWindowIcon("icons/logo.png");
        log.info("Starting game client...");
        new Lwjgl3Application(new Game(), cfg);
    }
}
