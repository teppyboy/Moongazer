package org.vibecoders.moongazer.scenes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.ui.UIDialogue;
import org.vibecoders.moongazer.ui.novel.DialogueStep;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class InGameScene extends Scene {
    private UIDialogue dialogue;
    private static final String IUNO_TEXTURE = "textures/vn_scene/iuno.png";
    private boolean started = false;

    public InGameScene(Game game) {
        super(game);
        initDialogue();
    }

    private void initDialogue() {
        var branches = new HashMap<String, List<DialogueStep>>();
        branches.put("default", List.of(
                new DialogueStep("Iuno", IUNO_TEXTURE, "Hmph. Apologies, but I need my rest..."),
                new DialogueStep("Iuno", IUNO_TEXTURE, "Choose:", DialogueStep.Action.CHOICE,
                        new DialogueStep.Choice[] {
                                new DialogueStep.Choice("Good path", "good_end", 0),
                                new DialogueStep.Choice("Bad path", "bad_end", 0)
                        })));
        branches.put("good_end", List.of(
                new DialogueStep("Iuno", IUNO_TEXTURE, "You chose the good end"),
                new DialogueStep("Iuno", IUNO_TEXTURE, "Toi yeu tretrauit..."),
                new DialogueStep("Iuno", IUNO_TEXTURE, "The End.", DialogueStep.Action.EXIT)));
        branches.put("bad_end", List.of(
                new DialogueStep("Iuno", IUNO_TEXTURE, "You chose the bad end."),
                new DialogueStep("Iuno", IUNO_TEXTURE, "Toi ghet tunxd..."),
                new DialogueStep("Iuno", IUNO_TEXTURE, "The End.", DialogueStep.Action.EXIT)));
        dialogue = new UIDialogue(branches);
        dialogue.setOnExit((reason, branch, step) -> {
            switch (reason) {
                case ACTION_EXIT:
                    log.trace("Dialogue reached Action.EXIT at " + branch + ":" + step);
                    break;
                case END_OF_BRANCH:
                    log.trace("Reached end of branch: " + branch);
                    break;
                case CHOICE_EXIT:
                    log.trace("User selected exit choice at " + branch + ":" + step);
                    break;
            }
            game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 500);
        });
        dialogue.setOnComplete(() -> {
            log.trace("Dialogue complete callback triggered.");
        });
        root.addActor(dialogue.container);
        game.stage.addActor(root);
    }

    public void render(SpriteBatch batch) {
        if (game.state == State.IN_GAME && !started) {
            dialogue.start();
            started = true;
        }
    }
}
