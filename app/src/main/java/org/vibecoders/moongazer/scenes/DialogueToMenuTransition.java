package org.vibecoders.moongazer.scenes;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DialogueToMenuTransition extends Transition {
    private final DialogueScene from;
    private final Scene to;
    private float totalTime = 0f;
    private final long duration;

    public DialogueToMenuTransition(Game game, DialogueScene from, Scene to, long duration) {
        super(game, from, to, State.MAIN_MENU, duration);
        this.from = from;
        this.to = to;
        this.duration = duration;
    }

    @Override
    public void render(SpriteBatch batch) {
        totalTime += Gdx.graphics.getDeltaTime();
        float progress = totalTime / (((float) duration) / 1000);

        if (progress >= 1.0f) {
            game.transition = null;
            from.dispose();
            game.setCurrentSceneAndState(to, State.MAIN_MENU);
            return;
        }

        float fromOpacity = 1 - progress;
        from.setAlpha(fromOpacity);
        from.render(batch);

        float toOpacity = progress;
        if (to.root != null) {
            to.root.setVisible(true);
            to.root.setColor(1, 1, 1, toOpacity);
        }
        batch.setColor(1, 1, 1, toOpacity);
        to.render(batch);

        batch.setColor(1, 1, 1, 1);
    }
}
