package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;

public abstract class BaseButton {
    protected Actor actor;

    public Actor getActor() {
        return actor;
    }

    public void setPosition(int x, int y) {
        actor.setPosition(x, y);
    }

    public void setSize(int width, int height) {
        actor.setSize(width, height);
    }

    public void addEventListener(EventListener eventListener) {
        actor.addListener(eventListener);
    }
}
