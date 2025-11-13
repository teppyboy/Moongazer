package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.scenes.arkanoid.ArkanoidEndless;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UIImageButton;

import static org.vibecoders.moongazer.Constants.PARALLAX_STRENGTH;
import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;
import static org.vibecoders.moongazer.Constants.WINDOW_WIDTH;

public class SelectionScene extends Scene {
    private UICloseButton backButton;
    private UIImageButton storyModeButton;
    private UIImageButton endlessModeButton;
    private Texture backgroundTexture;

    /**
     * Scene where the player selects between Story Mode and Endless Mode.
     * @param game The main game instance.
     */
    SelectionScene(Game game) {
        super(game);

        backgroundTexture = Assets.getAsset("textures/ui/GameSelectionBackground.png", Texture.class);
        root.setFillParent(true);

        storyModeButton = new UIImageButton("textures/ui/StoryMode.png");
        endlessModeButton = new UIImageButton("textures/ui/EndlessMode.png");
        storyModeButton.setSize(500, 500);
        storyModeButton.setPosition(WINDOW_WIDTH / 2 + 100, (WINDOW_HEIGHT - 500) / 2);
        endlessModeButton.setSize(500, 500);
        endlessModeButton.setPosition(WINDOW_WIDTH / 2 - 500 - 100, (WINDOW_HEIGHT - 500) / 2);
        root.addActor(storyModeButton.getActor());
        root.addActor(endlessModeButton.getActor());

        storyModeButton.onClick(() -> {
            log.debug("Story mode clicked");
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.storyModeScene, State.STORY_MODE, 500);
            }
        });

        endlessModeButton.onClick(() -> {
            log.debug("Endless mode clicked");
            if (game.transition == null) {
                game.recreateScene(game.endlessModeScene, () -> new ArkanoidEndless(game), scene -> game.endlessModeScene = scene);
                game.transition = new Transition(game, this, game.endlessModeScene, State.ENDLESS_MODE, 500);
            }
        });

        backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(WINDOW_WIDTH - 80, WINDOW_HEIGHT - 80);
        root.addActor(backButton.getActor());
        backButton.onClick(() -> {
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.mainMenuScene,
                        State.MAIN_MENU, 350);
            }
        });
        root.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    backButton.click();
                    return true;
                }
                return false;
            }
        });
        game.stage.addActor(root);
    }

    @Override
    public void render(SpriteBatch batch) {
        int mouseX = com.badlogic.gdx.Gdx.input.getX();
        int mouseY = WINDOW_HEIGHT - com.badlogic.gdx.Gdx.input.getY();
        float offsetX = ((mouseX - WINDOW_WIDTH / 2f) / WINDOW_WIDTH) * PARALLAX_STRENGTH;
        float offsetY = ((mouseY - WINDOW_HEIGHT / 2f) / WINDOW_HEIGHT) * PARALLAX_STRENGTH;
        float zoomScale = 1.05f;
        float bgWidth = WINDOW_WIDTH * zoomScale;
        float bgHeight = WINDOW_HEIGHT * zoomScale;
        float bgX = (WINDOW_WIDTH - bgWidth) / 2f + offsetX;
        float bgY = (WINDOW_HEIGHT - bgHeight) / 2f + offsetY;
        batch.draw(backgroundTexture, bgX, bgY, bgWidth, bgHeight);
    }
}
