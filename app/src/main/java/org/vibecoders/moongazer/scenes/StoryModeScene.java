package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UITextButton;
import org.vibecoders.moongazer.ui.story.ChallengeGoalsPanel;
import org.vibecoders.moongazer.ui.story.MapPanel;
import org.vibecoders.moongazer.ui.story.StageData;
import org.vibecoders.moongazer.ui.story.StageInfoPanel;
import org.vibecoders.moongazer.ui.story.StageRewardPanel;
import org.vibecoders.moongazer.ui.story.StageSelectionPanel;

import java.util.ArrayList;
import java.util.List;

import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;
import static org.vibecoders.moongazer.Constants.WINDOW_WIDTH;

public class StoryModeScene extends Scene {
    private StageSelectionPanel stagePanel;
    private StageInfoPanel infoPanel;
    private ChallengeGoalsPanel goalPanel;
    private StageRewardPanel rewardPanel;
    private UICloseButton backButton;
    private MapPanel mapPanel;
    private int currentStageID;
    List<StageData> stages;

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
        rewardPanel = new StageRewardPanel(root);
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

    private void onStageSelected(StageData stageData) {
        currentStageID = stageData.getStageId();
        infoPanel.updateInfo(stageData.getInfo());
        mapPanel.updateMap(stageData.getMap(), "Map for stage " + stageData.getStageId());
    }

    private void setupBackground() {
        root.setBackground(new TextureRegionDrawable(Assets.getBlackTexture()));
        root.setFillParent(true);
    }

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

    private void createPlayButton() {
        BitmapFont font = Assets.getFont("ui", 24);
        UITextButton playButton = new UITextButton("Go", font);
        playButton.setSize(270, 70);
        playButton.setPosition(WINDOW_WIDTH - 260 - 50, 40);
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

    private List<StageData> createStageData() {
        List<StageData> stages = new ArrayList<>();

        stages.add(new StageData(1, "Chapter I",
                "Iuno",
                "textures/stage/MapTest.png"));
        stages.add(new StageData(2, "Chapter II",
                "Phoebe",
                "textures/mode_selection/Bg3.png"));
        stages.add(new StageData(3, "Chapter III",
                "Protect energy nodes from explosive attacks",
                "textures/mode_selection/Bg3.png"));
        stages.add(new StageData(4, "Chapter IV",
                "Defeat the enemy flagship",
                "textures/mode_selection/Bg3.png"));
        stages.add(new StageData(5, "Chapter V",
                "Final Showdown",
                "textures/mode_selection/Bg3.png"));
        return stages;
    }

    @Override
    public void render(SpriteBatch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        stagePanel.update(delta);
    }
}
