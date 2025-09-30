package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.vibecoders.moongazer.buttons.MenuButton;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.Game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * Main menu scene.
 */
public class MainMenu extends Scene {
    Label textLabel;

    public MainMenu(Game game) {
        super(game);
        var font = Assets.getFont("ui", 24);
        textLabel = new Label("Moongazer", new LabelStyle(font, Color.BLACK));
        textLabel.setPosition(WINDOW_WIDTH / 2f - textLabel.getWidth() / 2f, WINDOW_HEIGHT / 2f - textLabel.getHeight() / 2f);

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

        playButton.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Play clicked");
            }
        });

        loadButton.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Load clicked");
            }
        });

        settingsButton.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Settings clicked");
            }
        });

        exitButton.getActor().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Exit clicked");
            }
        });

        game.root.addActor(textLabel);
        game.root.addActor(playButton.getActor());
        game.root.addActor(loadButton.getActor());
        game.root.addActor(settingsButton.getActor());
        game.root.addActor(exitButton.getActor());
    }
    /**
     * Renders the main menu scene.
     * @param batch The SpriteBatch to draw with.
     */
    @Override
    public void render(SpriteBatch batch) {
        batch.draw(TEXTURE_WHITE, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        // Unneeded as using Scene2D Stage to render the label
        // textLabel.draw(batch, 1.0f);
    }
}
