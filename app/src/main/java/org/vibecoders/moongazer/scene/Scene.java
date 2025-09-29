package org.vibecoders.moongazer.scene;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Scene {
    public abstract void render(SpriteBatch batch);
    public abstract void dispose();
}
