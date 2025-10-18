package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UIImageButton;
import org.vibecoders.moongazer.ui.UIScrollbar;
import org.vibecoders.moongazer.ui.UITextButton;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.table;
import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;
import static org.vibecoders.moongazer.Constants.WINDOW_WIDTH;

public class StoryModeScene extends Scene {
    private UICloseButton backButton;
    private UITextButton playButton;
    private Table stagePanel;
    private Table goalPanel;
    private Table rewardPanel;
    private Table infoPanel;
    private ScrollPane scrollPane;
    private Label.LabelStyle labelStyle;
    public BitmapFont font = Assets.getFont("ui", 24);

    StoryModeScene(Game game) {
        super(game);

        Texture background = Assets.getAsset("textures/mode_selection/Bg3.png", Texture.class);
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(background));
        root.setBackground(backgroundDrawable);
        root.setFillParent(true);

        backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(WINDOW_WIDTH - 40, WINDOW_HEIGHT - 40);
        root.addActor(backButton.getActor());
        backButton.onClick(() -> {
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.mainMenuScene,
                        State.MAIN_MENU, 350);
            }
        });

        playButton = new UITextButton("Go", font);
        playButton.setSize(270, 70);
        playButton.setPosition(WINDOW_WIDTH - 260 - 50, 40);
        root.addActor(playButton.getActor());

        playButton.onClick(() -> {
            log.debug("Play clicked");
            if (game.transition == null) {
                game.recreateScene(game.testInGameScene, () -> new TestInGameScene(game), scene -> game.testInGameScene = scene);
                game.transition = new Transition(game, this, game.testInGameScene, State.TEST_IN_GAME, 500);
            }
        });

        this.addChallengeGoalsUI();
        this.stageRewardUI();
        this.stageSelectionUI();
        this.stageInfoUI();
        game.stage.addActor(root);
    }

    private void stageSelectionUI() {
        stagePanel = new Table();

        stagePanel.setSize(400, 550);
        stagePanel.setPosition(10, (WINDOW_HEIGHT - 550) / 2f);

        Table contentTable = new Table();
        contentTable.defaults().pad(4);

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        var tintedBg = bg.tint(new Color(0.5f, 0.5f, 0.5f, 0.5f));

        ArrayList<UIImageButton> stageName = new ArrayList<>();
        stageName.add(new UIImageButton("textures/stage/stage5.png"));
        for (UIImageButton button : stageName) {
            Table row = new Table();
            row.setBackground(tintedBg);
            row.add(button.getActor()).size(260, 75).pad(15);
            contentTable.add(row).width(260).height(75).padBottom(5);
            contentTable.row();
        }

        scrollPane = new ScrollPane(contentTable);
        scrollPane.setHeight(550);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollBarPositions(false, false);

        stagePanel.add(scrollPane).pad(10);
        UIScrollbar customScrollbar = new UIScrollbar(scrollPane, 12, 550);
        stagePanel.add(customScrollbar.getActor()).width(40).height(550).padLeft(10);

        root.addActor(stagePanel);
    }

    private void mapUI() {}

    private void stageInfoUI() {
        infoPanel = new Table();

        infoPanel.setSize(500, 200);
        infoPanel.setPosition(420, 20);

        Table contentTable = new Table();
        contentTable.defaults().pad(2);
        contentTable.left().top();

        Label title = new Label("Simulation Info", labelStyle);
        title.setFontScale(1.05f);
        title.setColor(Color.WHITE);

        String info = "Simulated Scenario: Tacet Discords are targeting explosive energy nodes within the ruins, " +
                        "which might cause irreversible structural collapse. Defend the nodes at all costs.";

        Label description = new Label(info, labelStyle);
        description.setWrap(true);
        description.setAlignment(Align.left);

        contentTable.add(title).left().padBottom(10).row();
        contentTable.add(description).width(540).expandX().fillX().padTop(5);

        infoPanel.add(contentTable).expand().fill().pad(10);
        root.addActor(infoPanel);
    }

    private void addChallengeGoalsUI() {
        goalPanel = new Table();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        goalPanel.setFillParent(true);
        goalPanel.top().right();
        goalPanel.padTop(10).padRight(50);

        Table contentTable = new Table();
        contentTable.defaults().pad(4);

        Label title = new Label("Challenge goals", labelStyle);
        contentTable.add(title).colspan(2).padTop(60).padBottom(15);
        contentTable.row();

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        var tintedBg = bg.tint(new Color(0.5f, 0.5f, 0.5f, 0.5f));

        String[] conditions = { "Remaining Hearth <= 1", "Remaining Hearth <= 2", "Remaining Hearth <= 3" };
        for (String condition : conditions) {
            Table row = new Table();
            row.setBackground(tintedBg);
            row.add(new Label(condition, labelStyle)).expandX().left().padLeft(40).pad(15);
            contentTable.add(row).width(260).height(35).padBottom(5);
            contentTable.row();
        }
        goalPanel.add(contentTable);
        root.addActor(goalPanel);
    }

    private void stageRewardUI() {
        rewardPanel = new Table();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        rewardPanel.setFillParent(true);
        rewardPanel.bottom().right();
        rewardPanel.padBottom(100).padRight(50);

        Table contentTable = new Table();
        contentTable.defaults().pad(4);

        Label title = new Label("Reward", labelStyle);
        rewardPanel.add(title).colspan(2).padTop(60).padBottom(15);
        rewardPanel.row();

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        var tintedBg = bg.tint(new Color(0.5f, 0.5f, 0.5f, 0.5f));

        String[] rewards = {"Lunite"};
        for (String reward : rewards) {
            Table row = new Table();
            row.add(new Label(reward, labelStyle)).expandX().left().padLeft(40).pad(15);
            row.setBackground(tintedBg);
            contentTable.add(row).width(260).height(35).padBottom(5);
            contentTable.row();
        }

        rewardPanel.add(contentTable);
        root.addActor(rewardPanel);
    }

    @Override
    public void render(SpriteBatch batch) {}
}
