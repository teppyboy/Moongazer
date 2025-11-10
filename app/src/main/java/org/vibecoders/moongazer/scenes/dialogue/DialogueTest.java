package org.vibecoders.moongazer.scenes.dialogue;

import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.List;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.scenes.TestInGameScene;
import org.vibecoders.moongazer.scenes.Transition;
import org.vibecoders.moongazer.dialogue.DialogueStep;

public class DialogueTest extends Dialogue {
    private static final String IUNO_TEXTURE = "textures/vn_scene/iuno.png";
    private float totalTime = 0;
    private boolean dialogStarted = false;

    public DialogueTest(Game game) {
        super(game);
        initDialogue();
    }

    private void initDialogue() {
        var branches = new HashMap<String, List<DialogueStep>>();
        branches.put(
                "default",
                List.of(
                        new DialogueStep(
                                "Iuno",
                                IUNO_TEXTURE,
                                null,
                                "Hmph. Apologies, but I need my rest..."),
                        new DialogueStep(
                                "Iuno",
                                IUNO_TEXTURE,
                                "Choose:",
                                null,
                                DialogueStep.Action.CHOICE,
                                new DialogueStep.Choice[] {
                                        new DialogueStep.Choice("Good path", "good_end", 0),
                                        new DialogueStep.Choice("Bad path", "bad_end", 0),
                                })));
        branches.put(
                "good_end",
                List.of(
                        new DialogueStep(
                                "Iuno",
                                IUNO_TEXTURE,
                                "You chose the good end"),
                        new DialogueStep("Iuno", IUNO_TEXTURE, "Toi yeu tretrauit..."),
                        new DialogueStep(
                                "Iuno",
                                IUNO_TEXTURE,
                                "The End.",
                                DialogueStep.Action.EXIT)));
        branches.put(
                "bad_end",
                List.of(
                        new DialogueStep(
                                "Iuno",
                                IUNO_TEXTURE,
                                "You chose the bad end."),
                        new DialogueStep("Iuno", IUNO_TEXTURE, "Toi ghet tunxd..."),
                        new DialogueStep(
                                "Iuno",
                                IUNO_TEXTURE,
                                "The End.",
                                DialogueStep.Action.EXIT)));
        setDialogueBranches(branches);
        setOnExit((reason, branch, step) -> {
            switch (reason) {
                case ACTION_EXIT:
                    log.trace(
                            "Dialogue reached Action.EXIT at " + branch + ":" + step);
                    if (branch.equals("good_end")) {
                        game.transition = new Transition(
                                game,
                                this,
                                game.mainMenuScene,
                                State.MAIN_MENU,
                                500);
                    } else if (branch.equals("bad_end")) {
                        // Suck workaround but tanks :D
                        game.recreateScene(game.testInGameScene, () -> new TestInGameScene(game), scene -> game.testInGameScene = scene);
                        game.transition = new Transition(
                                game,
                                this,
                                game.testInGameScene,
                                State.TEST_IN_GAME,
                                500);
                    }
                    break;
                case END_OF_BRANCH:
                    log.trace("Reached end of branch: " + branch);
                    break;
                case CHOICE_EXIT:
                    log.trace(
                            "User selected exit choice at " + branch + ":" + step);
                    break;
            }
        });
        setOnComplete(() -> {
            log.trace("Dialogue complete callback triggered.");
        });
        root.addActor(container);
        game.stage.addActor(root);
    }

    @Override
    public void render(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        super.render(batch);
        totalTime += Gdx.graphics.getDeltaTime();
        if (totalTime > 2f) {
            if (dialogStarted) {
                return;
            }
            dialogStarted = true;
            log.debug("Invoking test dialogue start");
            start();
        }
    }
}
