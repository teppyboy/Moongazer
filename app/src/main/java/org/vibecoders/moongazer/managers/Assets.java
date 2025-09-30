package org.vibecoders.moongazer.managers;

import org.slf4j.Logger;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import java.util.ArrayList;

public class Assets {
    private static final AssetManager assetManager = new AssetManager();
    private static final FileHandleResolver resolver = new InternalFileHandleResolver();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Assets.class);
    private static final ArrayList<String> loadedFonts = new ArrayList<>();
    private static boolean startLoadAll = false;
    private static boolean loadedAll = false;

    public static <T> T getAsset(String fileName, Class<T> type) {
        return assetManager.get(fileName, type);
    }

    /**
     * Loads and returns a BitmapFont of the specified size from the given TTF file.
     * <p>
     * Special file name "ui" is mapped to "fonts/H7GBKHeavy.ttf" (Wuthering Waves UI font).
     * 
     * @param fileName the font name
     * @param size the font size
     * @return the loaded BitmapFont
     */
    public static BitmapFont getFont(String fileName, int size) {
        if (fileName.equals("ui")) {
            fileName = "fonts/H7GBKHeavy.ttf";
        }
        // Only works for .ttf files but okay.
        var fontKey = fileName.split(".", 1)[0] + "-" + size + ".ttf";
        if (!loadedFonts.contains(fontKey)) {
            var params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
            params.fontFileName = fileName;
            params.fontParameters.size = size;
            assetManager.load(fontKey, BitmapFont.class, params);
            assetManager.finishLoadingAsset(fontKey);
            loadedFonts.add(fontKey);
        }
        return assetManager.get(fontKey, BitmapFont.class);
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
        if (startLoadAll) {
            log.warn("loadAll() called multiple times!");
            return;
        }
        log.info("Loading all assets....");
        startLoadAll = true;
        // Add loader for TTF fonts
        assetManager.setLoader(com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.class,
                new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
    }

    public static boolean isLoadedAll() {
        return loadedAll;
    }

    public static void waitUntilLoaded() {
        assetManager.finishLoading();
        if (startLoadAll) {
            log.info("All assets loaded.");
            loadedAll = true;
        }
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static void dispose() {
        assetManager.dispose();
    }
}
