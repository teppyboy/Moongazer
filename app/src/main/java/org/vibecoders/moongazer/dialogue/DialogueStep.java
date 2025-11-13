package org.vibecoders.moongazer.dialogue;

public class DialogueStep {
    private final String speaker;
    private final String speakerAsset;
    private final String text;
    private final String audioAsset;
    private final Action action;
    private final Choice[] choices;

    public enum Action {
        EXIT, // Exit the dialogue screen
        CONTINUE, // Continue to next step
        CHOICE // Present choices to the user
    }

    public static class Choice {
        private final String text;
        private final String targetBranch;
        private final int targetStep;

        /**
         * Constructs a new Choice for dialogue branching.
         *
         * @param text the text to display for this choice
         * @param targetBranch the target dialogue branch to jump to
         * @param targetStep the step index within the target branch
         */
        public Choice(String text, String targetBranch, int targetStep) {
            this.text = text;
            this.targetBranch = targetBranch;
            this.targetStep = targetStep;
        }

        /**
         * Gets the display text for this choice.
         *
         * @return the choice text
         */
        public String getText() {
            return text;
        }

        /**
         * Gets the target branch identifier for this choice.
         *
         * @return the target branch name
         */
        public String getTargetBranch() {
            return targetBranch;
        }

        /**
         * Gets the target step index within the branch.
         *
         * @return the step index
         */
        public int getTargetStep() {
            return targetStep;
        }
    }

    /**
     * Constructs a simple dialogue step with continue action.
     *
     * @param speaker the name of the speaker
     * @param speakerAsset the asset path for the speaker's image
     * @param text the dialogue text to display
     */
    public DialogueStep(String speaker, String speakerAsset, String text) {
        this(speaker, speakerAsset, text, null, Action.CONTINUE, null);
    }

    /**
     * Constructs a dialogue step with a specific action.
     *
     * @param speaker the name of the speaker
     * @param speakerAsset the asset path for the speaker's image
     * @param text the dialogue text to display
     * @param action the action to perform after this step
     */
    public DialogueStep(String speaker, String speakerAsset, String text, Action action) {
        this(speaker, speakerAsset, text, null, action, null);
    }

    /**
     * Constructs a dialogue step with audio.
     *
     * @param speaker the name of the speaker
     * @param speakerAsset the asset path for the speaker's image
     * @param text the dialogue text to display
     * @param audioAsset the asset path for the audio to play
     */
    public DialogueStep(String speaker, String speakerAsset, String text, String audioAsset) {
        this(speaker, speakerAsset, text, audioAsset, Action.CONTINUE, null);
    }

    /**
     * Constructs a dialogue step with audio and action.
     *
     * @param speaker the name of the speaker
     * @param speakerAsset the asset path for the speaker's image
     * @param text the dialogue text to display
     * @param audioAsset the asset path for the audio to play
     * @param action the action to perform after this step
     */
    public DialogueStep(String speaker, String speakerAsset, String text, String audioAsset, Action action) {
        this(speaker, speakerAsset, text, audioAsset, action, null);
    }

    /**
     * Constructs a complete dialogue step with all options.
     *
     * @param speaker the name of the speaker
     * @param speakerAsset the asset path for the speaker's image
     * @param text the dialogue text to display
     * @param audioAsset the asset path for the audio to play (can be null)
     * @param action the action to perform after this step
     * @param choices array of choices for branching dialogue (can be null)
     */
    public DialogueStep(String speaker, String speakerAsset, String text, String audioAsset, Action action, Choice[] choices) {
        this.speaker = speaker;
        this.speakerAsset = speakerAsset;
        this.text = text;
        this.audioAsset = audioAsset;
        this.action = action;
        this.choices = choices;
    }

    /**
     * Gets the speaker's name.
     *
     * @return the speaker name
     */
    public String getSpeaker() {
        return speaker;
    }

    /**
     * Gets the asset path for the speaker's image.
     *
     * @return the speaker asset path
     */
    public String getSpeakerAsset() {
        return speakerAsset;
    }

    /**
     * Gets the dialogue text.
     *
     * @return the dialogue text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the audio asset path.
     *
     * @return the audio asset path, or null if no audio
     */
    public String getAudioAsset() {
        return audioAsset;
    }

    /**
     * Gets the action to perform after this dialogue step.
     *
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * Gets the available choices for this dialogue step.
     *
     * @return array of choices, or null if no choices
     */
    public Choice[] getChoices() {
        return choices;
    }

    /**
     * Checks if this dialogue step has choices available.
     *
     * @return true if choices are available, false otherwise
     */
    public boolean hasChoices() {
        return action == Action.CHOICE && choices != null && choices.length > 0;
    }
}
