package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pool;

public abstract class UIButton {
    public Actor actor;
    public Button button;
    private static final Pool<InputEvent> eventPool = new Pool<InputEvent>() {
        @Override
        protected InputEvent newObject() {
            return new InputEvent();
        }
    };

    /**
     * Gets the actor associated with this button.
     *
     * @return the actor instance
     */
    public Actor getActor() {
        return actor;
    }

    /**
     * Sets the position of the button.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void setPosition(int x, int y) {
        actor.setPosition(x, y);
    }

    /**
     * Sets the size of the button.
     *
     * @param width the width of the button
     * @param height the height of the button
     */
    public void setSize(int width, int height) {
        actor.setSize(width, height);
    }

    /**
     * Adds an event listener to the button.
     *
     * @param eventListener the event listener to add
     */
    public void addEventListener(EventListener eventListener) {
        actor.addListener(eventListener);
    }

    /**
     * Programmatically triggers a click on this button.
     * Fires both touchDown and touchUp events.
     */
    public void click() {
        InputEvent down = eventPool.obtain();
        down.setType(InputEvent.Type.touchDown);
        down.setButton(Input.Buttons.LEFT);
        down.setStageX(button.getX());
        down.setStageY(button.getY());
        button.fire(down);
        eventPool.free(down);
        InputEvent up = eventPool.obtain();
        up.setType(InputEvent.Type.touchUp);
        up.setButton(Input.Buttons.LEFT);
        up.setStageX(button.getX());
        up.setStageY(button.getY());
        button.fire(up);
        eventPool.free(up);
    }

    /**
     * Programmatically triggers a hover enter event on this button.
     */
    public void hoverEnter() {
        InputEvent e = eventPool.obtain();
        e.setType(InputEvent.Type.enter);
        e.setPointer(-1);
        button.fire(e);
        eventPool.free(e);
    }

    /**
     * Programmatically triggers a hover exit event on this button.
     */
    public void hoverExit() {
        InputEvent e = eventPool.obtain();
        e.setType(InputEvent.Type.exit);
        e.setPointer(-1);
        button.fire(e);
        eventPool.free(e);
    }

    /**
     * Sets the action to be executed when the button is clicked.
     *
     * @param action the runnable action to execute on click
     */
    public void onClick(Runnable action) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
    }

    /**
     * Sets the action to be executed when the mouse hovers over the button.
     *
     * @param action the runnable action to execute on hover enter
     */
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

    /**
     * Sets the action to be executed when the mouse leaves the button.
     *
     * @param action the runnable action to execute on hover exit
     */
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
