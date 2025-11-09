package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.dialogue.DialogueStep;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;

public class Stage2 extends Story {
    public Stage2(Game game) {
        super(game, 2);
    }
    
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
                        null,
                        "You have saved us all!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "Speak not so. Without Iuno, this victory would not have been possible.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Village Elder",
                        null,
                        "…Iuno? Who is Iuno?",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "She... She who fought beside me!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Village Elder",
                        null,
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
                        null,
                        "Why have they forgotten you?",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        null,
                        "His curse… it was meant for you.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "What did you do?",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "She held out her hand. Motes of light drifted from her fingertips.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        null,
                        "Live well, Rover. For both of us.",
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
