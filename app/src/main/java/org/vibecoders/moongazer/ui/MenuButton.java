package org.vibecoders.moongazer.ui;

import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MenuButton extends BaseButton {
    private TextButton textButton;
    private Texture buttonTexture;

    public MenuButton(String text, BitmapFont font) {
        buttonTexture = Assets.getAsset("textures/ui/menu_button.png", Texture.class);
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.overFontColor = Color.BLACK;
        style.downFontColor = Color.BLUE;

        style.up = buttonDrawable;
        style.down = buttonDrawable.tint(new Color(0.8f, 0.8f, 0.8f, 1f));
        style.over = buttonDrawable.tint(new Color(1f, 1f, 1f, 0.7f));

        textButton = new TextButton(text, style);
        textButton.getColor().a = 0.7f;
        this.actor = textButton;
    }
}