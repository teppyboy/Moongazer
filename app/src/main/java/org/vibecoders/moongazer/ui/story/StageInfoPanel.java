package org.vibecoders.moongazer.ui.story;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UIScrollbar;

public class StageInfoPanel {
    private Table panel;
    private Label description;
    private Label.LabelStyle labelStyle;
    private static final BitmapFont font = Assets.getFont("ui", 20);

    public StageInfoPanel(Table root) {
        createPanel();
        root.addActor(panel);
    }

    private void createPanel() {
        panel = new Table();
        panel.setSize(600, 150);
        panel.setPosition(320, 20);
        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Table contentTable = new Table();
        contentTable.defaults().pad(2);
        contentTable.left().top();

        Label title = new Label("Simulation Info", labelStyle);
        title.setFontScale(1.2f);

        String info = "";
        description = new Label(info, labelStyle);
        description.setWrap(true);
        description.setAlignment(Align.left);

        contentTable.add(title).left().padBottom(10).row();
        contentTable.add(description).width(560).expandX().fillX().padTop(5);

        ScrollPane scrollPane = new ScrollPane(contentTable);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setScrollBarPositions(false, false);
        scrollPane.setSmoothScrolling(true);
        scrollPane.setOverscroll(false, false);

        UIScrollbar customScrollbar = new UIScrollbar(scrollPane, 5, 180);

        panel.add(scrollPane).width(560).height(130).padRight(10);
        panel.add(customScrollbar.getActor()).width(10).height(180).padLeft(5);
    }

    public void updateInfo(String info) {
        description.setText("");
        description.setText(info);
    }
}