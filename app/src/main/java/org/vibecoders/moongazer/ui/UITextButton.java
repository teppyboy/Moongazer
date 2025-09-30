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
    private Texture buttonDownTexture;
    private Texture buttonOverTexture;

    public UITextButton(String text, BitmapFont font) {
        buttonTexture = Assets.getAsset("textures/ui/menu_button.png", Texture.class);
        buttonDownTexture = Assets.getAsset("textures/ui/menu_button_down.png", Texture.class);
        buttonOverTexture = Assets.getAsset("textures/ui/menu_button_over.png", Texture.class);
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        TextureRegionDrawable buttonDownDrawable = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));
        TextureRegionDrawable buttonOverDrawable = new TextureRegionDrawable(new TextureRegion(buttonOverTexture));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        // Text style
        style.font = font;
        style.fontColor = Color.BLACK;
        style.overFontColor = Color.BLACK;
        style.downFontColor = Color.BLACK;
        // Button style
        style.up = buttonDrawable;
        style.down = buttonDownDrawable;
        style.over = buttonOverDrawable;

        this.button = new TextButton(text, style);
        this.actor = button;
    }
}