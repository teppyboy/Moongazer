package org.vibecoders.moongazer.managers;

import org.slf4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class Assets {
    private static final AssetManager assetManager = new AssetManager();
    private static final FileHandleResolver resolver = new InternalFileHandleResolver();
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Assets.class);
    private static final ArrayList<String> loadedFonts = new ArrayList<>();
    private static final HashMap<String, FileHandle> loadedFiles = new HashMap<>();
    private static boolean startLoadAll = false;
    private static boolean loadedAll = false;
    private static Thread loadingThread = null;
    private static Texture textureWhite;
    private static Texture textureBlack;

    public static <T> T getAsset(String fileName, Class<T> type) {
        if (type == FileHandle.class) {
            if (!loadedFiles.containsKey(fileName)) {
                loadAny(fileName);
            }
            return type.cast(loadedFiles.get(fileName));
        }
        try {
            if (!assetManager.isLoaded(fileName, type)) {
                log.warn("Asset not loaded: {}", fileName);
                assetManager.load(fileName, type);
                assetManager.finishLoadingAsset(fileName);
            }
            return assetManager.get(fileName, type);
        } catch (Exception e) {
            log.error("Failed to load asset: {}", fileName, e);
            throw new RuntimeException("Asset loading failed: " + fileName, e);
        }
    }

    /**
     * Loads and returns a BitmapFont of the specified size from the given TTF file.
     * <p>
     * Special file name "ui" is mapped to "fonts/H7GBKHeavy.ttf" (Wuthering Waves
     * UI font).
     * 
     * @param fileName the font name
     * @param size     the font size
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

    public static void loadAny(String fileName) {
        FileHandle fh = Gdx.files.internal(fileName); 
        if (!fh.exists()) {
            log.error("File does not exist: {}", fileName);
            return;
        }
        if (loadedFiles.containsKey(fileName)) {
            return;
        }
        loadedFiles.put(fileName, fh);
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
        // Load all assets here
        assetManager.load("textures/main_menu/background.png", Texture.class);
        assetManager.load("textures/main_menu/title.png", Texture.class);
        assetManager.load("textures/ui/text_button.png", Texture.class);
        assetManager.load("textures/ui/IconExitGame.png", Texture.class);
        assetManager.load("textures/ui/UI_Icon_Setting.png", Texture.class);
        assetManager.load("textures/ui/ImgReShaSoundOn.png", Texture.class);
        assetManager.load("textures/ui/UI_Gcg_Icon_Close.png", Texture.class);
        assetManager.load("textures/ui/arrow-button-left.png", Texture.class);
        assetManager.load("textures/ui/arrow-button-right.png", Texture.class);
        assetManager.load("textures/ui/close.png", Texture.class);
        assetManager.load("textures/ui/close_hover.png", Texture.class);
        assetManager.load("textures/ui/close_clicked.png", Texture.class);
        assetManager.load("textures/ui/UI_SliderKnob.png", Texture.class);
        assetManager.load("textures/ui/UI_SliderBg.png", Texture.class);
        assetManager.load("textures/ui/UI_SliderBg2.png", Texture.class);
        assetManager.load("textures/ui/StoryMode.png", Texture.class);
        assetManager.load("textures/ui/EndlessMode.png", Texture.class);
        assetManager.load("textures/stage/stage5.png", Texture.class);
        assetManager.load("textures/ui/UI_Scrollbar_Handle.png", Texture.class);
        assetManager.load("textures/ui/hearth.png", Texture.class);
        assetManager.load("textures/ui/Lunite.png", Texture.class);
        assetManager.load("textures/ui/Astrite.png", Texture.class);
        assetManager.load("textures/ui/UI_Icon_Tower_Star.png", Texture.class);
        assetManager.load("textures/ui/ScrollVerticalBg.png", Texture.class);
        assetManager.load("textures/ui/ScrollVerticalKnob.png", Texture.class);
        assetManager.load("textures/stage/MapTest.png", Texture.class);
        assetManager.load("textures/ui/shorepiano.png", Texture.class);
        // VN scene textures
        assetManager.load("textures/vn_scene/iuno.png", Texture.class);
        assetManager.load("textures/vn_scene/separator.png", Texture.class);
        // "Load" unsupported file types as FileHandle
        loadingThread = new Thread(() -> {
            loadAny("videos/main_menu_background.webm");
            loadAny("audio/I Once Praised the Day.mp3");
            loadAny("audio/sfx/select.mp3");
            loadAny("audio/sfx/return.mp3");
            loadAny("audio/sfx/confirm.mp3");
            loadAny("audio/sfx/quit.mp3");
        });
        loadingThread.start();
    }

    public static boolean isLoadedAll() {
        return loadedAll;
    }

    public static boolean isStartLoadAll() {
        return startLoadAll;
    }

    public static void waitUntilLoaded() {
        assetManager.finishLoading();
        if (loadingThread != null) {
            try {
                loadingThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            loadingThread = null;
        }
        if (startLoadAll) {
            loadedAll = true;
        }
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static Texture getWhiteTexture() {
        if (textureWhite == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            textureWhite = new Texture(pixmap);
            pixmap.dispose(); 
        }
        return textureWhite;
    }

    public static Texture getBlackTexture() {
        if (textureBlack == null) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.BLACK);
            pixmap.fill();
            textureBlack = new Texture(pixmap);
            pixmap.dispose();
        }
        return textureBlack;
    }

    public static void dispose() {
        for (var fontKey : loadedFonts) {
            if (assetManager.isLoaded(fontKey, BitmapFont.class)) {
                assetManager.unload(fontKey);
            }
        }
        loadedFonts.clear();
        loadedFiles.clear();
        assetManager.dispose();
        if (textureWhite != null) {
            textureWhite.dispose();
            textureWhite = null;
        }
        if (textureBlack != null) {
            textureBlack.dispose();
            textureBlack = null;
        }
    }
}
