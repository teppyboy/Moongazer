package org.vibecoders.moongazer.ui;

import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UITextButton extends UIButton {
    private Texture buttonTexture;
    private static final Color upTint = new Color(0.9f, 0.9f, 0.9f, 1f);
    private static final Color downTint = new Color(0.8f, 0.8f, 0.8f, 1f);
    private static final Color overTint = new Color(1f, 1f, 1f, 1f);

    /**
     * Constructs a new text button with the specified text and font.
     * The button automatically loads a default button texture with different tints for states.
     *
     * @param text the text to display on the button
     * @param font the bitmap font to use for rendering the text
     */
    public UITextButton(String text, BitmapFont font) {
        buttonTexture = Assets.getAsset("textures/ui/text_button.png", Texture.class);
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.overFontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;

        style.up = buttonDrawable.tint(upTint);
        style.down = buttonDrawable.tint(downTint);
        style.over = buttonDrawable.tint(overTint);

        this.button = new TextButton(text, style);
        this.actor = this.button;
    }

    /**
     * Sets the action to be executed when the button is clicked.
     * Plays a select sound effect before executing the action.
     *
     * @param action the runnable action to execute on click
     */
    public void onClick(Runnable action) {
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Audio.playSfxSelect();
                action.run();
            }
        });
    }
}