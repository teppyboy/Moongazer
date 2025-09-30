package org.vibecoders.moongazer.scenes;

import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Scene {
    public Scene() {
        if (!Assets.isLoadedAll()) {
            Assets.waitUntilLoaded();
        }
    }
    public abstract void render(SpriteBatch batch);
    public void dispose() {
        // Default implementation does nothing
    }
}
