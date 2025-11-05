package org.vibecoders.moongazer.scenes;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Handles transitions between scenes with a linear transition effect.
 */
public class Transition extends Scene {
    private Scene from;
    private Scene to;
    private State targetState;
    private float totalTime = 0f;
    private long duration;

    /**
     * Creates a new transition between two scenes.
     * 
     * @param from        The scene to transition from.
     * @param to          The scene to transition to.
     * @param targetState The target state of the game after the transition.
     * @param duration    The duration of the transition in milliseconds.
     */
    public Transition(Game game, Scene from, Scene to, State targetState, long duration) {
        // Transition does not need to render UI elements
        super(game);
        this.root = null;
        this.from = from;
        this.to = to;
        this.targetState = targetState;
        this.duration = duration;
    }

    /**
     * Renders the transition effect.
     * 
     * @param batch The SpriteBatch to draw with.
     */
    @Override
    public void render(SpriteBatch batch) {
        totalTime += Gdx.graphics.getDeltaTime();
        var toOpacity = totalTime / (((float) duration) / 1000);
        if (toOpacity >= 0.99) {
            log.trace("Transition complete to state: {}", targetState);
            game.state = targetState;
            game.transition = null;
            // Set keyboard focus to the new scene's root
            from.root.setVisible(false);
            to.root.setVisible(true);
            game.stage.setKeyboardFocus(to.root);
            // Reset batch color to full opacity
            batch.setColor(1, 1, 1, 1);
            return;
        }
        var fromOpacity = 1 - toOpacity;
        log.trace("Transition opacities - from: {}, to: {}", fromOpacity, toOpacity);
        batch.setColor(1, 1, 1, fromOpacity);
        from.root.setVisible(true);
        from.root.setColor(1, 1, 1, fromOpacity);
        from.render(batch);
        batch.setColor(1, 1, 1, toOpacity);
        to.root.setVisible(true);
        to.root.setColor(1, 1, 1, toOpacity);
        to.render(batch);
    }
}