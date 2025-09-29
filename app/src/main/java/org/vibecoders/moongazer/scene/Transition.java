package org.vibecoders.moongazer.scene;

import org.slf4j.Logger;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Handles transitions between scenes with a linear transition effect.
 */
public class Transition extends Scene {
    private Scene from;
    private Scene to;
    private State targetState;
    private long startTime;
    private long duration;

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Transition.class);

    /**
     * Creates a new transition between two scenes.
     * @param from The scene to transition from.
     * @param to The scene to transition to.
     * @param targetState The target state of the game after the transition.
     * @param duration The duration of the transition in milliseconds.
     */
    public Transition(Scene from, Scene to, State targetState, long duration) {
        this.from = from;
        this.to = to;
        this.targetState = targetState;
        this.duration = duration;
        startTime = System.currentTimeMillis();
    }

    /**
     * Renders the transition effect.
     * @param batch The SpriteBatch to draw with.
     */
    @Override
    public void render(SpriteBatch batch) {
        var toOpacity = ((float) (System.currentTimeMillis() - startTime)) / duration;
        if (toOpacity >= 0.99) {
            log.debug("Transition complete to state: {}", targetState);
            Game.state = targetState;
            Game.transition = null;
            return;
        }
        var fromOpacity = 1 - toOpacity;
        log.debug("Transition opacities - from: {}, to: {}", fromOpacity, toOpacity);
        batch.setColor(1, 1, 1, fromOpacity);
        from.render(batch);
        batch.setColor(1, 1, 1, toOpacity);
        to.render(batch);
    }
}
