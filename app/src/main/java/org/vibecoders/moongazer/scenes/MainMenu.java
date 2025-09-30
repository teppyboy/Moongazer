package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.MenuButton;
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
        MenuButton playButton = new MenuButton("Play", font);
        MenuButton loadButton = new MenuButton("Load", font);
        MenuButton settingsButton = new MenuButton("Settings", font);
        MenuButton exitButton = new MenuButton("Exit", font);

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

        playButton.addEventListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                log.debug("Play clicked");
            }
        });

        loadButton.addEventListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                log.debug("Load clicked");
            }
        });

        settingsButton.addEventListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                log.debug("Settings clicked");
            }
        });

        exitButton.addEventListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                log.debug("Exit clicked");
            }
        });

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