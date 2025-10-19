package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UIImageButton;

import java.awt.*;

import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;
import static org.vibecoders.moongazer.Constants.WINDOW_WIDTH;

public class SelectionScene extends Scene {
    private UICloseButton backButton;
    private UIImageButton storyModeButton;
    private UIImageButton endlessModeButton;

    SelectionScene(Game game) {
        super(game);

        Texture background = Assets.getAsset("textures/mode_selection/Bg3.png", Texture.class);
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(background));
        root.setBackground(backgroundDrawable);
        root.setFillParent(true);

        storyModeButton = new UIImageButton("textures/ui/StoryMode.png");
        endlessModeButton = new UIImageButton("textures/ui/EndlessMode.png");
        storyModeButton.setSize(500, 500);
        storyModeButton.setPosition(WINDOW_WIDTH / 2 - 500 - 100, (WINDOW_HEIGHT - 500) / 2);
        endlessModeButton.setSize(500, 500);
        endlessModeButton.setPosition(WINDOW_WIDTH / 2 + 100, (WINDOW_HEIGHT - 500) / 2);
        root.addActor(storyModeButton.getActor());
        root.addActor(endlessModeButton.getActor());

        storyModeButton.onClick(() -> {
            log.debug("Story mode clicked");
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.storyModeScene, State.STORY_MODE, 500);
            }
        });

        backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(WINDOW_WIDTH - 40, WINDOW_HEIGHT - 40);
        root.addActor(backButton.getActor());
        backButton.onClick(() -> {
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.mainMenuScene,
                        State.MAIN_MENU, 350);
            }
        });

        game.stage.addActor(root);
    }

    @Override
    public void render(SpriteBatch batch) {
    }
}
