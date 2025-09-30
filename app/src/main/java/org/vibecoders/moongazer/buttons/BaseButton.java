package org.vibecoders.moongazer.buttons;

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

    public void  setSize(int width, int height) {
        actor.setSize(width, height);
    }

    public void eventListener(EventListener eventListener) {
        actor.addListener(eventListener);
    }
}
