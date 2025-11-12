package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.managers.Assets;

public class UIImageButton extends UIButton {
    private static final Color upTint = new Color(0.9f, 0.9f, 0.9f, 1f);
    private static final Color downTint = new Color(0.8f, 0.8f, 0.8f, 1f);
    private static final Color overTint = new Color(1f, 1f, 1f, 1f);

    public UIImageButton(String texturePath) {
        Texture texture = Assets.getAsset(texturePath, Texture.class);
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(texture));
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = buttonDrawable.tint(upTint);
        style.imageDown = buttonDrawable.tint(downTint);
        style.imageOver = buttonDrawable.tint(overTint);
        this.button = new ImageButton(style);
        this.actor = button;
    }
}
