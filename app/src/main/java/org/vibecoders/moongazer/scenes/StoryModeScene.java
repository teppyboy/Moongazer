package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UITextButton;
import org.vibecoders.moongazer.ui.storymodeUI.*;
//import org.vibecoders.moongazer.ui.storymodeUI.StoryModeUI;

import java.util.ArrayList;
import java.util.List;

import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;
import static org.vibecoders.moongazer.Constants.WINDOW_WIDTH;

public class StoryModeScene extends Scene {
    private StageSelectionPanel stagePanel;
    private StageInfoPanel infoPanel;
    private ChallengeGoalsPanel goalPanel;
    private StageRewardPanel rewardPanel;
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

        game.stage.addActor(root);
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
        UICloseButton backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(WINDOW_WIDTH - 40, WINDOW_HEIGHT - 40);
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
//            if (game.transition == null) {
//                game.recreateScene(game.testInGameScene, () -> new TestInGameScene(game), scene -> game.testInGameScene = scene);
//                game.transition = new Transition(game, this, game.testInGameScene, State.TEST_IN_GAME, 500);
//            }
        });
    }

    private List<StageData> createStageData() {
        List<StageData> stages = new ArrayList<>();

        stages.add(new StageData(1, "Stage 1",
                "Defend against initial wave of Tacet Discords Defend against initial wave of Tacet Discords Defend" +
                        "Defend against initial wave of Tacet DiscordsDefend against initial wave of Tacet Discords against initial wave of Tacet Discords",
                "textures/stage/MapTest.png"));

        stages.add(new StageData(2, "Stage 2",
                "ProtectDefend against initial wave of Tacet DiscordsDefend against initial wave of Tacet DiscordsDefend against initial wave of Tacet " +
                        "Discords energy nodes from explosive attacks", "textures/mode_selection/Bg3.png"));

        stages.add(new StageData(3, "Stage 3",
                "Protect eneDefend against initial wave of Tacet DiscordsDefend against initial wave of Tacet DiscordsDefend ag" +
                        "ainst initial wave of Tacet Discordsrgy nodes from explosive attacks", "textures/mode_selection/Bg3.png"));

        for (int i = 4; i <= 15; i++) {
            stages.add(new StageData(i, "Stage " + i,
                    "Protect eneDefend against initial wave of Tacet DiscordsDefend against initial wave of Tacet Discord" +
                            "sDefend against initial wave of", "textures/stage/MapTest.png"));
        }

        return stages;
    }

    @Override
    public void render(SpriteBatch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        stagePanel.update(delta);
    }
}
