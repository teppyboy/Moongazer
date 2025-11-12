package org.vibecoders.moongazer.managers;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Test stub for Assets manager - this overrides the real Assets class in tests
 * Returns dummy textures instead of loading real files
 */
public class Assets {
    private static Texture dummyTexture;
    
    static {
        // Create one dummy texture to be reused
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        dummyTexture = new Texture(pixmap);
        pixmap.dispose();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getAsset(String fileName, Class<T> type) {
        if (type == Texture.class) {
            return (T) dummyTexture;
        }
        return null;
    }
    
    public static Texture getBlackTexture() {
        return dummyTexture;
    }
}
