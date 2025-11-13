package org.vibecoders.moongazer.scenes;

import org.slf4j.Logger;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class Scene {
    protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
    public Table root;
    public Game game;
    public Scene(Game game) {
        this();
        this.game = game;
    }

    /**
     * Default constructor for Scene. Initializes the root Table and ensures all assets are loaded.
     */
    public Scene() {
        root = new Table();
        if (!Assets.isLoadedAll() && Assets.isStartLoadAll()) {
            Assets.waitUntilLoaded();
        }
    }
    public abstract void render(SpriteBatch batch);
    public void dispose() {
        // Default implementation does nothing
    }
}
