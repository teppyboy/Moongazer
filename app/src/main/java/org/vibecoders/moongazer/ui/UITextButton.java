package org.vibecoders.moongazer.ui;

import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UITextButton extends UIButton {
    private Texture buttonTexture;

    public UITextButton(String text, BitmapFont font) {
        buttonTexture = Assets.getAsset("textures/ui/text_button.png", Texture.class);
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.overFontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;

        style.up = buttonDrawable.tint(new Color(0.9f, 0.9f, 0.9f, 1f));
        style.down = buttonDrawable.tint(new Color(0.8f, 0.8f, 0.8f, 1f));
        style.over = buttonDrawable.tint(new Color(1f, 1f, 1f, 1f));

        this.button = new TextButton(text, style);
        this.actor = this.button;
    }
}