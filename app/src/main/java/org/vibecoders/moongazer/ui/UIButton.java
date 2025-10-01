package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class UIButton {
    public Actor actor;
    public Button button;

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

    public void click() {
        // Thx ChatGPT
        InputEvent down = new InputEvent();
        down.setType(InputEvent.Type.touchDown);
        down.setButton(Input.Buttons.LEFT);
        down.setStageX(button.getX());
        down.setStageY(button.getY());
        button.fire(down);

        InputEvent up = new InputEvent();
        up.setType(InputEvent.Type.touchUp);
        up.setButton(Input.Buttons.LEFT);
        up.setStageX(button.getX());
        up.setStageY(button.getY());
        button.fire(up);
    }

    public void hoverEnter() {
        InputEvent e = new InputEvent();
        e.setType(InputEvent.Type.enter);
        e.setPointer(-1);
        button.fire(e);
    }

    public void hoverExit() {
        InputEvent e = new InputEvent();
        e.setType(InputEvent.Type.exit);
        e.setPointer(-1);
        button.fire(e);
    }

    public void onClick(Runnable action) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
    }

    public void onHoverEnter(Runnable action) {
        button.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (pointer == -1) {
                    action.run();
                }
            }
        });
    }

    public void onHoverExit(Runnable action) {
        button.addListener(new ClickListener() {
            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if (pointer == -1) {
                    action.run();
                }
            }
        });
    }
}
