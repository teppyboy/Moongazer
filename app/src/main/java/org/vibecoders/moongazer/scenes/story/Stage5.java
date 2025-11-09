package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.dialogue.DialogueStep;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;

public class Stage5 extends Story {
    public Stage5(Game game) {
        super(game, 5);
    }
    
    @Override
    protected void initIntroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "",
                        null,
                        "The vision of the ancient library returned.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "A cold wind swept in, but the warmth of her hand lingered on my chest.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "Returning back to the village, I met the Elder passing by, a basket in his hand.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "Elder, Iuno... have you seen her?",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I stood frozen. My heart stopped.",
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
                        "Village Elder",
                        null,
                        "Ah yes, Iuno left this basket of honey-apple tarts for you." +
                                "Said she was off to pick some Sunflowers",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "And then...",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "She was running towards me." +
                                "Her hair flying in the wind." +
                                "A bouquet of flowers in her hand.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        null,
                        "Rover! There you are! I’ve been looking everywhere for you!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I pulled her into an embrace, holding her tightly." +
                                "That familiar, heart-aching scent. This warmth… it was real.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        null,
                        "You're squeezing me! What is wrong with you?" +
                                "It’s as if... we haven't seen each other in a century.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "For me... it truly has been.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I took her hand. The sky was clear and blue. My world... was whole once more.",
                        DialogueStep.Action.EXIT)
        ));
        outroDialogue = new Dialogue(game) {};
        outroDialogue.setDialogueBranches(branches, "default");
    }
}
