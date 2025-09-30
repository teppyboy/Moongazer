package org.vibecoders.moongazer.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuButton extends BaseButton {
    private TextButton textButton;

    public MenuButton(String text, BitmapFont font) {
        this(text, font, Color.WHITE, Color.YELLOW);
    }

    public MenuButton(String text, BitmapFont font, Color normalColor, Color hoverColor) {

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = normalColor;
        style.overFontColor = hoverColor;
        style.downFontColor = hoverColor;

        // Transparent backgrounds
        style.up = null;
        style.down = null;
        style.over = null;
        style.checked = null;

        textButton = new TextButton(text, style);

        // Add the text button as an actor to this button
        this.actor = textButton;
    }

    public TextButton getTextButton() {
        return textButton;
    }

    public void setText(String text) {
        textButton.setText(text);
    }

    public String getText() {
        return textButton.getText().toString();
    }
}