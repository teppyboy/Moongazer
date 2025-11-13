package org.vibecoders.moongazer.ui.story;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.managers.Assets;

public class HighScorePanel {
    private Table panel;
    private Label.LabelStyle labelStyle;
    private Label.LabelStyle scoreLabelStyle;
    private static final BitmapFont font = Assets.getFont("ui", 24);

    private Label highScoreValueLabel;

    /**
     * Constructs a high score panel and adds it to the root table.
     *
     * @param root the root table to add this panel to
     */
    public HighScorePanel(Table root) {
        createPanel();
        root.addActor(panel);
    }

    /**
     * Creates the panel UI with the high score display.
     */
    private void createPanel() {
        panel = new Table();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);
        scoreLabelStyle = new Label.LabelStyle(font, Color.WHITE); // Gold color

        panel.setFillParent(true);
        panel.bottom().right();
        panel.padBottom(200).padRight(5);

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
        scoreRow.add(highScoreValueLabel).expandX().padRight(5);

        contentTable.add(scoreRow).width(300).height(50).padBottom(5);
        contentTable.row();

        panel.add(contentTable);
    }

    /**
     * Updates the displayed high score value.
     *
     * @param bestScore the best score to display, or 0 to show "None"
     */
    public void updateHighScore(int bestScore) {
        if (highScoreValueLabel != null) {
            if (bestScore > 0) {
                highScoreValueLabel.setText(String.valueOf(bestScore));
            } else {
                highScoreValueLabel.setText("None");
            }
        } else {
            highScoreValueLabel.setText("None");
        }
    }

    /**
     * Gets the panel table.
     *
     * @return the panel table
     */
    public Table getPanel() {
        return panel;
    }

    /**
     * Sets the visibility of the panel.
     *
     * @param visible true to show the panel, false to hide it
     */
    public void setVisible(boolean visible) {
        panel.setVisible(visible);
    }
}