package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UITextButton;
import org.vibecoders.moongazer.ui.story.ChallengeGoalsPanel;
import org.vibecoders.moongazer.ui.story.MapPanel;
import org.vibecoders.moongazer.ui.story.StageData;
import org.vibecoders.moongazer.ui.story.StageInfoPanel;
import org.vibecoders.moongazer.ui.story.HighScorePanel;
import org.vibecoders.moongazer.ui.story.StageSelectionPanel;
import org.vibecoders.moongazer.SaveGameManager;

import java.util.ArrayList;
import java.util.List;

import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;
import static org.vibecoders.moongazer.Constants.WINDOW_WIDTH;
import static org.vibecoders.moongazer.Constants.PARALLAX_STRENGTH;

public class StoryModeScene extends Scene {
    private StageSelectionPanel stagePanel;
    private StageInfoPanel infoPanel;
    private ChallengeGoalsPanel goalPanel;
    private HighScorePanel highScorePanel;
    private UICloseButton backButton;
    private MapPanel mapPanel;
    private int currentStageID;
    List<StageData> stages;
    private Texture backgroundTexture;

    StoryModeScene(Game game) {
        super(game);
        stages = createStageData();
        createPlayButton();
        createCloseButton();
        setupBackground();

        mapPanel = new MapPanel(root);
        stagePanel = new StageSelectionPanel(root, stages, this::onStageSelected);
        infoPanel = new StageInfoPanel(root);
        goalPanel = new ChallengeGoalsPanel(root);
        highScorePanel = new HighScorePanel(root);
        root.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    backButton.click();
                    return true;
                }
                return false;
            }
        });
        game.stage.addActor(root);
        if (!stages.isEmpty()) {
            onStageSelected(stages.get(0));
        }
    }

    /**
     * Called when a stage is selected in the stage selection panel.
     *
     * @param stageData The data of the selected stage.
     */
    private void onStageSelected(StageData stageData) {
        currentStageID = stageData.getStageId();
        infoPanel.updateInfo(stageData.getInfo());
        mapPanel.updateMap(stageData.getMap(), "Illustration for stage " + stageData.getStageId());
        
        // Load and display high score for this stage
        int highScore = SaveGameManager.getStoryHighScore(stageData.getStageId());
        highScorePanel.updateHighScore(highScore);
    }

    /**
     * Sets up the background texture for the scene.
     */
    private void setupBackground() {
        backgroundTexture = Assets.getAsset("textures/stage/background_story1.png", Texture.class);
        // Don't set background on root - we'll draw it manually in render() for parallax effect
        root.setFillParent(true);
    }

    /**
     * Creates the close button that returns to the main menu.
     */
    private void createCloseButton() {
        backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(WINDOW_WIDTH - 80, WINDOW_HEIGHT - 80);
        root.addActor(backButton.getActor());
        backButton.onClick(() -> {
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.mainMenuScene,
                        State.MAIN_MENU, 350);
            }
        });
    }

    /**
     * Creates the play button that starts the selected stage.
     */
    private void createPlayButton() {
        BitmapFont font = Assets.getFont("ui", 24);
        UITextButton playButton = new UITextButton("Go", font);
        playButton.setSize(270, 70);
        playButton.setPosition(WINDOW_WIDTH - 270 - 5, 40);
        root.addActor(playButton.getActor());

        playButton.onClick(() -> {
            log.debug("Stage " + currentStageID + " clicked");
            if (game.transition == null) {
                if (currentStageID == 1) {
                    game.recreateScene(game.storyStageScene,
                            () -> new org.vibecoders.moongazer.scenes.story.Stage1(game),
                            scene -> game.storyStageScene = scene);
                    game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                }
                if (currentStageID == 2) {
                    game.recreateScene(game.storyStageScene,
                            () -> new org.vibecoders.moongazer.scenes.story.Stage2(game),
                            scene -> game.storyStageScene = scene);
                    game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                }
                if (currentStageID == 3) {
                    game.recreateScene(game.storyStageScene,
                            () -> new org.vibecoders.moongazer.scenes.story.Stage3(game),
                            scene -> game.storyStageScene = scene);
                    game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                }
                if (currentStageID == 4) {
                    game.recreateScene(game.storyStageScene,
                            () -> new org.vibecoders.moongazer.scenes.story.Stage4(game),
                            scene -> game.storyStageScene = scene);
                    game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                }
                if (currentStageID == 5) {
                    game.recreateScene(game.storyStageScene,
                            () -> new org.vibecoders.moongazer.scenes.story.Stage5(game),
                            scene -> game.storyStageScene = scene);
                    game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                } else {
                    log.warn("Stage {} not implemented yet", currentStageID);
                }
            }
        });
    }

    /**
     * Creates the list of stage data for the story mode.
     *
     * @return A list of StageData objects representing each stage.
     */
    private List<StageData> createStageData() {
        List<StageData> stages = new ArrayList<>();

        stages.add(new StageData(1, "Chapter I",
                "Victory?",
                "textures/mode_selection/stage1.png"));
        stages.add(new StageData(2, "Chapter II",
                "Echoes of Oblivion",
                "textures/mode_selection/stage2.png"));
        stages.add(new StageData(3, "Chapter III",
                "The Realm of Chaos",
                "textures/mode_selection/stage3.png"));
        stages.add(new StageData(4, "Chapter IV",
                "Fate Rewritten",
                "textures/mode_selection/stage4.png"));
        stages.add(new StageData(5, "Chapter V",
                "A New Beginning",
                "textures/mode_selection/stage5.png"));
        return stages;
    }

    /**
     * Renders the scene with a parallax background effect.
     *
     * @param batch The SpriteBatch used for rendering.
     */
    @Override
    public void render(SpriteBatch batch) {
        // Apply parallax effect to background
        int mouseX = Gdx.input.getX();
        int mouseY = WINDOW_HEIGHT - Gdx.input.getY();
        float offsetX = ((mouseX - WINDOW_WIDTH / 2f) / WINDOW_WIDTH) * PARALLAX_STRENGTH;
        float offsetY = ((mouseY - WINDOW_HEIGHT / 2f) / WINDOW_HEIGHT) * PARALLAX_STRENGTH;
        
        // Zoom background by 1.05x and center it with parallax offset
        float zoomScale = 1.05f;
        float bgWidth = WINDOW_WIDTH * zoomScale;
        float bgHeight = WINDOW_HEIGHT * zoomScale;
        float bgX = (WINDOW_WIDTH - bgWidth) / 2f + offsetX;
        float bgY = (WINDOW_HEIGHT - bgHeight) / 2f + offsetY;
        
        batch.draw(backgroundTexture, bgX, bgY, bgWidth, bgHeight);
        
        float delta = Gdx.graphics.getDeltaTime();
        stagePanel.update(delta);
    }
}
