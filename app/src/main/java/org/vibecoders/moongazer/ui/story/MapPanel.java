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

import static org.vibecoders.moongazer.Constants.MAP_HEIGHT;
import static org.vibecoders.moongazer.Constants.MAP_WIDTH;
import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;
import static org.vibecoders.moongazer.Constants.WINDOW_WIDTH;

public class MapPanel {
    private Table panel;
    private static final BitmapFont font = Assets.getFont("ui", 20);
    private final float x = (WINDOW_WIDTH - MAP_WIDTH) / 2f - 40;
    private final float y = (WINDOW_HEIGHT - MAP_HEIGHT) / 2f + 30;
    private Label title;
    private Image map;

    /**
     * Constructs a map panel and adds it to the root table.
     *
     * @param root the root table to add this panel to
     */
    public MapPanel(Table root) {
        createPanel();
        root.addActor(panel);
    }

    /**
     * Creates the panel UI with the map image and title.
     */
    private void createPanel() {
        panel = new Table();
        panel.setFillParent(true);

        title = new Label("Map", new Label.LabelStyle(font, Color.WHITE));
        title.setPosition(x + MAP_WIDTH / 2f - title.getWidth() / 2f, y + MAP_HEIGHT + 10);

        Texture mapTexture = Assets.getAsset("textures/ui/shorepiano.png", Texture.class);
        TextureRegionDrawable mapDrawable = new TextureRegionDrawable(new TextureRegion(mapTexture));
        map = new Image(mapDrawable);
        map.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        map.setSize(MAP_WIDTH, MAP_HEIGHT);
        map.setPosition(x, y);

        panel.addActor(map);
        panel.addActor(title);
    }

    /**
     * Updates the map image and title.
     *
     * @param path  the path to the new map texture
     * @param title the new title text
     */
    public void updateMap(String path, String title) {
        if (map != null) {
            map.remove();
        }
        updateTitle(title);
        Texture mapTexture = Assets.getAsset(path, Texture.class);
        TextureRegionDrawable mapDrawable = new TextureRegionDrawable(new TextureRegion(mapTexture));
        map = new Image(mapDrawable);
        map.setScaling(com.badlogic.gdx.utils.Scaling.fit);
        map.setSize(MAP_WIDTH, MAP_HEIGHT);
        map.setPosition(x, y);
        panel.addActor(map);
    }

    /**
     * Updates the title text and repositions it to center.
     *
     * @param newTitle the new title text
     */
    private void updateTitle(String newTitle) {
        title.setText(newTitle);
        title.setPosition(x + MAP_WIDTH / 2f - title.getPrefWidth() / 2f, y + MAP_HEIGHT + 10);
    }
}