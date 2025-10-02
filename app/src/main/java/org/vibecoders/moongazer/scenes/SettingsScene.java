package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.Settings;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UITextButton;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;

import java.util.HashMap;

import static com.badlogic.gdx.scenes.scene2d.ui.Slider.*;
import static org.vibecoders.moongazer.Constants.*;

public class SettingsScene extends Scene {
    private boolean isEditingKeybind = false;
    private UITextButton currentEditingButton = null;
    private String currentKeybindAction = "";
    private HashMap<String, UITextButton> keybindButtons = new HashMap<>();
    private Slider musicSlider;

    public SettingsScene(Game game) {
        super(game);
        
        root.setFillParent(true);
        BitmapFont font = Assets.getFont("ui", 24);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        
        // Main panel
        Table mainPanel = new Table();
        mainPanel.setSize(800, 600);
        mainPanel.setPosition((Gdx.graphics.getWidth() - 800) / 2f,
                (Gdx.graphics.getHeight() - 600) / 2f);

        Label title = new Label("Settings", labelStyle);
        mainPanel.add(title).colspan(2).padTop(60).padBottom(40);
        mainPanel.row();


        // slider placeholders
        Texture sliderBgTexture = Assets.getAsset("textures/ui/UI_SliderBg2.png", Texture.class);
        Texture sliderKnobTexture = Assets.getAsset("textures/ui/UI_SliderKnob.png", Texture.class);
        Texture sliderKnobOverTexture = Assets.getAsset("textures/ui/UI_SliderBg.png", Texture.class);

        SliderStyle sliderStyle = new SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(sliderKnobTexture));
        sliderStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(sliderKnobOverTexture));
        sliderStyle.knobAfter = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));

        // Volume settings
        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        var tintedBg = bg.tint(new Color(0.2f, 0.2f, 0.2f, 0.3f));

        String[] volumes = {"Master Volume", "Music Volume", "SFX Volume"};
        for (String volume : volumes) {
            Table row = new Table();
            row.setBackground(tintedBg);
            row.add(new Label(volume, labelStyle)).expandX().left().padLeft(40).pad(15);
            musicSlider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
            musicSlider.setValue(0.5f);
            row.add(musicSlider).width(300).right().padRight(40);
            mainPanel.add(row).width(700).height(60).padBottom(5);
            mainPanel.row();
        }

        // Keybind settings
        Table section = new Table();
        section.setBackground(tintedBg);
        section.add(new Label("Keybinds", labelStyle)).colspan(2).expandX().left()
                .padLeft(20).padTop(15).padBottom(10);
        section.row();

        // Player keybinds
        String[] players = {"Player 1", "Player 2"};
        String[] prefixes = {"p1", "p2"};
        String[] actions = {"_left", "_right"};
        String[] labels = {"    Move Left", "    Move Right"};

        for (int p = 0; p < players.length; p++) {
            section.add(new Label("  " + players[p], labelStyle)).colspan(2).left()
                    .padLeft(60);
            section.row();
            for (int a = 0; a < actions.length; a++) {
                section.add(new Label(labels[a], labelStyle)).left().padLeft(80);
                String action = prefixes[p] + actions[a];
                UITextButton button = new UITextButton(getKeyName(Settings.getKeybind(action)), font);
                button.button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!isEditingKeybind) {
                            isEditingKeybind = true;
                            currentEditingButton = button;
                            currentKeybindAction = action;
                            ((TextButton)(button.button)).setText("Press Key...");
                        }
                    }
                });
                keybindButtons.put(action, button);
                section.add(button.button).width(240).height(60).right().padRight(40);
                section.row();
            }
        }

        mainPanel.add(section).width(700).padBottom(20);
        mainPanel.row();

        // Back button
        UICloseButton backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80);
        backButton.onClick(() -> {
            if (game.transition == null && !isEditingKeybind) {
                game.transition = new Transition(game, this, game.mainMenuScene,
                        State.MAIN_MENU, 350);
            }
        });

        root.addActor(mainPanel);
        root.addActor(backButton.getActor());
        game.stage.addActor(root);

        // Key listener
        root.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (isEditingKeybind && currentEditingButton != null) {
                    if (keycode == Input.Keys.ESCAPE) {
                        if (currentEditingButton != null) {
                            ((TextButton)(currentEditingButton.button)).setText(getKeyName(Settings.getKeybind(currentKeybindAction)));
                        }
                        isEditingKeybind = false;
                        currentEditingButton = null;
                        currentKeybindAction = "";
                        return true;
                    }
                    if (isKeybindAlreadyUsed(keycode, currentKeybindAction)) {
                        ((TextButton)(currentEditingButton.button)).setText("Key in use!");
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                                Gdx.app.postRunnable(() -> {
                                    if (isEditingKeybind && currentEditingButton != null) {
                                        ((TextButton)(currentEditingButton.button)).setText("Press Key...");
                                    }
                                });
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                        return true;
                    }
                    Settings.keybinds.put(currentKeybindAction, keycode);
                    ((TextButton)(currentEditingButton.button)).setText(getKeyName(keycode));
                    isEditingKeybind = false;
                    currentEditingButton = null;
                    currentKeybindAction = "";
                    return true;
                } else if (keycode == Input.Keys.ESCAPE) {
                    backButton.click();
                    return true;
                }
                return false;
            }
        });
    }

    private String getKeyName(int keycode) {
        String keyName = Input.Keys.toString(keycode);
        return keyName != null ? keyName.toUpperCase() : "Unknown";
    }

    private boolean isKeybindAlreadyUsed(int keycode, String currentAction) {
        return Settings.keybinds.entrySet().stream()
                .anyMatch(entry -> !entry.getKey().equals(currentAction) &&
                        entry.getValue().equals(keycode));
    }

    @Override
    public void render(SpriteBatch batch) {
        game.mainMenuScene.render(batch);
        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(Assets.getWhiteTexture(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.setColor(Color.WHITE);
    }
}
