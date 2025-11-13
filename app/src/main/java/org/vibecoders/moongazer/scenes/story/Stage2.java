package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.dialogue.DialogueStep;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;

public class Stage2 extends Story {
    private static final String IUNO = "textures/vn_scene/iuno.png";
    private static final String ROVER = "textures/vn_scene/rover.png";
    private static final String ELDER = "textures/vn_scene/elder.png";
    private static final String ROVERIDLE1 = "audio/storysfx/ROVERIDLE1.mp3";
    private static final String ROVERHM = "audio/storysfx/ROVERHM.mp3";
    private static final String ROVERASK = "audio/storysfx/ROVERASK.mp3";
    private static final String IUNOHIT = "audio/storysfx/IUNOHIT.mp3";
    private static final String IUNODIE = "audio/storysfx/IUNODIE.mp3";

    /**
     * Constructs Stage 2 of the story mode.
     *
     * @param game the main game instance
     */
    public Stage2(Game game) {
        super(game, 2);
    }
    
    /**
     * Initializes the intro dialogue for Stage 2.
     * Shows the return to the village and the mysterious disappearance of Iuno from everyone's memory.
     */
    @Override
    protected void initIntroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "",
                        null,
                        "When I returned to the village, joyous cries echoed.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The Village Elder clapped me hard upon the shoulder, his laughter booming.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Village Elder",
                        ELDER,
                        "You have saved us all!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "Speak not so. Without Iuno, this victory would not have been possible.",
                        ROVERIDLE1,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Village Elder",
                        ELDER,
                        "…Iuno? Who is Iuno?",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "She... She who fought beside me!",
                        ROVERHM,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Village Elder",
                        ELDER,
                        "...",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "A sudden silence fell. The blood in my veins turned to ice. I began to run.",
                        DialogueStep.Action.EXIT)
        ));
        introDialogue = new Dialogue(game) {};
        introDialogue.setDialogueBranches(branches, "default");
    }
    
    /**
     * Initializes the outro dialogue for Stage 2.
     * Shows Rover discovering Iuno's fading form and learning about the curse.
     */
    @Override
    protected void initOutroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "",
                        null,
                        "I ran to the sacred grove by the lake, where she once waited for me.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "She was there, but her form was naught but mist and smoke." +
                                "Ethereal, fragile." +
                                "It seemed as though my very breath caused her to dissipate further.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "Why have they forgotten you?",
                        ROVERASK,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        IUNO,
                        "His curse… it was meant for you.",
                        IUNOHIT,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "What did you do?",
                        ROVERASK,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "She held out her hand. Motes of light drifted from her fingertips.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        IUNO,
                        "Live well, Rover. For both of us.",
                        IUNODIE,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I tried to grasp her, but my arms embraced only hopeless emptiness." +
                                "The light faded into nothingness.",
                        DialogueStep.Action.EXIT)
        ));
        outroDialogue = new Dialogue(game) {};
        outroDialogue.setDialogueBranches(branches, "default");
    }
}
