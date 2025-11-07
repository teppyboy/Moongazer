package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.Settings;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;

import java.util.HashMap;
import java.util.Map;

import static org.vibecoders.moongazer.Constants.*;

/**
 * Settings overlay for pause menu - matches MainMenu/SettingsScene style
 */
public class PauseMenuSettings {
    private static final Logger log = LoggerFactory.getLogger(PauseMenuSettings.class);

    private boolean isOpen = false;
    private Stage settingsStage;
    private Table root;
    private BitmapFont titleFont;
    private BitmapFont labelFont;
    private UISlider masterVolSlider;
    private UISlider musicSlider;
    private UISlider sfxSlider;
    private UITextButton backButton;
    private HashMap<Integer, Long> currentKeyDown = new HashMap<>();

    private Runnable onClose;

    public PauseMenuSettings() {
        initUI();
    }

    private void initUI() {
        titleFont = Assets.getFont("ui", 40);
        labelFont = Assets.getFont("ui", 24);

        settingsStage = new Stage();
        root = new Table();
        root.setFillParent(true);
        settingsStage.addActor(root);

        Table mainPanel = new Table();
        mainPanel.setSize(800, 600);
        mainPanel.setPosition((WINDOW_WIDTH - 800) / 2f, (WINDOW_HEIGHT - 600) / 2f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(labelFont, Color.WHITE);
        Label title = new Label("SETTINGS", labelStyle);
        mainPanel.add(title).colspan(2).padTop(60).padBottom(40);
        mainPanel.row();

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        var tintedBg = bg.tint(new Color(0.2f, 0.2f, 0.2f, 0.3f));

        String[] volumes = { "Master Volume", "Music Volume", "SFX Volume" };
        UISlider[] sliders = new UISlider[3];

        for (int i = 0; i < volumes.length; i++) {
            Table row = new Table();
            row.setBackground(tintedBg);
            row.add(new Label(volumes[i], labelStyle)).expandX().left().padLeft(40).pad(15);

            if (i == 0) {
                masterVolSlider = new UISlider();
                masterVolSlider.setValue(Settings.getMasterVolume());
                masterVolSlider.onChanged(() -> {
                    Settings.setMasterVolume(masterVolSlider.getValue());
                    Audio.musicSetVolume();
                });
                sliders[i] = masterVolSlider;
                row.add(masterVolSlider.slider).width(300).right().padRight(40);
            } else if (i == 1) {
                musicSlider = new UISlider();
                musicSlider.setValue(Settings.getMusicVolume());
                musicSlider.onChanged(() -> {
                    Settings.setMusicVolume(musicSlider.getValue());
                    Audio.musicSetVolume();
                });
                sliders[i] = musicSlider;
                row.add(musicSlider.slider).width(300).right().padRight(40);
            } else {
                sfxSlider = new UISlider();
                sfxSlider.setValue(Settings.getSfxVolume());
                sfxSlider.onChanged(() -> {
                    Settings.setSfxVolume(sfxSlider.getValue());
                });
                sliders[i] = sfxSlider;
                row.add(sfxSlider.slider).width(300).right().padRight(40);
            }

            mainPanel.add(row).width(700).height(60).padBottom(5);
            mainPanel.row();
        }

        backButton = new UITextButton("Back", labelFont);
        backButton.setSize(300, 70);
        backButton.onClick(() -> {
            log.debug("Back from settings");
            Audio.playSfxReturn();
            close();
        });

        mainPanel.add(backButton.getActor()).width(300).height(70).padTop(40);
        mainPanel.row();

        root.addActor(mainPanel);

        initKeyboardHandling();
    }

    private void initKeyboardHandling() {
        root.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                handleKeyDown(keycode);
                currentKeyDown.put(keycode, TimeUtils.millis());
                return true;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                currentKeyDown.remove(keycode);
                return true;
            }
        });
    }

    private void handleKeyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            Audio.playSfxReturn();
            close();
        }
    }

    public void open() {
        if (!isOpen) {
            isOpen = true;
            Gdx.input.setInputProcessor(settingsStage);
            log.info("Settings overlay opened");
        }
    }

    public void close() {
        if (isOpen) {
            isOpen = false;
            currentKeyDown.clear();
            if (onClose != null) {
                onClose.run();
            }
            if (settingsStage != null && settingsStage.getRoot() != null) {
                settingsStage.getRoot().getColor().a = 1f;
            }
            log.info("Settings overlay closed");
        }
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void render(SpriteBatch batch, float parentAlpha) {
        if (!isOpen) return;

        float alpha = MathUtils.clamp(parentAlpha, 0f, 1f);
        if (alpha <= 0f) {
            return;
        }

        for (Map.Entry<Integer, Long> entry : currentKeyDown.entrySet()) {
            Integer keyCode = entry.getKey();
            Long timeStamp = entry.getValue();
            if (TimeUtils.millis() - timeStamp > 150) {
                handleKeyDown(keyCode);
                currentKeyDown.put(keyCode, TimeUtils.millis());
            }
        }

        batch.setColor(0, 0, 0, 0.5f * alpha);
        batch.draw(Assets.getWhiteTexture(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.setColor(Color.WHITE);

        batch.end();

        if (settingsStage.getRoot() != null) {
            settingsStage.getRoot().getColor().a = alpha;
        }
        settingsStage.act(Gdx.graphics.getDeltaTime());
        settingsStage.draw();

        batch.begin();
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void dispose() {
        if (settingsStage != null) {
            settingsStage.dispose();
        }
    }
}
