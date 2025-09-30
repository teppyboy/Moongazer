package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;

public abstract class UIButton {
    protected Actor actor;
    protected Button button;

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

    public void onClick(Runnable action) {
        button.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                action.run();
            }
        });
    }
}
