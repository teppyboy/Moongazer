package org.vibecoders.moongazer;

import org.slf4j.Logger;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    private static final AssetManager assetManager = new AssetManager();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Assets.class);

    public static <T> T getAsset(String fileName, Class<T> type) {
        return assetManager.get(fileName, type);
    }

    /**
     * Loads assets required for the intro scene only.
     * 
     * This is used to load the logo before the main assets are loaded.
     */
    public static void loadIntroAndWait() {
        assetManager.load("icons/logo.png", Texture.class);
        waitUntilLoaded();
    }

    public static void loadAll() {
        log.info("Loading all assets....");
        log.warn("stub");
    }

    public static void waitUntilLoaded() {
        assetManager.finishLoading();
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static void dispose() {
        assetManager.dispose();
    }
}
