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
    
    /**
     * Constructs a new story scene for the specified stage.
     * Stops menu music and starts stage-specific music.
     *
     * @param game the main game instance
     * @param stageNumber the stage number (1-5)
     */
    public Story(Game game, int stageNumber) {
        super(game);
        this.stageNumber = stageNumber;
        Audio.menuMusicStop();
        playStageMusic();
        startIntro();
    }
    
    /**
     * Initializes the intro dialogue for this stage.
     * Must be implemented by subclasses.
     */
    protected abstract void initIntroDialogue();

    /**
     * Initializes the outro dialogue for this stage.
     * Must be implemented by subclasses.
     */
    protected abstract void initOutroDialogue();
    
    /**
     * Plays the music for the current stage.
     */
    private void playStageMusic() {
        switch (stageNumber) {
            case 1: Audio.stage1MusicPlay(); break;
            case 2: Audio.stage2MusicPlay(); break;
            case 3: Audio.stage3MusicPlay(); break;
            case 4: Audio.stage4MusicPlay(); break;
            case 5: Audio.stage5MusicPlay(); break;
        }
    }
    
    /**
     * Stops the music for the current stage.
     */
    private void stopStageMusic() {
        switch (stageNumber) {
            case 1: Audio.stage1MusicStop(); break;
            case 2: Audio.stage2MusicStop(); break;
            case 3: Audio.stage3MusicStop(); break;
            case 4: Audio.stage4MusicStop(); break;
            case 5: Audio.stage5MusicStop(); break;
        }
    }
    
    /**
     * Initializes the gameplay (Arkanoid) for the current stage.
     * Sets up callbacks and loads save game if applicable.
     */
    protected void initGameplay() {
        // Create appropriate StageArkanoid based on stage number
        switch (stageNumber) {
            case 1:
                gameplay = new Stage1Arkanoid(game, 3);
                break;
            case 2:
                gameplay = new Stage2Arkanoid(game, 3);
                break;
            case 3:
                gameplay = new Stage3Arkanoid(game, 3);
                break;
            case 4:
                gameplay = new Stage4Arkanoid(game, 3);
                break;
            case 5:
                gameplay = new Stage5Arkanoid(game, 3);
                break;
            default:
                log.error("Invalid stage number: {}", stageNumber);
                gameplay = new Stage1Arkanoid(game, 3);
                break;
        }
        
        gameplay.setOnReturnToMainMenu(() -> {
            log.info("Stage " + stageNumber + " quit! Returning to main menu");
            stopStageMusic();
            Audio.menuMusicPlay();
            if (game.transition == null && game.mainMenuScene != null) {
                game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 500);
            }
        });
        
        // Only load save game if coming from LoadScene (indicated by loadingSaveSlotId)
        if (game.loadingSaveSlotId != -1) {
            log.info("Loading from save slot {} for stage {}", game.loadingSaveSlotId, stageNumber);
            gameplay.loadGame();
        }
    }
    
    /**
     * Starts the intro dialogue phase.
     */
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

    /**
     * Completes the story and returns to main menu.
     */
    protected void onStoryComplete() {
        log.info("Stage " + stageNumber + " complete!");
        stopStageMusic();

        // Check if there's a next stage
        if (stageNumber < 5) {
            // Proceed to next stage
            transitionToNextStage();
        } else {
            // Final stage completed, return to story mode selection
            log.info("All stages complete! Returning to story mode selection");
            Audio.menuMusicPlay();
            if (game.transition == null && game.storyModeScene != null) {
                game.transition = new Transition(game, this, game.storyModeScene, State.STORY_MODE, 500);
            }
        }
    }

    /**
     * Transition to the next stage in the story
     */
    protected void transitionToNextStage() {
        int nextStageNumber = stageNumber + 1;
        log.info("Transitioning to stage {}", nextStageNumber);

        Scene nextStage = createStageScene(nextStageNumber);
        if (nextStage != null && game.transition == null) {
            game.transition = new Transition(game, this, nextStage, State.STORY_STAGE, 500);
        } else {
            log.error("Failed to create stage {} scene, returning to story mode", nextStageNumber);
            Audio.menuMusicPlay();
            if (game.transition == null && game.storyModeScene != null) {
                game.transition = new Transition(game, this, game.storyModeScene, State.STORY_MODE, 500);
            }
        }
    }

    /**
     * Create a stage scene based on stage number
     */
    private Scene createStageScene(int stageNum) {
        switch (stageNum) {
            case 1: return new Stage1(game);
            case 2: return new Stage2(game);
            case 3: return new Stage3(game);
            case 4: return new Stage4(game);
            case 5: return new Stage5(game);
            default:
                log.error("Invalid stage number: {}", stageNum);
                return null;
        }
    }

    /**
     * Renders the current phase of the story.
     *
     * @param batch the sprite batch for rendering
     */
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
    
    /**
     * Disposes of all resources used by the story scene.
     */
    @Override
    public void dispose() {
        if (introDialogue != null) introDialogue.dispose();
        if (outroDialogue != null) outroDialogue.dispose();
        if (gameplay != null) gameplay.dispose();
        super.dispose();
    }
}
