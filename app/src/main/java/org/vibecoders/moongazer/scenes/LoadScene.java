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
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UITextButton;
import org.vibecoders.moongazer.ui.UIScrollbar;

public class LoadScene extends Scene {
    private UIScrollbar customScrollbar;
    private ScrollPane scrollPane;
    private static final float KEYBOARD_SCROLL_SPEED = 500f;
    private static final float KEYBOARD_CLICK_SCROLL = 50f;
    private boolean isKeyScrollingUp = false;
    private boolean isKeyScrollingDown = false;
    private float keyScrollAccumulator = 0f;

    public LoadScene(Game game) {
        super(game);

        root.setFillParent(true);
        BitmapFont font = Assets.getFont("ui", 24);
        BitmapFont smallFont = Assets.getFont("ui", 18);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle smallLabelStyle = new Label.LabelStyle(smallFont, Color.LIGHT_GRAY);

        // Main panel
        Table mainPanel = new Table();
        mainPanel.setSize(900, 700);
        mainPanel.setPosition((Gdx.graphics.getWidth() - 900) / 2f,
                (Gdx.graphics.getHeight() - 700) / 2f);

        Label title = new Label("Load Game", labelStyle);
        mainPanel.add(title).colspan(3).padTop(60).padBottom(30);
        mainPanel.row();

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        Drawable tintedBg = bg.tint(new Color(0.2f, 0.2f, 0.2f, 0.3f));

        Table saveList = new Table();
        saveList.top();

        // TODO: Load actual save files
        // For now, display placeholder save slots
        createSaveSlots(saveList, tintedBg, font, smallLabelStyle);

        scrollPane = new ScrollPane(saveList);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollBarPositions(false, false);

        // Add ScrollPane and custom scrollbar side by side
        mainPanel.add(scrollPane).width(750).height(500).pad(10);

        // Create custom scrollbar
        customScrollbar = new UIScrollbar(scrollPane, 12, 500);
        mainPanel.add(customScrollbar.getActor()).width(40).height(500).padLeft(10);

        mainPanel.row();

        // Back button
        UICloseButton backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80);
        backButton.onClick(() -> {
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.mainMenuScene,
                        State.MAIN_MENU, 350);
            }
        });

        root.addActor(mainPanel);
        root.addActor(backButton.getActor());
        game.stage.addActor(root);

        // ESC and Arrow keys listener
        root.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    backButton.click();
                    return true;
                } else if (keycode == Input.Keys.UP) {
                    if (!isKeyScrollingUp) {
                        isKeyScrollingUp = true;
                        scrollUp(KEYBOARD_CLICK_SCROLL); // Immediate scroll on first press
                    }
                    return true;
                } else if (keycode == Input.Keys.DOWN) {
                    if (!isKeyScrollingDown) {
                        isKeyScrollingDown = true;
                        scrollDown(KEYBOARD_CLICK_SCROLL); // Immediate scroll on first press
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

    private void createSaveSlots(Table saveList, Drawable bg, BitmapFont font, Label.LabelStyle smallLabelStyle) {
        //Placeholder
        for (int i = 1; i <= 100; i++) {
            Table saveSlot = new Table();
            saveSlot.setBackground(bg);

            Table infoTable = new Table();
            infoTable.left();

            Label slotLabel = new Label("Save Slot " + i, new Label.LabelStyle(font, Color.WHITE));
            Label dateLabel = new Label("Empty Slot", smallLabelStyle);

            infoTable.add(slotLabel).left().padLeft(20);
            infoTable.row();
            infoTable.add(dateLabel).left().padLeft(20).padTop(5);

            UITextButton loadButton = new UITextButton("Load", font);
            loadButton.setSize(150, 50);
            // TODO: Implement actual load functionality
            int slotNumber = i;
            loadButton.onClick(() -> {
                log.debug("Loading save slot " + slotNumber);
                // Future: Load game from save file
            });

            saveSlot.add(infoTable).expandX().left().pad(15);
            saveSlot.add(loadButton.button).width(150).height(50).right().padRight(20);

            saveList.add(saveSlot).width(750).height(100).padBottom(10);
            saveList.row();
        }
    }

    private void scrollUp(float amount) {
        float currentScroll = scrollPane.getScrollY();
        float newScroll = Math.max(0, currentScroll - amount);
        scrollPane.setScrollY(newScroll);
    }

    private void scrollDown(float amount) {
        float currentScroll = scrollPane.getScrollY();
        float maxScroll = scrollPane.getMaxY();
        float newScroll = Math.min(maxScroll, currentScroll + amount);
        scrollPane.setScrollY(newScroll);
    }

    @Override
    public void render(SpriteBatch batch) {
        float delta = com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        // Update scrollbar for continuous scrolling when holding buttons
        if (customScrollbar != null) {
            customScrollbar.update(delta);
        }

        // Handle continuous keyboard scrolling when holding arrow keys
        if (isKeyScrollingUp) {
            keyScrollAccumulator += KEYBOARD_SCROLL_SPEED * delta;
            if (keyScrollAccumulator >= 1f) {
                float scrollAmount = (int) keyScrollAccumulator;
                scrollUp(scrollAmount);
                keyScrollAccumulator -= scrollAmount;
            }
        } else if (isKeyScrollingDown) {
            keyScrollAccumulator += KEYBOARD_SCROLL_SPEED * delta;
            if (keyScrollAccumulator >= 1f) {
                float scrollAmount = (int) keyScrollAccumulator;
                scrollDown(scrollAmount);
                keyScrollAccumulator -= scrollAmount;
            }
        }

        game.mainMenuScene.render(batch);
        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(Assets.getWhiteTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
