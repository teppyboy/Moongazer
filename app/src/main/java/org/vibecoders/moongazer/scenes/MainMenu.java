package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

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
        root.addActor(textLabel);
        game.stage.addActor(root);
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
