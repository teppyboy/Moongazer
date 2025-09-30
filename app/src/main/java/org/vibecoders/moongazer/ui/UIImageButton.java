package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class UIImageButton extends UIButton {
    public UIImageButton() {
        this.button = new ImageButton(new ImageButton.ImageButtonStyle());
        this.actor = button;
    }
}
