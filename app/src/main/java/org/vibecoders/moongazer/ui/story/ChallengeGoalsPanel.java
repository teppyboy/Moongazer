package org.vibecoders.moongazer.ui.story;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.managers.Assets;

public class ChallengeGoalsPanel {
    private Table panel;
    private Label.LabelStyle labelStyle;
    private static final BitmapFont font = Assets.getFont("ui", 24);

    public ChallengeGoalsPanel(Table root) {
        createPanel();
        root.addActor(panel);
    }

    private void createPanel() {
        panel = new Table();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        panel.setFillParent(true);
        panel.top().right();
        panel.padTop(70).padRight(50);

        Table contentTable = new Table();
        contentTable.defaults().pad(4);

        Label title = new Label("Challenge goals", labelStyle);
        contentTable.add(title).colspan(2).padTop(60).padBottom(15);
        contentTable.row();

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        var tintedBg = bg.tint(new Color(0.3f, 0.3f, 0.3f, 0.3f));

        Texture hearthTexture = Assets.getAsset("textures/ui/hearth.png", Texture.class);
        TextureRegionDrawable hearthDrawable = new TextureRegionDrawable(new TextureRegion(hearthTexture));
        Texture starTexture = Assets.getAsset("textures/ui/UI_Icon_Tower_Star.png", Texture.class);
        TextureRegionDrawable starDrawable = new TextureRegionDrawable(new TextureRegion(starTexture));

        String[] conditions = { "Remaining = 1    1", "Remaining = 2    2", "Remaining = 3    3"};
        for (String condition : conditions) {
            Table row = new Table();
            Image starIcon = new Image(starDrawable);
            Image hearthIcon = new Image(hearthDrawable);
            row.add(hearthIcon).size(30, 30).padLeft(1).padRight(4);
            row.setBackground(tintedBg);
            row.add(new Label(condition, labelStyle)).padLeft(5);
            row.add(starIcon).size(35, 35).padLeft(1).padRight(1);
            contentTable.add(row).width(300).height(35).padBottom(5);
            contentTable.row();
        }
        panel.add(contentTable);
    }
}
