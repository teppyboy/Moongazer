package org.vibecoders.moongazer.vn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

/**
 * Transparent dialogue box with typing effect.
 * Shows speaker name, separator, and dialogue text.
 */
public class DialogueBoxTransparent extends Group {
    private final Label nameLabel;
    private final Label textLabel;
    private CharSequence fullText;
    private float shown = 0f;
    private boolean done = true;
    private final float charPerSec = 45f;

    private static final float BOX_HEIGHT = 200f;
    private static final float TEXT_MARGIN = 20f;

    public DialogueBoxTransparent(BitmapFont font, TextureRegion bgRegion, TextureRegion sepRegion, float width) {
        Image background = new Image(bgRegion);
        background.setSize(width, BOX_HEIGHT);
        background.setColor(1f, 1f, 1f, 0.3f);
        addActor(background);

        Label.LabelStyle nameStyle = new Label.LabelStyle();
        nameStyle.font = font;
        nameStyle.fontColor = Color.GOLD;
        nameLabel = new Label("", nameStyle);
        nameLabel.setFontScale(1.2f);
        nameLabel.setAlignment(Align.center);
        nameLabel.setWidth(width);
        nameLabel.setPosition(0, BOX_HEIGHT + 40);
        addActor(nameLabel);

        Image separator = new Image(sepRegion);
        float separatorDisplayWidth = width;
        float aspectRatio = (float) sepRegion.getRegionHeight() / (float) sepRegion.getRegionWidth();
        float separatorDisplayHeight = separatorDisplayWidth * aspectRatio;

        if (separatorDisplayHeight > 180f) {
            separatorDisplayHeight = 180f;
            separatorDisplayWidth = separatorDisplayHeight / aspectRatio;
        }

        separator.setSize(separatorDisplayWidth, separatorDisplayHeight);
        float sepX = (width - separatorDisplayWidth) / 2f;
        float sepY = BOX_HEIGHT - 100;
        separator.setPosition(sepX, sepY);
        separator.setColor(1f, 1f, 1f, 1f);
        addActor(separator);

        Label.LabelStyle textStyle = new Label.LabelStyle();
        textStyle.font = font;
        textStyle.fontColor = Color.WHITE;
        textLabel = new Label("", textStyle);
        textLabel.setWrap(true);
        textLabel.setWidth(width - TEXT_MARGIN * 2);
        textLabel.setAlignment(Align.center);
        textLabel.setPosition(TEXT_MARGIN, (BOX_HEIGHT / 2) - 20);
        addActor(textLabel);

        setSize(width, BOX_HEIGHT + 30);
    }

    public void setDialogue(String speaker, String text) {
        nameLabel.setText(speaker);
        fullText = text;
        shown = 0f;
        done = false;
        textLabel.setText("");
    }

    public boolean isDone() {
        return done;
    }

    public void skip() {
        if (fullText != null) {
            textLabel.setText(fullText);
            done = true;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (done || fullText == null) return;

        shown += charPerSec * delta;
        int n = Math.min(fullText.length(), (int) shown);
        textLabel.setText(fullText.subSequence(0, n));

        if (n >= fullText.length()) {
            done = true;
        }
    }
}