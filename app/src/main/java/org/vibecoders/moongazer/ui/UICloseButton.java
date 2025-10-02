package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.managers.Assets;

public class UICloseButton extends UIButton {
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
}
