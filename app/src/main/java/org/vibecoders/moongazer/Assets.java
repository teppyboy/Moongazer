package org.vibecoders.moongazer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    private static final AssetManager assetManager = new AssetManager();

    public static <T> T getAsset(String fileName, Class<T> type) {
        return assetManager.get(fileName, type);
    }

    public static void loadAll() {
        // We load all assets here for simplicity :)
        assetManager.load("icons/logo.png", Texture.class);
        assetManager.finishLoading();
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static void dispose() {
        assetManager.dispose();
    }
}
