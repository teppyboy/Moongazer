package org.vibecoders.moongazer.scene;

import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;
import static org.vibecoders.moongazer.Constants.WINDOW_WIDTH;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.Assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Intro extends Scene {
    private Texture logo;
    private long startTime;
    private long endTime = 0;
    private static final Logger log = LoggerFactory.getLogger(Intro.class);

    public Intro() {
        logo = Assets.getAsset("icons/logo.png", Texture.class);
        startTime = System.currentTimeMillis() + 500;
    }

    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        log.debug("Rendering logo at position: ({}, {})", WINDOW_WIDTH / 2 - logo.getWidth() / 4,
                WINDOW_HEIGHT / 2 - logo.getHeight() / 4);
        var currentOpacity = (float) (System.currentTimeMillis() - startTime) / 1000;
        if (currentOpacity > 1) {
            if (endTime == 0) {
                endTime = System.currentTimeMillis() + 3000;
            }
            currentOpacity = 1 - ((float) (System.currentTimeMillis() - endTime) / 1000);
        }
        batch.setColor(1, 1, 1, currentOpacity);
        batch.draw(logo, WINDOW_WIDTH / 2 - logo.getWidth() / 4, WINDOW_HEIGHT / 2 - logo.getHeight() / 4,
                logo.getWidth() / 2, logo.getHeight() / 2);
    }

    public void dispose() {
        log.debug("sybau");
    }
}
