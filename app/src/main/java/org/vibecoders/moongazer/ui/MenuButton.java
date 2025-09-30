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
        style.overFontColor = Color.BLUE;
        style.downFontColor = Color.BLUE;

        style.up = buttonDrawable;
        style.down = buttonDrawable;
        style.over = buttonDrawable;

        textButton = new TextButton(text, style);
        this.actor = textButton;
    }
}