package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.Settings;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UIImageButton;

import java.util.HashMap;

import static org.vibecoders.moongazer.Constants.*;

public class SettingsScene extends Scene {

    private Table mainPanel;
    private boolean isEditingKeybind = false;
    private TextButton currentEditingButton = null;
    private String currentKeybindAction = "";

    private HashMap<String, TextButton> keybindButtons = new HashMap<>();

    public SettingsScene(Game game) {
        super(game);
        initUI();
    }

    private void initUI() {
        root.setFillParent(true);
        root.clear();

        BitmapFont font = Assets.getFont("ui", 24);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        mainPanel = new Table();
        mainPanel.setSize(800, 600);
        mainPanel.setPosition(
                (Gdx.graphics.getWidth() - 800) / 2f,
                (Gdx.graphics.getHeight() - 600) / 2f);

        TextureRegionDrawable rowBgDrawable = new TextureRegionDrawable(Assets.getWhiteTexture());
        Color bgColor = new Color(0.2f, 0.2f, 0.2f, 0.3f);
        Drawable tintedBg = rowBgDrawable.tint(bgColor);

        Label titleLabel = new Label("Settings", new Label.LabelStyle(font, Color.WHITE));
        mainPanel.add(titleLabel).colspan(2).padTop(60).padBottom(40);
        mainPanel.row();

        // TODO: Volume Sliders
        String[] volumeLabels = { "Master Volume", "Music Volume", "SFX Volume" };
        for (String volumeLabel : volumeLabels) {
            Table volumeRow = new Table();
            volumeRow.setBackground(tintedBg);

            volumeRow.add(new Label(volumeLabel, labelStyle)).expandX().left().padLeft(40).padTop(15).padBottom(15);
            volumeRow.add(new Label("[Slider Placeholder]", labelStyle)).right().padRight(40).padTop(15).padBottom(15);

            mainPanel.add(volumeRow).width(700).height(60).padBottom(5);
            mainPanel.row();
        }

        Table keybindsSection = new Table();
        keybindsSection.setBackground(tintedBg);

        keybindsSection.add(new Label("Keybinds", labelStyle)).colspan(2).expandX().left().padLeft(40).padTop(15)
                .padBottom(10);
        keybindsSection.row();

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.BLACK;
        TextureRegionDrawable keybindBgDrawable = new TextureRegionDrawable(Assets.getWhiteTexture());
        Color buttonColor = new Color(0.7f, 0.7f, 0.7f, 1.0f);
        textButtonStyle.up = keybindBgDrawable.tint(buttonColor);

        TextButton.TextButtonStyle editingButtonStyle = new TextButton.TextButtonStyle();
        editingButtonStyle.font = font;
        editingButtonStyle.fontColor = Color.WHITE;
        Color editingColor = new Color(0.3f, 0.3f, 0.8f, 1.0f);
        editingButtonStyle.up = keybindBgDrawable.tint(editingColor);

        keybindsSection.add(new Label("  Player 1", labelStyle)).colspan(2).left().padLeft(60).padBottom(8);
        keybindsSection.row();

        keybindsSection.add(new Label("    Move Left", labelStyle)).left().padLeft(80);
        TextButton player1LeftButton = new TextButton(getKeyName(Settings.keybinds.get("p1_left")), textButtonStyle);
        setupKeybindButton(player1LeftButton, "p1_left", textButtonStyle, editingButtonStyle);
        keybindButtons.put("p1_left", player1LeftButton);
        keybindsSection.add(player1LeftButton).width(150).right().padRight(40).padBottom(5);
        keybindsSection.row();

        keybindsSection.add(new Label("    Move Right", labelStyle)).left().padLeft(80);
        TextButton player1RightButton = new TextButton(getKeyName(Settings.keybinds.get("p1_right")), textButtonStyle);
        setupKeybindButton(player1RightButton, "p1_right", textButtonStyle, editingButtonStyle);
        keybindButtons.put("p1_right", player1RightButton);
        keybindsSection.add(player1RightButton).width(150).right().padRight(40).padBottom(10);
        keybindsSection.row();

        keybindsSection.add(new Label("  Player 2", labelStyle)).colspan(2).left().padLeft(60).padTop(5).padBottom(8);
        keybindsSection.row();

        keybindsSection.add(new Label("    Move Left", labelStyle)).left().padLeft(80);
        TextButton player2LeftButton = new TextButton(getKeyName(Settings.keybinds.get("p2_left")), textButtonStyle);
        setupKeybindButton(player2LeftButton, "p2_left", textButtonStyle, editingButtonStyle);
        keybindButtons.put("p2_left", player2LeftButton);
        keybindsSection.add(player2LeftButton).width(150).right().padRight(40).padBottom(5);
        keybindsSection.row();

        keybindsSection.add(new Label("    Move Right", labelStyle)).left().padLeft(80);
        TextButton player2RightButton = new TextButton(getKeyName(Settings.keybinds.get("p2_right")), textButtonStyle);
        setupKeybindButton(player2RightButton, "p2_right", textButtonStyle, editingButtonStyle);
        keybindButtons.put("p2_right", player2RightButton);
        keybindsSection.add(player2RightButton).width(150).right().padRight(40).padBottom(15);
        keybindsSection.row();

        mainPanel.add(keybindsSection).width(700).padBottom(20);
        mainPanel.row();

        // Close button
        UIImageButton backButton = new UIImageButton("textures/ui/UI_Gcg_Icon_Close.png");
        backButton.setSize(40, 40);
        backButton.setPosition(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80);
        backButton.getActor().setName("backButton");
        backButton.onClick(() -> {
            if (game.transition == null && !isEditingKeybind) {
                game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 350);
            }
        });

        root.addActor(mainPanel);
        root.addActor(backButton.getActor());
        game.stage.addActor(root);

        // Listener for key input when editing keybind
        game.stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (isEditingKeybind) {
                    if (currentEditingButton != null) {
                        // ESC not allowed, cancel editing
                        if (keycode == Input.Keys.ESCAPE) {
                            cancelKeybindEdit();
                            return true;
                        }

                        // Check if key is already used
                        if (isKeybindAlreadyUsed(keycode, currentKeybindAction)) {
                            // Error_msg
                            currentEditingButton.setText("Key in use!");
                            // Delay
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                    Gdx.app.postRunnable(() -> {
                                        if (isEditingKeybind && currentEditingButton != null) {
                                            currentEditingButton.setText("Press Key...");
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }).start();
                            return true;
                        }

                        // Update keybind
                        Settings.keybinds.put(currentKeybindAction, keycode);
                        currentEditingButton.setText(getKeyName(keycode));

                        finishKeybindEdit();
                        return true;
                    }
                } else {
                    if (keycode == Input.Keys.ESCAPE) {
                        game.transition = new Transition(game, SettingsScene.this, game.mainMenuScene,
                                State.MAIN_MENU, 350);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setupKeybindButton(TextButton button, String action, TextButton.TextButtonStyle normalStyle,
            TextButton.TextButtonStyle editingStyle) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isEditingKeybind) {
                    startKeybindEdit(button, action, editingStyle);
                }
            }
        });
    }

    private void startKeybindEdit(TextButton button, String action, TextButton.TextButtonStyle editingStyle) {
        isEditingKeybind = true;
        currentEditingButton = button;
        currentKeybindAction = action;

        button.setStyle(editingStyle);
        button.setText("Press Key...");
    }

    private void finishKeybindEdit() {
        if (currentEditingButton != null) {
            // Return to normal
            TextButton.TextButtonStyle normalStyle = new TextButton.TextButtonStyle();
            BitmapFont font = Assets.getFont("ui", 24);
            normalStyle.font = font;
            normalStyle.fontColor = Color.BLACK;
            TextureRegionDrawable keybindBgDrawable = new TextureRegionDrawable(Assets.getWhiteTexture());
            Color buttonColor = new Color(0.7f, 0.7f, 0.7f, 1.0f);
            normalStyle.up = keybindBgDrawable.tint(buttonColor);

            currentEditingButton.setStyle(normalStyle);
        }

        isEditingKeybind = false;
        currentEditingButton = null;
        currentKeybindAction = "";
    }

    private void cancelKeybindEdit() {
        if (currentEditingButton != null) {
            // Reset text
            currentEditingButton.setText(getKeyName(Settings.getKeybind(currentKeybindAction)));
        }
        finishKeybindEdit();
    }

    private String getKeyName(int keycode) {
        String keyName = Input.Keys.toString(keycode);
        return keyName != null ? keyName.toUpperCase() : "Unknown";
    }

    private boolean isKeybindAlreadyUsed(int keycode, String currentAction) {
        for (java.util.Map.Entry<String, Integer> entry : Settings.keybinds.entrySet()) {
            if (!entry.getKey().equals(currentAction) && entry.getValue().equals(keycode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(SpriteBatch batch) {
        game.mainMenuScene.render(batch);
        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(Assets.getWhiteTexture(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.setColor(Color.WHITE);
    }
}
