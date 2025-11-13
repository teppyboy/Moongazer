package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;

public class UICloseButton extends UIButton {
    /**
     * Constructs a new close button with predefined textures for normal, hover, and clicked states.
     * The button automatically loads close.png, close_hover.png, and close_clicked.png textures.
     */
    public UICloseButton() {
        Texture closeTexture = Assets.getAsset("textures/ui/close.png", Texture.class);
        Texture closeHoverTexture = Assets.getAsset("textures/ui/close_hover.png", Texture.class);
        Texture closeClickTexture = Assets.getAsset("textures/ui/close_clicked.png", Texture.class);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(closeTexture));
        TextureRegionDrawable hoverDrawable = new TextureRegionDrawable(new TextureRegion(closeHoverTexture));
        TextureRegionDrawable clickDrawable = new TextureRegionDrawable(new TextureRegion(closeClickTexture));
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawable;
        style.imageDown = clickDrawable;
        style.imageOver = hoverDrawable;
        this.button = new ImageButton(style);
        this.actor = button;
    }

    /**
     * Sets the action to be executed when the button is clicked.
     * Plays a return sound effect before executing the action.
     *
     * @param action the runnable action to execute on click
     */
    @Override
    public void onClick(Runnable action) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Audio.playSfxReturn();
                action.run();
            }
        });
    }
}
