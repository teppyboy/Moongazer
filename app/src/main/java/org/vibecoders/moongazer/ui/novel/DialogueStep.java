package org.vibecoders.moongazer.ui.novel;

public class DialogueStep {
    private final String speaker;
    private final String speakerAsset;
    private final String text;
    private final Action action;
    private final Choice[] choices;

    public enum Action {
        EXIT,      // Exit the dialogue screen
        CONTINUE,  // Continue to next step
        CHOICE     // Present choices to the user
    }

    public static class Choice {
        private final String text;
        private final String targetBranch;
        private final int targetStep;

        public Choice(String text, String targetBranch, int targetStep) {
            this.text = text;
            this.targetBranch = targetBranch;
            this.targetStep = targetStep;
        }

        public String getText() {
            return text;
        }

        public String getTargetBranch() {
            return targetBranch;
        }

        public int getTargetStep() {
            return targetStep;
        }
    }

    // Constructor for simple dialogue (continue action)
    public DialogueStep(String speaker, String speakerAsset, String text) {
        this(speaker, speakerAsset, text, Action.CONTINUE, null);
    }

    // Constructor with action
    public DialogueStep(String speaker, String speakerAsset, String text, Action action) {
        this(speaker, speakerAsset, text, action, null);
    }

    // Full constructor
    public DialogueStep(String speaker, String speakerAsset, String text, Action action, Choice[] choices) {
        this.speaker = speaker;
        this.speakerAsset = speakerAsset;
        this.text = text;
        this.action = action;
        this.choices = choices;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getSpeakerAsset() {
        return speakerAsset;
    }

    public String getText() {
        return text;
    }

    public Action getAction() {
        return action;
    }

    public Choice[] getChoices() {
        return choices;
    }

    public boolean hasChoices() {
        return action == Action.CHOICE && choices != null && choices.length > 0;
    }
}
