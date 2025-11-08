package org.vibecoders.moongazer.scenes.story;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.scenes.Scene;
import org.vibecoders.moongazer.scenes.dialogue.Dialogue;

public abstract class Story extends Scene {
    protected enum StoryPhase {
        INTRO_DIALOGUE,
        GAMEPLAY,
        OUTRO_DIALOGUE,
        COMPLETE
    }
    protected StoryPhase currentPhase = StoryPhase.INTRO_DIALOGUE;
    protected Dialogue introDialogue;
    protected Dialogue outroDialogue;
    protected StoryArkanoid gameplay;
    public Story(Game game) {
        super(game);
    }
    protected abstract void initIntroDialogue();
    protected abstract void initOutroDialogue();
    protected abstract void initGameplay();
    protected void startIntro() {
        currentPhase = StoryPhase.INTRO_DIALOGUE;
        initIntroDialogue();
        introDialogue.setOnComplete(() -> {
            log.info("Intro dialogue complete, starting gameplay");
            currentPhase = StoryPhase.GAMEPLAY;
            introDialogue.dispose();
            introDialogue = null;
            initGameplay();
            gameplay.setOnLevelComplete(() -> {
                log.info("Gameplay complete, starting outro dialogue");
                currentPhase = StoryPhase.OUTRO_DIALOGUE;
                initOutroDialogue();
                outroDialogue.setOnComplete(() -> {
                    log.info("Outro dialogue complete, story finished");
                    currentPhase = StoryPhase.COMPLETE;
                    outroDialogue.dispose();
                    outroDialogue = null;
                    onStoryComplete();
                });
                outroDialogue.start();
            });
        });
        introDialogue.start();
    }
    protected abstract void onStoryComplete();
    @Override
    public void render(SpriteBatch batch) {
        switch (currentPhase) {
            case INTRO_DIALOGUE:
                if (introDialogue != null) {
                    introDialogue.render(batch);
                }
                break;
            case GAMEPLAY:
                if (gameplay != null) {
                    gameplay.render(batch);
                }
                break;
            case OUTRO_DIALOGUE:
                if (outroDialogue != null) {
                    outroDialogue.render(batch);
                }
                break;
            case COMPLETE:
                break;
        }
    }
    @Override
    public void dispose() {
        if (introDialogue != null) {
            introDialogue.dispose();
        }
        if (outroDialogue != null) {
            outroDialogue.dispose();
        }
        if (gameplay != null) {
            gameplay.dispose();
        }
        super.dispose();
    }
}
