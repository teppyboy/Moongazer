package org.vibecoders.moongazer.ui.storymodeUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.managers.Assets;

public class StageRewardPanel {
    private Table panel;
    private Label.LabelStyle labelStyle;
    private static final BitmapFont font = Assets.getFont("ui", 24);

    public StageRewardPanel(Table root) {
        createPanel();
        root.addActor(panel);
    }

    private void createPanel() {
        panel = new Table();
        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        panel.setFillParent(true);
        panel.bottom().right();
        panel.padBottom(300).padRight(50);

        Table contentTable = new Table();
        contentTable.defaults().pad(4);

        Label title = new Label("Reward", labelStyle);
        panel.add(title).colspan(2).padTop(60).padBottom(15);
        panel.row();

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        var tintedBg = bg.tint(new Color(0.3f, 0.3f, 0.3f, 0.3f));

        Texture luniteTexture = Assets.getAsset("textures/ui/Lunite.png", Texture.class);
        TextureRegionDrawable luniteDrawable = new TextureRegionDrawable(new TextureRegion(luniteTexture));
        Image luniteIcon = new Image(luniteDrawable);
        Texture astriteTexture = Assets.getAsset("textures/ui/Astrite.png", Texture.class);
        TextureRegionDrawable astriteDrawable = new TextureRegionDrawable(new TextureRegion(astriteTexture));
        Image astriteIcon = new Image(astriteDrawable);

        String[] rewards = {"Lunite", "Astrite"};
        for (String reward : rewards) {
            Table row = new Table();
            if (reward.equals("Lunite")) {
                row.add(luniteIcon).size(40, 40).padLeft(10).padRight(5);
            } else if (reward.equals("Astrite")) {
                row.add(astriteIcon).size(40, 40).padLeft(10).padRight(5);
            }
            row.add(new Label(reward, labelStyle)).expandX().left().padLeft(40).pad(15);
            row.setBackground(tintedBg);
            contentTable.add(row).width(300).height(35).padBottom(5);
            contentTable.row();
        }

        panel.add(contentTable);
    }
}
