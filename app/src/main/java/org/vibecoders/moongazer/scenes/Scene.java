package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Scene {
    public abstract void render(SpriteBatch batch);
    public void dispose() {
        // Default implementation does nothing
    }
}
