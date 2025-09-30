package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.managers.Assets;

public class UIImageButton extends UIButton {

    public UIImageButton() {
        this.button = new ImageButton(new ImageButton.ImageButtonStyle());
        this.actor = button;
    }

    public UIImageButton(UIImageButton other) {
        this.button = new ImageButton((ImageButton.ImageButtonStyle) other.button.getStyle());
        this.actor = button;
    }

    private static UIImageButton createButton(String texturePath) {
        Texture texture = Assets.getAsset(texturePath, Texture.class);
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawable;
        style.imageDown = drawable;
        style.imageOver = drawable;

        UIImageButton button = new UIImageButton();
        button.button.setStyle(style);
        return button;
    }

    public static UIImageButton settingButton() {
        return createButton("textures/ui/UI_Icon_Setting.png");
    }

    public static UIImageButton exitButton() {
        return createButton("textures/ui/IconExitGame.png");
    }

    public static UIImageButton soundButton() {
        return createButton("textures/ui/ImgReShaSoundOn.png");
    }

    public static UIImageButton closeButton() {
        return createButton("textures/ui/UI_Gcg_Icon_Close.png");
    }
}
