package org.vibecoders.moongazer.ui.story;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.SaveGameManager;
import org.vibecoders.moongazer.managers.Assets;

public class HighScorePanel {
    private Table panel;
    private Label.LabelStyle labelStyle;
    private Label.LabelStyle scoreLabelStyle;
    private static final BitmapFont font = Assets.getFont("ui", 24);

    private Label highScoreValueLabel;

    public HighScorePanel(Table root) {
        createPanel();
        root.addActor(panel);
    }

    private void createPanel() {
        panel = new Table();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        scoreLabelStyle = new Label.LabelStyle(font, Color.WHITE); // Gold color

        panel.setFillParent(true);
        panel.bottom().right();
        panel.padBottom(200).padRight(50);

        Table contentTable = new Table();
        contentTable.defaults().pad(8);

        Label title = new Label("Highest Score", labelStyle);
        contentTable.add(title).colspan(2).padTop(5).padBottom(5);
        contentTable.row();

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        var tintedBg = bg.tint(new Color(0.2f, 0.2f, 0.3f, 0.5f));

        Table scoreRow = new Table();
        scoreRow.setBackground(tintedBg);

        highScoreValueLabel = new Label("", scoreLabelStyle);
        scoreRow.add(highScoreValueLabel).expandX().padRight(20);

        contentTable.add(scoreRow).width(300).height(50).padBottom(5);
        contentTable.row();

        panel.add(contentTable);
    }

    public void updateHighScore(int bestScore) {
        if (highScoreValueLabel != null) {
            highScoreValueLabel.setText(String.valueOf(bestScore));
        } else {
            highScoreValueLabel.setText("None");
        }
    }

    public Table getPanel() {
        return panel;
    }

    public void setVisible(boolean visible) {
        panel.setVisible(visible);
    }
}