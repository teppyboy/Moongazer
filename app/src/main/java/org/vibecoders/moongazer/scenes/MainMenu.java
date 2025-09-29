package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Main menu scene.
 */
public class MainMenu extends Scene {
    /**
     * Renders the main menu scene.
     * @param batch The SpriteBatch to draw with.
     */
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(TEXTURE_WHITE, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }
}
