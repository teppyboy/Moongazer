package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.SaveGameManager;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UITextButton;
import org.vibecoders.moongazer.ui.UIScrollbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Scene for loading and saving game states.
 */
public class LoadScene extends Scene {
    public enum Mode {
        LOAD, SAVE
    }

    public static class SaveGameData {
        public int stageId;
        public int currentScore;
        public int lives;
        public int bricksDestroyed;
        public String gameStateJson;
        public String progressJson;

        public SaveGameData(int stageId, int currentScore, int lives, int bricksDestroyed,
                           String gameStateJson, String progressJson) {
            this.stageId = stageId;
            this.currentScore = currentScore;
            this.lives = lives;
            this.bricksDestroyed = bricksDestroyed;
            this.gameStateJson = gameStateJson;
            this.progressJson = progressJson;
        }
    }

    private UIScrollbar customScrollbar;
    private ScrollPane scrollPane;
    private static final float KEYBOARD_SCROLL_SPEED = 500f;
    private static final float KEYBOARD_CLICK_SCROLL = 50f;
    private boolean isKeyScrollingUp = false;
    private boolean isKeyScrollingDown = false;
    private float keyScrollAccumulator = 0f;
    private Mode currentMode = Mode.LOAD;
    private SaveGameData pendingSaveData;
    private Table saveList;

    public LoadScene(Game game) {
        super(game);
        buildUI();
    }

    public void setMode(Mode mode) {
        this.currentMode = mode;
        rebuildUI();
    }

    public void setSaveData(SaveGameData data) {
        this.pendingSaveData = data;
        this.currentMode = Mode.SAVE;
        rebuildUI();
    }

    private void rebuildUI() {
        root.clear();
        buildUI();
    }

    /**
     * Builds the user interface for the load/save scene.
     */
    private void buildUI() {
        root.setFillParent(true);
        BitmapFont font = Assets.getFont("ui", 24);
        BitmapFont smallFont = Assets.getFont("ui", 18);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle smallLabelStyle = new Label.LabelStyle(smallFont, Color.LIGHT_GRAY);

        Table mainPanel = new Table();
        mainPanel.setSize(900, 700);
        mainPanel.setPosition((Gdx.graphics.getWidth() - 900) / 2f,
                (Gdx.graphics.getHeight() - 700) / 2f);

        String titleText = currentMode == Mode.SAVE ? "Save Game" : "Load Game";
        Label title = new Label(titleText, labelStyle);
        mainPanel.add(title).colspan(3).padTop(60).padBottom(30);
        mainPanel.row();

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        Drawable tintedBg = bg.tint(new Color(0.2f, 0.2f, 0.2f, 0.3f));

        saveList = new Table();
        saveList.top();
        createSaveSlots(saveList, tintedBg, font, smallLabelStyle);

        scrollPane = new ScrollPane(saveList);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollBarPositions(false, false);

        mainPanel.add(scrollPane).width(750).height(500).pad(10);
        customScrollbar = new UIScrollbar(scrollPane, 12, 500);
        mainPanel.add(customScrollbar.getActor()).width(40).height(500).padLeft(10);
        mainPanel.row();

        UICloseButton backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80);
        backButton.onClick(() -> {
            if (game.transition == null) {
                if (currentMode == Mode.SAVE && game.storyStageScene != null) {
                    game.transition = new Transition(game, this, game.storyStageScene,
                            State.STORY_STAGE, 350);
                } else {
                    game.transition = new Transition(game, this, game.mainMenuScene,
                            State.MAIN_MENU, 350);
                }
            }
        });

        root.addActor(mainPanel);
        root.addActor(backButton.getActor());
        game.stage.addActor(root);

        root.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    backButton.click();
                    return true;
                } else if (keycode == Input.Keys.UP) {
                    if (!isKeyScrollingUp) {
                        isKeyScrollingUp = true;
                        scroll(-KEYBOARD_CLICK_SCROLL);
                    }
                    return true;
                } else if (keycode == Input.Keys.DOWN) {
                    if (!isKeyScrollingDown) {
                        isKeyScrollingDown = true;
                        scroll(KEYBOARD_CLICK_SCROLL);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.UP) {
                    isKeyScrollingUp = false;
                    keyScrollAccumulator = 0f;
                    return true;
                } else if (keycode == Input.Keys.DOWN) {
                    isKeyScrollingDown = false;
                    keyScrollAccumulator = 0f;
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Creates the save/load slots in the UI.
     */
    private void createSaveSlots(Table saveList, Drawable bg, BitmapFont font, Label.LabelStyle smallLabelStyle) {
        Label.LabelStyle slotLabelStyle = new Label.LabelStyle(font, Color.WHITE);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (currentMode == Mode.SAVE) {
            createSaveModeSlots(saveList, bg, font, slotLabelStyle, smallLabelStyle, dateFormat);
        } else {
            createLoadModeSlots(saveList, bg, font, slotLabelStyle, smallLabelStyle, dateFormat);
        }
    }

    /**
     * Creates the save mode slots in the UI.
     */
    private void createSaveModeSlots(Table saveList, Drawable bg, BitmapFont font,
                                    Label.LabelStyle slotLabelStyle, Label.LabelStyle smallLabelStyle,
                                    SimpleDateFormat dateFormat) {
        Table newSaveSlot = new Table();
        newSaveSlot.setBackground(bg);
        Table infoTable = new Table();
        infoTable.left();

        Label newSaveLabel = new Label("Create New Save", slotLabelStyle);
        newSaveLabel.setColor(Color.GREEN);
        infoTable.add(newSaveLabel).left().padLeft(20);

        UITextButton saveButton = new UITextButton("Save", font);
        saveButton.setSize(150, 50);
        saveButton.onClick(() -> {
            if (pendingSaveData != null) {
                createNewSave();
            }
        });

        newSaveSlot.add(infoTable).expandX().left().pad(15);
        newSaveSlot.add(saveButton.button).width(150).height(50).right().padRight(20);

        saveList.add(newSaveSlot).width(750).height(100).padBottom(10);
        saveList.row();

        List<SaveGameManager.SaveSlot> slots = SaveGameManager.getAllSaveSlots();

        for (SaveGameManager.SaveSlot slot : slots) {
            Table saveSlot = new Table();
            saveSlot.setBackground(bg);
            Table slotInfoTable = new Table();
            slotInfoTable.left();

            Label slotNameLabel = new Label(slot.slotName, slotLabelStyle);
            String dateStr = dateFormat.format(new Date(slot.timestamp));
            String infoText = String.format("Stage %d | Score: %d | Lives: %d",
                slot.currentStageId, slot.currentScore, slot.lives);
            Label infoLabel = new Label(infoText, smallLabelStyle);
            Label dateLabel = new Label(dateStr, smallLabelStyle);

            slotInfoTable.add(slotNameLabel).left().padLeft(20);
            slotInfoTable.row();
            slotInfoTable.add(infoLabel).left().padLeft(20).padTop(5);
            slotInfoTable.row();
            slotInfoTable.add(dateLabel).left().padLeft(20).padTop(2);

            UITextButton overwriteButton = new UITextButton("Overwrite", font);
            overwriteButton.setSize(150, 50);
            int slotId = slot.slotId;
            overwriteButton.onClick(() -> {
                if (pendingSaveData != null) {
                    overwriteSave(slotId);
                }
            });

            saveSlot.add(slotInfoTable).expandX().left().pad(15);
            saveSlot.add(overwriteButton.button).width(150).height(50).right().padRight(20);

            saveList.add(saveSlot).width(750).height(120).padBottom(10);
            saveList.row();
        }

        if (slots.isEmpty() && pendingSaveData == null) {
            Label noSavesLabel = new Label("No saved games found", slotLabelStyle);
            noSavesLabel.setColor(Color.GRAY);
            saveList.add(noSavesLabel).center().pad(50);
        }
    }

    /**
     * Creates the load mode slots in the UI.
     */
    private void createLoadModeSlots(Table saveList, Drawable bg, BitmapFont font,
                                    Label.LabelStyle slotLabelStyle, Label.LabelStyle smallLabelStyle,
                                    SimpleDateFormat dateFormat) {
        List<SaveGameManager.SaveSlot> slots = SaveGameManager.getAllSaveSlots();

        if (slots.isEmpty()) {
            Label noSavesLabel = new Label("No saved games found", slotLabelStyle);
            noSavesLabel.setColor(Color.GRAY);
            saveList.add(noSavesLabel).center().pad(50);
            return;
        }

        for (SaveGameManager.SaveSlot slot : slots) {
            Table saveSlot = new Table();
            saveSlot.setBackground(bg);
            Table infoTable = new Table();
            infoTable.left();

            Label slotNameLabel = new Label(slot.slotName, slotLabelStyle);
            String dateStr = dateFormat.format(new Date(slot.timestamp));
            String infoText = String.format("Stage %d | Score: %d | Lives: %d",
                slot.currentStageId, slot.currentScore, slot.lives);
            Label infoLabel = new Label(infoText, smallLabelStyle);
            Label dateLabel = new Label(dateStr, smallLabelStyle);

            infoTable.add(slotNameLabel).left().padLeft(20);
            infoTable.row();
            infoTable.add(infoLabel).left().padLeft(20).padTop(5);
            infoTable.row();
            infoTable.add(dateLabel).left().padLeft(20).padTop(2);

            Table buttonTable = new Table();

            UITextButton loadButton = new UITextButton("Load", font);
            loadButton.setSize(130, 50);
            int slotId = slot.slotId;
            int stageId = slot.currentStageId;
            loadButton.onClick(() -> {
                log.info("Loading save slot {} (Stage {})", slotId, stageId);
                loadGameAndStart(slotId);
            });

            UITextButton deleteButton = new UITextButton("Delete", font);
            deleteButton.setSize(130, 50);
            deleteButton.onClick(() -> {
                log.info("Deleting save slot {}", slotId);
                SaveGameManager.deleteSaveSlot(slotId);
                rebuildUI();
            });

            buttonTable.add(loadButton.button).width(130).height(50).padRight(5);
            buttonTable.add(deleteButton.button).width(130).height(50).padLeft(5);

            saveSlot.add(infoTable).expandX().left().pad(15);
            saveSlot.add(buttonTable).right().padRight(20);

            saveList.add(saveSlot).width(750).height(120).padBottom(10);
            saveList.row();
        }
    }

    /**
     * Creates a new save slot with the pending save data.
     */
    private void createNewSave() {
        if (pendingSaveData == null) {
            log.warn("No pending save data");
            return;
        }

        String slotName = String.format("Save - Stage %d", pendingSaveData.stageId);
        int slotId = SaveGameManager.createSaveSlot(
            slotName,
            pendingSaveData.stageId,
            pendingSaveData.currentScore,
            pendingSaveData.lives,
            pendingSaveData.bricksDestroyed,
            pendingSaveData.gameStateJson,
            pendingSaveData.progressJson
        );

        if (slotId != -1) {
            log.info("Successfully created new save slot {}", slotId);
            rebuildUI();
        } else {
            log.error("Failed to create new save slot");
        }
    }

    /**
     * Overwrites an existing save slot with the pending save data.
     * @param slotId The ID of the save slot to overwrite.
     */
    private void overwriteSave(int slotId) {
        if (pendingSaveData == null) {
            log.warn("No pending save data");
            return;
        }

        SaveGameManager.SaveSlot existingSlot = SaveGameManager.getSaveSlot(slotId);
        if (existingSlot == null) {
            log.warn("Save slot {} not found", slotId);
            return;
        }

        boolean success = SaveGameManager.updateSaveSlot(
            slotId,
            existingSlot.slotName,
            pendingSaveData.stageId,
            pendingSaveData.currentScore,
            pendingSaveData.lives,
            pendingSaveData.bricksDestroyed,
            pendingSaveData.gameStateJson,
            pendingSaveData.progressJson
        );

        if (success) {
            log.info("Successfully overwrote save slot {}", slotId);
            rebuildUI();
        } else {
            log.error("Failed to overwrite save slot {}", slotId);
        }
    }

    /**
     * Loads a game from the specified save slot and starts the corresponding stage.
     * @param slotId The ID of the save slot to load.
     */
    private void loadGameAndStart(int slotId) {
        if (game.transition != null) {
            return;
        }

        SaveGameManager.SaveSlot slot = SaveGameManager.getSaveSlot(slotId);
        if (slot == null) {
            log.error("Save slot {} not found", slotId);
            return;
        }

        int stageId = slot.currentStageId;
        log.info("Loading save slot {} for stage {}", slotId, stageId);

        game.loadingSaveSlotId = slotId;

        switch (stageId) {
            case 1:
                game.recreateScene(game.storyStageScene,
                        () -> new org.vibecoders.moongazer.scenes.story.Stage1(game),
                        scene -> game.storyStageScene = scene);
                game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                break;
            case 2:
                game.recreateScene(game.storyStageScene,
                        () -> new org.vibecoders.moongazer.scenes.story.Stage2(game),
                        scene -> game.storyStageScene = scene);
                game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                break;
            case 3:
                game.recreateScene(game.storyStageScene,
                        () -> new org.vibecoders.moongazer.scenes.story.Stage3(game),
                        scene -> game.storyStageScene = scene);
                game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                break;
            case 4:
                game.recreateScene(game.storyStageScene,
                        () -> new org.vibecoders.moongazer.scenes.story.Stage4(game),
                        scene -> game.storyStageScene = scene);
                game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                break;
            case 5:
                game.recreateScene(game.storyStageScene,
                        () -> new org.vibecoders.moongazer.scenes.story.Stage5(game),
                        scene -> game.storyStageScene = scene);
                game.transition = new Transition(game, this, game.storyStageScene, State.STORY_STAGE, 500);
                break;
            default:
                log.warn("Unknown stage ID: {}", stageId);
                game.loadingSaveSlotId = -1;
                break;
        }
    }

    /**
     * Scrolls the scroll pane by the specified amount.
     * @param amount The amount to scroll.
     */
    private void scroll(float amount) {
        float newScroll = scrollPane.getScrollY() + amount;
        scrollPane.setScrollY(Math.max(0, Math.min(scrollPane.getMaxY(), newScroll)));
    }

    /**
     * Renders the load/save scene.
     */
    @Override
    public void render(SpriteBatch batch) {
        float delta = Gdx.graphics.getDeltaTime();
        customScrollbar.update(delta);

        if (isKeyScrollingUp || isKeyScrollingDown) {
            keyScrollAccumulator += KEYBOARD_SCROLL_SPEED * delta;
            if (keyScrollAccumulator >= 1f) {
                float scrollAmount = (int) keyScrollAccumulator;
                scroll(isKeyScrollingUp ? -scrollAmount : scrollAmount);
                keyScrollAccumulator -= scrollAmount;
            }
        }

        game.mainMenuScene.render(batch);
        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(Assets.getWhiteTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
    }
}
