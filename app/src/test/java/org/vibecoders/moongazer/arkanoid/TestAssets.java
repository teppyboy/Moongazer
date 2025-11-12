package org.vibecoders.moongazer.arkanoid;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import java.util.HashMap;
import java.util.Map;

/**
 * Test implementation of Assets manager that returns dummy textures
 * instead of loading real files
 */
public class TestAssets {
    private static final Map<String, Texture> textureCache = new HashMap<>();
    private static Texture dummyTexture;
    
    public static void initialize() {
        // Create one dummy texture to be reused
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        dummyTexture = new Texture(pixmap);
        pixmap.dispose();
    }
    
    public static void dispose() {
        if (dummyTexture != null) {
            dummyTexture.dispose();
            dummyTexture = null;
        }
        textureCache.clear();
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
