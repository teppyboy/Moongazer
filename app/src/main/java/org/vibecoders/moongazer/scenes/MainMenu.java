package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.Game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class MainMenu extends Scene {
    private Label newGameLabel;
    private Label loadGameLabel;
    private Label settingsLabel;
    private Label quitGameLabel;

    private Texture backgroundTexture;
    private Texture titleTexture;

    public MainMenu(Game game) {
        super(game);
        backgroundTexture = Assets.getAsset("textures/main_menu/background.png", Texture.class);
        titleTexture = Assets.getAsset("textures/main_menu/title.png", Texture.class);
        initializeLabels();
    }

    private void initializeLabels() {
        var menuFont = Assets.getFont("ui", 24);

        newGameLabel = new Label("New Game", new LabelStyle(menuFont, Color.BLACK));
        loadGameLabel = new Label("Load Game", new LabelStyle(menuFont, Color.BLACK));
        settingsLabel = new Label("Settings", new LabelStyle(menuFont, Color.BLACK));
        quitGameLabel = new Label("Quit Game", new LabelStyle(menuFont, Color.BLACK));

        float centerX = WINDOW_WIDTH / 2f;
        float startY = WINDOW_HEIGHT * 0.6f;
        float menuSpacing = 60f;

        newGameLabel.setPosition(centerX - newGameLabel.getWidth() / 2f, startY);
        loadGameLabel.setPosition(centerX - loadGameLabel.getWidth() / 2f, startY - menuSpacing);
        settingsLabel.setPosition(centerX - settingsLabel.getWidth() / 2f, startY - menuSpacing * 2);
        quitGameLabel.setPosition(centerX - quitGameLabel.getWidth() / 2f, startY - menuSpacing * 3);

        root.addActor(newGameLabel);
        root.addActor(loadGameLabel);
        root.addActor(settingsLabel);
        root.addActor(quitGameLabel);
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

        newGameLabel.draw(batch, 1.0f);
        loadGameLabel.draw(batch, 1.0f);
        settingsLabel.draw(batch, 1.0f);
        quitGameLabel.draw(batch, 1.0f);
    }
}