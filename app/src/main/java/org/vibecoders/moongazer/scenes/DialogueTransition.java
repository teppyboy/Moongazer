package org.vibecoders.moongazer.scenes;

import org.vibecoders.moongazer.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DialogueTransition extends Transition {
    private final Scene from;
    private final DialogueScene to;
    private float totalTime = 0f;
    private final long duration;

    public DialogueTransition(Game game, Scene from, DialogueScene to, long duration) {
        super(game, from, to, null, duration);
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
            to.setAlpha(1f);
            game.setScene(to);
            return;
        }

        float fromOpacity = 1 - progress;
        if (from.root != null) {
            from.root.setVisible(true);
            from.root.setColor(1, 1, 1, fromOpacity);
        }
        batch.setColor(1, 1, 1, fromOpacity);
        from.render(batch);

        to.setAlpha(progress);
        batch.setColor(1, 1, 1, 1);
        to.render(batch);

        batch.setColor(1, 1, 1, 1);
    }
}