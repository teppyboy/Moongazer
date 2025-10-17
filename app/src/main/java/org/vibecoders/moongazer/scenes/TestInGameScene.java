package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.dialogue.DialogueTest;

public class TestInGameScene extends Scene {
    private float totalTime = 0;
    private boolean dialogStarted = false;

    public TestInGameScene(Game game) {
        super(game);
    }

    public void render(SpriteBatch batch) {
        totalTime += Gdx.graphics.getDeltaTime();
        if (totalTime > 3f) {
            if (dialogStarted) {
                return;
            }
            dialogStarted = true;
            game.recreateScene(game.currentDialogue, () -> new DialogueTest(game), scene -> game.currentDialogue = scene);
            game.transition = new Transition(
                    game,
                    this,
                    game.currentDialogue,
                    State.DIALOGUE,
                    1000);
        }
        batch.draw(
                Assets.getWhiteTexture(),
                0,
                0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
    }
}
