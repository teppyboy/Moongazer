package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.dialogue.DialogueStep;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.scenes.Transition;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;

public class Stage1 extends Story {
    public Stage1(Game game) {
        super(game);
        startIntro();
    }
    @Override
    protected void initIntroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "Iuno",
                        null,
                        "Year 2157... The lunar colony \"Moongazer\" has detected anomalies.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Commander",
                        null,
                        "Strange crystalline structures are forming around the base. They're blocking our communications!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "You",
                        null,
                        "I'll take the prototype energy paddle. If I can break through these crystals, we might restore the link.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Commander",
                        null,
                        "Be careful out there. We're counting on you, Moongazer!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "System",
                        null,
                        "Mission: Break through the crystal barrier. Some structures are reinforced - avoid them.",
                        DialogueStep.Action.EXIT)
        ));
        introDialogue = new Dialogue(game) {};
        introDialogue.setDialogueBranches(branches, "default");
    }
    @Override
    protected void initOutroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "You",
                        null,
                        "I did it! The crystal barrier is destroyed!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Commander",
                        null,
                        "Excellent work! Communications are back online. The moon base is safe again.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "System",
                        null,
                        "But this is only the beginning... More anomalies detected in other sectors.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "You",
                        null,
                        "Then I'll be ready. Moongazer, out.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "System",
                        null,
                        "Mission Complete. Stage 1 cleared!",
                        DialogueStep.Action.EXIT)
        ));
        outroDialogue = new Dialogue(game) {};
        outroDialogue.setDialogueBranches(branches, "default");
    }
    @Override
    protected void initGameplay() {
        gameplay = new Stage1Arkanoid(game, 3);
    }
    @Override
    protected void onStoryComplete() {
        log.info("Stage 1 complete! Returning to story mode selection");
        if (game.transition == null && game.storyModeScene != null) {
            game.transition = new Transition(game, this, game.storyModeScene,
                    State.STORY_MODE, 500);
        }
    }
}
