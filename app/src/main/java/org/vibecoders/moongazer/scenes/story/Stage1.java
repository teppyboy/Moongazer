package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.dialogue.DialogueStep;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;

public class Stage1 extends Story {
    private static final String IUNO = "textures/vn_scene/iuno.png";
    private static final String ROVER = "textures/vn_scene/rover.png";
    private static final String CREP = "textures/vn_scene/crep.png";
    private static final String ROVERLIBER = "audio/storysfx/ROVERLIBER.mp3";
    private static final String IUNOFIGHT = "audio/storysfx/IUNOFIGHT.mp3";
    private static final String IUNOLIBERSFX = "audio/storysfx/IUNOLIBERSFX.mp3";

    /**
     * Constructs Stage 1 of the story mode.
     *
     * @param game the main game instance
     */
    public Stage1(Game game) {
        super(game, 1);
    }
    
    /**
     * Initializes the intro dialogue for Stage 1.
     * Shows the confrontation with The False Sovereign.
     */
    @Override
    protected void initIntroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "",
                        null,
                        "Smoke. Fire. And the earth did tremble.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "Before me stood The False Sovereign,an entity woven from hatred and chaos itself.\n" +
                                "The entire battlefield was a tempest of light and shadow.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "The False Sovereign",
                        CREP,
                        "Thou canst not defeat me, mortal. Thy destiny is already ordained.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "Then I shall write my own destiny.",
                        ROVERLIBER,
                        DialogueStep.Action.EXIT)
        ));
        introDialogue = new Dialogue(game) {};
        introDialogue.setDialogueBranches(branches, "default");
    }
    
    /**
     * Initializes the outro dialogue for Stage 1.
     * Shows the aftermath of the battle and Iuno's ritual.
     */
    @Override
    protected void initOutroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "",
                        null,
                        "The blade in my hand blazed forth." +
                                "My body may be rent and broken, but my will roared, forged in the fires of adrenaline." +
                                "I wagered all upon a single strike. Light clashed with shadow.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The world itself seemed to shatter.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "And in the distance, Iuno." +
                                "She was performing a ritual." +
                                "Ancient runes flared to life, magic pouring from her." +
                                "The winds howled, swirling around her delicate form.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        IUNO,
                        "Victory shall be ours.",
                        IUNOFIGHT,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "A deluge of light consumed all.",
                        IUNOLIBERSFX,
                        DialogueStep.Action.EXIT)
        ));
        outroDialogue = new Dialogue(game) {};
        outroDialogue.setDialogueBranches(branches, "default");
    }
}
