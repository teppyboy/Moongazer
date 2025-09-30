package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UIImageButton;
import org.vibecoders.moongazer.ui.UITextButton;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MainMenu extends Scene {
    private Texture backgroundTexture;
    private Texture titleTexture;
    private float titleY;
    private float titleX;
    private float titleWidth;
    private float titleHeight;

    public MainMenu(Game game) {
        super(game);
        backgroundTexture = Assets.getAsset("textures/main_menu/background.png", Texture.class);
        titleTexture = Assets.getAsset("textures/main_menu/title.png", Texture.class);
        // Scale and position title
        float targetTitleWidth = 500f;
        float originalWidth = titleTexture.getWidth();
        float originalHeight = titleTexture.getHeight();
        float scale = targetTitleWidth / originalWidth;
        titleWidth = originalWidth * scale;
        titleHeight = originalHeight * scale;
        titleX = (WINDOW_WIDTH - titleWidth) / 2f;
        titleY = WINDOW_HEIGHT / 2f - titleHeight / 8f;

        // Buttons
        var font = Assets.getFont("ui", 24);
        UITextButton playButton = new UITextButton("Play", font);
        UITextButton loadButton = new UITextButton("Load", font);
        UITextButton settingsButton = new UITextButton("Settings", font);
        UITextButton exitButton = new UITextButton("Exit", font);
      
        int buttonWidth = 300;
        int buttonHeight = 80;

        playButton.setSize(buttonWidth, buttonHeight);
        loadButton.setSize(buttonWidth, buttonHeight);
        settingsButton.setSize(buttonWidth, buttonHeight);
        exitButton.setSize(buttonWidth, buttonHeight);

        int centerX = WINDOW_WIDTH / 2 - buttonWidth / 2;
        int startY = WINDOW_HEIGHT / 2 - buttonHeight / 2;
        int buttonSpacing = 65;

        playButton.setPosition(centerX, startY);
        loadButton.setPosition(centerX, startY - buttonSpacing);
        settingsButton.setPosition(centerX, startY - buttonSpacing * 2);
        exitButton.setPosition(centerX, startY - buttonSpacing * 3);

        playButton.onClick(() -> log.debug("Play clicked"));
        loadButton.onClick(() -> log.debug("Load clicked"));
        settingsButton.onClick(() -> {
            log.debug("Settings clicked");
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.settingsScene, State.SETTINGS, 350);
            }
        });
        exitButton.onClick(() -> {
            log.debug("Exit clicked");
            Gdx.app.exit();
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
        batch.draw(titleTexture, titleX, titleY, titleWidth, titleHeight);
    }
}