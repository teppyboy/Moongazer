package org.vibecoders.moongazer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Client configuration constants and default values
 * used throughout the Moongazer client application.
 */
public class Constants {

    /**
     * Window configuration.
     */
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final String WINDOW_TITLE = "Moongazer";
    public static final Texture TEXTURE_WHITE = new Texture(new Pixmap(1, 1, Pixmap.Format.RGBA8888) {{
        setColor(Color.WHITE);
        fill();
    }});
    public static final Texture TEXTURE_BLACK = new Texture(new Pixmap(1, 1, Pixmap.Format.RGBA8888) {{
        setColor(Color.BLACK);
        fill();
    }});
}
