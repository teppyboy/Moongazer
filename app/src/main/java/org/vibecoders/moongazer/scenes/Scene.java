package org.vibecoders.moongazer.scenes;

import org.slf4j.Logger;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Scene {
    protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());
    public Scene(Game game) {
        this();
    }
    public Scene() {
        if (!Assets.isLoadedAll() && Assets.isStartLoadAll()) {
            Assets.waitUntilLoaded();
        }
    }
    public abstract void render(SpriteBatch batch);
    public void dispose() {
        // Default implementation does nothing
    }
}
