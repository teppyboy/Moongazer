package org.vibecoders.moongazer.scenes.story;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Audio;
import org.vibecoders.moongazer.scenes.Scene;
import org.vibecoders.moongazer.scenes.Transition;
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
    private final int stageNumber;
    
    public Story(Game game, int stageNumber) {
        super(game);
        this.stageNumber = stageNumber;
        Audio.menuMusicStop();
        playStageMusic();
        startIntro();
    }
    
    protected abstract void initIntroDialogue();
    protected abstract void initOutroDialogue();
    
    private void playStageMusic() {
        switch (stageNumber) {
            case 1: Audio.stage1MusicPlay(); break;
            case 2: Audio.stage2MusicPlay(); break;
            case 3: Audio.stage3MusicPlay(); break;
            case 4: Audio.stage4MusicPlay(); break;
            case 5: Audio.stage5MusicPlay(); break;
        }
    }
    
    private void stopStageMusic() {
        switch (stageNumber) {
            case 1: Audio.stage1MusicStop(); break;
            case 2: Audio.stage2MusicStop(); break;
            case 3: Audio.stage3MusicStop(); break;
            case 4: Audio.stage4MusicStop(); break;
            case 5: Audio.stage5MusicStop(); break;
        }
    }
    
    protected void initGameplay() {
        gameplay = new Stage1Arkanoid(game, 3);
        gameplay.setOnReturnToMainMenu(() -> {
            log.info("Stage " + stageNumber + " quit! Returning to main menu");
            stopStageMusic();
            Audio.menuMusicPlay();
            if (game.transition == null && game.mainMenuScene != null) {
                game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 500);
            }
        });
        if (org.vibecoders.moongazer.SaveGameManager.hasSaveGame(stageNumber)) {
            log.info("Found saved game for stage {}, attempting to load...", stageNumber);
            gameplay.loadGame();
        }
    }
    
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
    
    protected void onStoryComplete() {
        log.info("Stage " + stageNumber + " complete! Returning to story mode selection");
        stopStageMusic();
        Audio.menuMusicPlay();
        if (game.transition == null && game.storyModeScene != null) {
            game.transition = new Transition(game, this, game.storyModeScene, State.STORY_MODE, 500);
        }
    }
    
    @Override
    public void render(SpriteBatch batch) {
        switch (currentPhase) {
            case INTRO_DIALOGUE:
                if (introDialogue != null) introDialogue.render(batch);
                break;
            case GAMEPLAY:
                if (gameplay != null) gameplay.render(batch);
                break;
            case OUTRO_DIALOGUE:
                if (outroDialogue != null) outroDialogue.render(batch);
                break;
            case COMPLETE:
                break;
        }
    }
    
    @Override
    public void dispose() {
        if (introDialogue != null) introDialogue.dispose();
        if (outroDialogue != null) outroDialogue.dispose();
        if (gameplay != null) gameplay.dispose();
        super.dispose();
    }
}
