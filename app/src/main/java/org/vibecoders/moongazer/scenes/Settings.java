package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static org.vibecoders.moongazer.Constants.*;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UIImageButton;

public class Settings extends Scene {
    public Settings(Game game) {
        super(game);
        // WIP
        UIImageButton settingButton = new UIImageButton("textures/ui/UI_Icon_Setting.png");
        UIImageButton exitImgButton = new UIImageButton("textures/ui/IconExitGame.png");
        UIImageButton soundButton = new UIImageButton("textures/ui/ImgReShaSoundOn.png");
        UIImageButton closeButton = new UIImageButton("textures/ui/UI_Gcg_Icon_Close.png");
        settingButton.setSize(50, 50);
        exitImgButton.setSize(50, 50);
        soundButton.setSize(50, 50);
        closeButton.setSize(50, 50);
        settingButton.setPosition(20, WINDOW_HEIGHT - 70);
        exitImgButton.setPosition(WINDOW_WIDTH - 70, WINDOW_HEIGHT - 70);
        soundButton.setPosition(20, 20);
        closeButton.setPosition(WINDOW_WIDTH - 70, 20);
        
        root.addActor(settingButton.getActor());
        root.addActor(exitImgButton.getActor());
        root.addActor(soundButton.getActor());
        root.addActor(closeButton.getActor());
        settingButton.onClick(() -> log.debug("Settings clicked"));
        exitImgButton.onClick(() -> {
            log.debug("Exit clicked");
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 350);
            }
        });
        soundButton.onClick(() -> log.debug("Sound clicked"));
        closeButton.onClick(() -> log.debug("Close clicked"));

        game.stage.addActor(root);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(Assets.getWhiteTexture(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }
}
