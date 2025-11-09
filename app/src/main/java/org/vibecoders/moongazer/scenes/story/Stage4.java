package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.dialogue.DialogueStep;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;

public class Stage4 extends Story {
    public Stage4(Game game) {
        super(game, 4);
    }
    
    @Override
    protected void initIntroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "",
                        null,
                        "I raised the Fated Anchor high. Its light tore open the Realm of Chaos.\n" +
                                "Through the rift in reality, I saw her—adrift, floating in the endless void.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "But then...",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "A tolling bell rang out, not from the void, but from the very fabric of reality." +
                                "An immense pressure sought to crush the Chaos Realm.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "A figure stepped forth from the rift of light, the Fate Arbiter." +
                                "His visage was hidden by a silver mask etched with ancient glyphs, his eyes like a thousand timelines flowing in reverse.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Fate Arbiter",
                        null,
                        "Rover of the mortal realm." +
                                "Thou dost commit the ultimate heresy." +
                                "Dost thou seek to rewrite reality, to challenge the tapestry woven by the Ordinance of Destiny?",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "I seek only to bring her home.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Fate Arbiter",
                        null,
                        "None are forgotten without cause." +
                                "All existence demands balance." +
                                "If thou pullest her from the Void, something else must take her place." +
                                "A world, a memory, or thou thyself.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "I will lose nothing more!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The light of the Anchor flared, defying the very space around us.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The Fate Arbiter raised his hand." +
                                "From his fingertips, chains of time erupted, forming a colossal circle—the Sigil of Equilibrium.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Fate Arbiter",
                        null,
                        "Thou shalt be erased from every branch of reality" +
                                "No timeline shall ever again bear the entity known as Rover.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "Then I shall carve my name with this memory!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I plunged the Anchor into the floor of the void.",
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
                        "Two forces memory and destiny collided, shattering the chaotic sky.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The Fate Arbiter launched shards of reality, striking at my very soul.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "But I held fast to the Anchor." +
                                "Every time he erased one memory, I illuminated it with another." +
                                "Her smile. Her voice calling my name in the rain." +
                                "The look in her eyes before she vanished." +
                                "All merged, forging a blade of light—the Eclipse Memory.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Fate Arbiter",
                        null,
                        "Thou canst not win! Thou art but a mortal!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "Laws are written by those who fear loss. I... do not.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "I lunged, piercing the darkness, plunging the blade forth to end the conflict.\n" +
                                "The light of memory tore through all.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Fate Arbiter",
                        null,
                        "You… have rewritten fate. But remember… none… escape the price…",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The Fate Arbiter dissolved, leaving only silence." +
                                "The light from the Fated Anchor stabilized, and a form materialized within it, Iuno.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        null,
                        "I told you to look after yourself.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        null,
                        "I was merely taking back what was mine",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        null,
                        "Then... let us return to where we belong.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "She placed her hand on my chest." +
                                "The light spread, consuming the void around us.",
                        DialogueStep.Action.EXIT)
        ));
        outroDialogue = new Dialogue(game) {};
        outroDialogue.setDialogueBranches(branches, "default");
    }
}
