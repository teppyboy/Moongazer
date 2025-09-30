package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UIImageButton;
import org.vibecoders.moongazer.ui.UITextButton;
import org.vibecoders.moongazer.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenu extends Scene {
    private Texture backgroundTexture;
    private Texture titleTexture;

    public MainMenu(Game game) {
        super(game);
        backgroundTexture = Assets.getAsset("textures/main_menu/background.png", Texture.class);
        titleTexture = Assets.getAsset("textures/main_menu/title.png", Texture.class);

        var font = Assets.getFont("ui", 24);
        UITextButton playButton = new UITextButton("Play", font);
        UITextButton loadButton = new UITextButton("Load", font);
        UITextButton settingsButton = new UITextButton("Settings", font);
        UITextButton exitButton = new UITextButton("Exit", font);

        UIImageButton settingButton = UIImageButton.settingButton();
        UIImageButton exitImgButton = UIImageButton.exitButton();
        UIImageButton soundButton = UIImageButton.soundButton();
        UIImageButton closeButton = UIImageButton.closeButton();
        settingButton.setSize(50, 50);
        exitImgButton.setSize(50, 50);
        soundButton.setSize(50, 50);
        closeButton.setSize(50, 50);
        settingButton.setPosition(20, WINDOW_HEIGHT - 70);
        exitImgButton.setPosition(WINDOW_WIDTH - 70, WINDOW_HEIGHT - 70);
        soundButton.setPosition(20, 20);
        closeButton.setPosition(WINDOW_WIDTH - 70, 20);

        int buttonWidth = 220;
        int buttonHeight = 65;
        playButton.setSize(buttonWidth, buttonHeight);
        loadButton.setSize(buttonWidth, buttonHeight);
        settingsButton.setSize(buttonWidth, buttonHeight);
        exitButton.setSize(buttonWidth, buttonHeight);

        int centerX = WINDOW_WIDTH / 2 - buttonWidth / 2;
        int startY = WINDOW_HEIGHT / 2 - buttonHeight / 2;
        int buttonSpacing = 50;

        playButton.setPosition(centerX, startY);
        loadButton.setPosition(centerX, startY - buttonSpacing);
        settingsButton.setPosition(centerX, startY - buttonSpacing * 2);
        exitButton.setPosition(centerX, startY - buttonSpacing * 3);

        playButton.onClick(() -> log.debug("Play clicked"));
        loadButton.onClick(() -> log.debug("Load clicked"));
        settingsButton.onClick(() -> log.debug("Settings clicked"));
        exitButton.onClick(() -> log.debug("Exit clicked"));

        root.addActor(settingButton.getActor());
        root.addActor(exitImgButton.getActor());
        root.addActor(soundButton.getActor());
        root.addActor(closeButton.getActor());
        root.addActor(playButton.getActor());
        root.addActor(loadButton.getActor());
        root.addActor(settingsButton.getActor());
        root.addActor(exitButton.getActor());
        game.stage.addActor(root);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(backgroundTexture, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        float targetTitleWidth = 300f;
        float originalWidth = titleTexture.getWidth();
        float originalHeight = titleTexture.getHeight();
        float scale = targetTitleWidth / originalWidth;
        float titleWidth = originalWidth * scale;
        float titleHeight = originalHeight * scale;
        float titleX = (WINDOW_WIDTH - titleWidth) / 2f;
        float titleY = WINDOW_HEIGHT * 0.58f;
        batch.draw(titleTexture, titleX, titleY, titleWidth, titleHeight);
    }
}