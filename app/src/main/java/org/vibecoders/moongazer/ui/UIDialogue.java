package org.vibecoders.moongazer.ui;

import static org.vibecoders.moongazer.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;
import org.vibecoders.moongazer.ui.novel.CharacterActor;
import org.vibecoders.moongazer.ui.novel.ChoiceBox;
import org.vibecoders.moongazer.ui.novel.DialogueBoxTransparent;
import org.vibecoders.moongazer.ui.novel.DialogueStep;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UIDialogue {
    public interface ExitCallback {
        void onExit(ExitReason reason, String branch, int step);
    }

    public enum ExitReason {
        ACTION_EXIT,      // DialogueStep with Action.EXIT
        END_OF_BRANCH,    // Reached end of dialogue branch
        CHOICE_EXIT       // Choice selected with targetStep -1
    }

    public Table container;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UIDialogue.class);
    private HashMap<String, CharacterActor> characters;
    private DialogueBoxTransparent dialogue;
    private ChoiceBox choice;
    private String currentBranch;
    private int currentStep = 0;
    private Runnable onComplete;
    private ExitCallback onExit;
    private HashMap<String, List<DialogueStep>> dialogueBranches;
    // UI 
    private float currentOpacity = 0;
    private Image background;
    private Image overlay;
    private Texture overlayTexture;
    private Texture dialogBackgroundTexture;
    private TextureRegion dialogBg;
    private TextureRegion separator;
    private boolean started = false;
    private boolean isEnding = false;

    public UIDialogue(HashMap<String, List<DialogueStep>> dialogueBranches) {
        this(dialogueBranches, "default");
    }

    public UIDialogue(HashMap<String, List<DialogueStep>> dialogueBranches, String startBranch) {
        this.dialogueBranches = dialogueBranches;
        this.currentBranch = startBranch;
        this.characters = new HashMap<>();

        container = new Table();
        container.setFillParent(true);

        Texture bgTexture = Assets.getAsset("textures/main_menu/background.png", Texture.class);
        background = new Image(bgTexture);
        background.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        background.setColor(0.5f, 0.5f, 0.5f, 1f);
        container.addActor(background);

        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(0, 0, 0, 0.4f);
        overlayPixmap.fill();
        overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();
        overlay = new Image(overlayTexture);
        overlay.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        container.addActor(overlay);

        dialogBg = createDialogBackground();
        separator = new TextureRegion(Assets.getAsset("textures/vn_scene/separator.png", Texture.class));

        var font = Assets.getFont("ui", 20);
        dialogue = new DialogueBoxTransparent(font, dialogBg, separator, WINDOW_WIDTH - 100);
        dialogue.setPosition(50, 20);
        container.addActor(dialogue);

        container.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent e, int keycode) {
                if (started && choice == null) {
                    nextOrSkip();
                }
                return true;
            }

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                if (started && choice == null) {
                    nextOrSkip();
                    return true;
                }
                return false;
            }
        });
        container.setVisible(false);
    }

    public void start() {
        if (!started) {
            started = true;
            container.setVisible(true);
            showStep(currentBranch, 0);
        }
    }

    public void end() {
        if (started && !isEnding) {
            isEnding = true;
        }
    }

    private TextureRegion createDialogBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        dialogBackgroundTexture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(dialogBackgroundTexture);
    }

    private void nextOrSkip() {
        if (!dialogue.isDone()) {
            dialogue.skip();
            return;
        }
        showStep(currentBranch, ++currentStep);
    }

    private CharacterActor loadCharacter(String speakerAsset) {
        if (!characters.containsKey(speakerAsset)) {
            TextureRegion texture = new TextureRegion(Assets.getAsset(speakerAsset, Texture.class));
            CharacterActor actor = new CharacterActor(texture);
            float charX = (WINDOW_WIDTH - actor.getWidth()) / 2f;
            float charY = (WINDOW_HEIGHT - actor.getHeight()) / 2f + 100;
            actor.setPosition(charX, charY);
            characters.put(speakerAsset, actor);
        }
        return characters.get(speakerAsset);
    }

    private void showStep(String branch, int stepIndex) {
        if (choice != null) {
            choice.remove();
            choice = null;
        }

        List<DialogueStep> steps = dialogueBranches.get(branch);
        if (steps == null || stepIndex >= steps.size()) {
            if (onExit != null) {
                onExit.onExit(ExitReason.END_OF_BRANCH, branch, stepIndex);
            }
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        DialogueStep step = steps.get(stepIndex);
        currentBranch = branch;
        currentStep = stepIndex;

        // Handle character display
        if (step.getSpeakerAsset() != null && !step.getSpeakerAsset().isEmpty()) {
            CharacterActor actor = loadCharacter(step.getSpeakerAsset());
            if (actor.getParent() == null) {
                container.addActor(actor);
            }
            // Hide other characters
            for (Map.Entry<String, CharacterActor> entry : characters.entrySet()) {
                if (!entry.getKey().equals(step.getSpeakerAsset())) {
                    CharacterActor otherActor = entry.getValue();
                    if (otherActor.getParent() != null) {
                        otherActor.remove();
                    }
                }
            }
        } else {
            // Hide all characters if no speaker asset
            for (CharacterActor actor : characters.values()) {
                if (actor.getParent() != null) {
                    actor.remove();
                }
            }
        }

        dialogue.toFront();
        dialogue.setDialogue(step.getSpeaker(), step.getText());

        // Handle actions
        switch (step.getAction()) {
            case EXIT:
                // Will exit on next click/key press
                break;

            case CONTINUE:
                // Normal flow, will continue to next step on click
                break;

            case CHOICE:
                if (step.hasChoices()) {
                    var font = Assets.getFont("ui", 18);
                    DialogueStep.Choice[] choices = step.getChoices();
                    String[] choiceTexts = new String[choices.length];
                    for (int i = 0; i < choices.length; i++) {
                        choiceTexts[i] = choices[i].getText();
                    }

                    choice = new ChoiceBox(font, choiceTexts, idx -> {
                        Audio.playSfxSelect();
                        DialogueStep.Choice selectedChoice = choices[idx];
                        String targetBranch = selectedChoice.getTargetBranch();
                        int targetStep = selectedChoice.getTargetStep();

                        if (targetBranch == null || targetBranch.isEmpty()) {
                            targetBranch = currentBranch;
                        }

                        if (targetStep == -1) {
                            if (onExit != null) {
                                onExit.onExit(ExitReason.CHOICE_EXIT, currentBranch, currentStep);
                            }
                            if (onComplete != null) {
                                onComplete.run();
                            }
                        } else {
                            showStep(targetBranch, targetStep);
                        }
                    });
                    choice.setPosition(WINDOW_WIDTH - 260, WINDOW_HEIGHT / 2);
                    container.addActor(choice);
                }
                break;
        }

        // If action is EXIT, trigger completion on next interaction
        if (step.getAction() == DialogueStep.Action.EXIT) {
            // Override nextOrSkip behavior temporarily
            container.clearListeners();
            container.addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent e, int keycode) {
                    if (started && choice == null) {
                        if (onExit != null) {
                            onExit.onExit(ExitReason.ACTION_EXIT, currentBranch, currentStep);
                        }
                        if (onComplete != null) {
                            onComplete.run();
                        }
                    }
                    return true;
                }

                @Override
                public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                    if (started && choice == null) {
                        if (onExit != null) {
                            onExit.onExit(ExitReason.ACTION_EXIT, currentBranch, currentStep);
                        }
                        if (onComplete != null) {
                            onComplete.run();
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    public void setOnComplete(Runnable onComplete) {
        this.onComplete = onComplete;
    }

    public void setOnExit(ExitCallback onExit) {
        this.onExit = onExit;
    }

    public void render() {
        if (isEnding && currentOpacity > 0f) {
            currentOpacity -= Gdx.graphics.getDeltaTime() * 3;
            var opacity = new Color(1f, 1f, 1f, currentOpacity);
            container.setColor(opacity);
            background.setColor(opacity);
            overlay.setColor(opacity);
            dialogue.setColor(opacity);
            for (CharacterActor actor : characters.values()) {
                actor.setColor(opacity);
            }
            if (choice != null) {
                choice.setColor(opacity);
            }
            if (currentOpacity <= 0f) {
                started = false;
                isEnding = false;
                dispose();
            }
            return;
        }
        if (started && currentOpacity < 1f) {
            currentOpacity += Gdx.graphics.getDeltaTime() * 3;
            var opacity = new Color(1f, 1f, 1f, currentOpacity);
            container.setColor(opacity);
            background.setColor(opacity);
            overlay.setColor(opacity);
            dialogue.setColor(opacity);
            for (CharacterActor actor : characters.values()) {
                actor.setColor(opacity);
            }
            if (choice != null) {
                choice.setColor(opacity);
            }
        }
    }

    public void dispose() {
        container.setVisible(false);
        if (overlayTexture != null) {
            overlayTexture.dispose();
            overlayTexture = null;
        }
        if (dialogBackgroundTexture != null) {
            dialogBackgroundTexture.dispose();
            dialogBackgroundTexture = null;
        }
        if (background != null) {
            background.remove();
            background = null;
        }
        if (overlay != null) {
            overlay.remove();
            overlay = null;
        }
        for (CharacterActor actor : characters.values()) {
            actor.clear();
        }
        characters.clear();
        dialogue.clear();
        if (container != null) {
            var stage = container.getStage();
            container.clear();
            container.remove();
            container = null;
            if (stage != null) {
                log.trace("Forcing stage update after dialogue end");
                stage.act(0);
            }
        }
        log.trace("Dialogue disposed");
    }
}
