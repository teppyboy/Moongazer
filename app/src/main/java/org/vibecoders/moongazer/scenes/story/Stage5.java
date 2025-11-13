package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.dialogue.DialogueStep;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;

public class Stage5 extends Story {
    private static final String IUNO = "textures/vn_scene/iuno.png";
    private static final String ROVER = "textures/vn_scene/rover.png";
    private static final String ELDER = "textures/vn_scene/elder.png";
    private static final String ROVERASK = "audio/storysfx/ROVERASK.mp3";
    private static final String IUNOINTRO = "audio/storysfx/IUNOINTRO.mp3";
    private static final String IUNODONE3 = "audio/storysfx/IUNODONE3.mp3";
    private static final String ROVERHM = "audio/storysfx/ROVERHM.mp3";

    /**
     * Constructs Stage 5 (final stage) of the story mode.
     *
     * @param game the main game instance
     */
    public Stage5(Game game) {
        super(game, 5);
    }
    
    /**
     * Initializes the intro dialogue for Stage 5.
     * Shows Rover's anxiety about whether Iuno has truly returned.
     */
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
                        ROVER,
                        "Elder, Iuno... have you seen her?",
                        ROVERASK,
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
    
    /**
     * Initializes the outro dialogue for Stage 5.
     * Shows the happy reunion with Iuno, confirming she has been successfully restored to reality.
     */
    @Override
    protected void initOutroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "Village Elder",
                        ELDER,
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
                        IUNO,
                        "Rover! There you are! I've been looking everywhere for you!",
                        IUNOINTRO,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I pulled her into an embrace, holding her tightly." +
                                "That familiar, heart-aching scent. This warmth… it was real.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        IUNO,
                        "You're squeezing me! What is wrong with you?" +
                                "It's as if... we haven't seen each other in a century.",
                        IUNODONE3,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "For me... it truly has been.",
                        ROVERHM,
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
