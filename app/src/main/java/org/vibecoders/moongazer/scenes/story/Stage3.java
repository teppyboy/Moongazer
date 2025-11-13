package org.vibecoders.moongazer.scenes.story;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.dialogue.DialogueStep;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

import java.util.HashMap;
import java.util.List;

public class Stage3 extends Story {
    private static final String IUNO = "textures/vn_scene/iuno.png";
    private static final String ROVER = "textures/vn_scene/rover.png";
    private static final String LILI = "textures/vn_scene/lili.png";
    private static final String CREP = "textures/vn_scene/crep.png";
    private static final String ROVERHM = "audio/storysfx/ROVERHM.mp3";
    private static final String ROVERGO = "audio/storysfx/ROVERGO.mp3";
    private static final String IUNOSAVE = "audio/storysfx/IUNOSAVE.mp3";
    private static final String ROVERHIT = "audio/storysfx/ROVERHIT.mp3";
    private static final String IUNOFIGHT2 = "audio/storysfx/IUNOFIGHT2.mp3";

    /**
     * Constructs Stage 3 of the story mode.
     *
     * @param game the main game instance
     */
    public Stage3(Game game) {
        super(game, 3);
    }
    
    /**
     * Initializes the intro dialogue for Stage 3.
     * Shows Rover seeking help from Lillibet at the Ancient Library to save Iuno.
     */
    @Override
    protected void initIntroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "",
                        null,
                        "I sought out the Great Ancient Library." +
                                "Amidst the towering, ceiling-high shelves, Lillibet, the prietess looked up.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Lillibet",
                        LILI,
                        "You seek one who has been erased from the very weave of reality.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "So you know.",
                        ROVERHM,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Lillibet",
                        LILI,
                        "She used the forbidden rite, Foresight Fugue." +
                                "Its toll is existence itself.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "I must bring her back.",
                        ROVERGO,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Lillibet",
                        LILI,
                        "It is no simple task." +
                                "She is adrift in the Realm of Chaos—the burial ground of all forgotten memories." +
                                "Your memory is the only compass." +
                                "You must forge a Fated Anchor, sculpted from the very memories of you two.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I nodded, knowing what I must do next.",
                        DialogueStep.Action.EXIT)
        ));
        introDialogue = new Dialogue(game) {};
        introDialogue.setDialogueBranches(branches, "default");
    }
    
    /**
     * Initializes the outro dialogue for Stage 3.
     * Shows Rover's journey through the Realm of Chaos to find memory fragments.
     */
    @Override
    protected void initOutroDialogue() {
        HashMap<String, List<DialogueStep>> branches = new HashMap<>();
        branches.put("default", List.of(
                new DialogueStep(
                        "",
                        null,
                        "A formless darkness. An ocean of shattered realities.\n" +
                                "The Chaos was not empty. It resisted me.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Shadowy Voice",
                        null,
                        "\"She never existed.\" \"Thou art merely alone.\" \"Forget her.\"",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I walked on. I ignored the whispers." +
                                "The very ground beneath my feet shifted constantly." +
                                "A familiar library corridor would twist into a blood-soaked battlefield, then dissolve into an endless sea of black sand.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I sought the first fragment. It was not merely lost; it was buried." +
                                "I saw a version of myself, a happy Rover, laughing with... someone faceless.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "The Chaos",
                        null,
                        "Dost thou see? There was never anyone there.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "I focused on the feeling of that day—the scent of apple tarts, the warmth of her hand in mine.\n" +
                                "The illusion shattered. A small shard of light—her laughter—hovered, trembling. I seized it.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "My very soul felt worn thin, as if millennia had passed in my wandering. But still, I pressed on.\n" +
                                "Each memory I reclaimed was a battle against oblivion itself, against the entropy of this universe.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The journey continued and the fragments keep increased. Two, then three,...",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "And then, I stood before the final piece. It was not hidden.\n" +
                                "It simply was, hovering over a fractured stone plinth in the void." +
                                "When I touched it, the world around me dissolved… and I saw… the truth.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The battlefield." +
                                "But this time, I saw through her eyes." +
                                "I saw myself, about to fall.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "The False Sovereign",
                        CREP,
                        "You wish to save him? His destiny has ended.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        IUNO,
                        "Not here. Not before me.",
                        IUNOSAVE,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "'She' closed her eyes, her hands forming seals." +
                                "Ancient, blazing sigils burned themselves onto her arms." +
                                "The ground around her began to crack, the very air twisting.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "The False Sovereign",
                        CREP,
                        "Ha! 'Foresight Fugue'? You dare use that Forbidden Magic? You know the price!" +
                                "The price is existence itself! Thy name! The memory of thee! All shall be erased!",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Iuno",
                        IUNO,
                        "Yes, together with thee.",
                        IUNOFIGHT2,
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "A torrent of white light erupted from her, bending my fate and erasing her own.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "",
                        null,
                        "The memory shattered." +
                                "The seven fragments flew out, swirling, crystallizing into a radiant object." +
                                "The Fated Anchor.",
                        DialogueStep.Action.CONTINUE),
                new DialogueStep(
                        "Rover",
                        ROVER,
                        "Iuno...",
                        ROVERHIT,
                        DialogueStep.Action.EXIT)
        ));
        outroDialogue = new Dialogue(game) {};
        outroDialogue.setDialogueBranches(branches, "default");
    }
}
