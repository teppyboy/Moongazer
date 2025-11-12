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
    private static final HashMap<String, com.badlogic.gdx.audio.Sound> loadedSounds = new HashMap<>();
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

    public static com.badlogic.gdx.audio.Sound getSound(String fileName) {
        if (loadedSounds.containsKey(fileName)) {
            return loadedSounds.get(fileName);
        } else {
            log.error("Sound not loaded: {}", fileName);
            return null;
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
        assetManager.load("textures/ui/GameSelectionBackground.png", Texture.class);
        // VN scene textures
        assetManager.load("textures/vn_scene/iuno.png", Texture.class);
        assetManager.load("textures/vn_scene/rover.png", Texture.class);
        assetManager.load("textures/vn_scene/boss.png", Texture.class);
        assetManager.load("textures/vn_scene/elder.png", Texture.class);
        assetManager.load("textures/vn_scene/crep.png", Texture.class);
        assetManager.load("textures/vn_scene/lili.png", Texture.class);
        assetManager.load("textures/vn_scene/separator.png", Texture.class);
        // Mode selection and stageBg assets
        assetManager.load("textures/mode_selection/stage1.png", Texture.class);
        assetManager.load("textures/mode_selection/stage2.png", Texture.class);
        assetManager.load("textures/mode_selection/stage3.png", Texture.class);
        assetManager.load("textures/mode_selection/stage4.png", Texture.class);
        assetManager.load("textures/mode_selection/stage5.png", Texture.class);
        assetManager.load("textures/stage/Bg1.png", Texture.class);
        assetManager.load("textures/stage/Bg2.png", Texture.class);
        assetManager.load("textures/stage/Bg3.png", Texture.class);
        assetManager.load("textures/stage/Bg4.png", Texture.class);
        assetManager.load("textures/stage/Bg5.png", Texture.class);
        // Arkanoid gameplay assets
        assetManager.load("textures/arkanoid/normal_ball.png", Texture.class);
        assetManager.load("textures/arkanoid/paddle.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/unbreakable_brick.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/breakable_brick_lv1.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/breakable_brick_lv2.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/breakable_brick_lv3.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/expandpaddlebrick.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/extralifebrick.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/fastballbrick.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/slowballbrick.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/multiballbrick.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/superballbrick.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/laserbrick.png", Texture.class);
        assetManager.load("textures/arkanoid/bricks/explosivebrick.png", Texture.class);
        // "Load" unsupported file types as FileHandle
        loadingThread = new Thread(() -> {
            // Load endless mode music (both OGG and MP3 formats)
            loadAny("videos/main_menu_background.webm");
            loadAny("audio/I Once Praised the Day.mp3");
            loadAny("audio/stage1.mp3");
            loadAny("audio/stage2.ogg");
            loadAny("audio/stage3.ogg");
            loadAny("audio/stage4.ogg");
            loadAny("audio/stage5.ogg");
            loadAny("audio/sfx/select.mp3");
            loadAny("audio/sfx/return.mp3");
            loadAny("audio/sfx/confirm.mp3");
            loadAny("audio/sfx/quit.mp3");
            // load story voicelines and sfx
            loadAny("audio/storysfx/BOSS.mp3");
            loadAny("audio/storysfx/IUNODIE.mp3");
            loadAny("audio/storysfx/IUNODONE.mp3");
            loadAny("audio/storysfx/IUNODONE2.mp3");
            loadAny("audio/storysfx/IUNODONE3.mp3");
            loadAny("audio/storysfx/IUNOFIGHT.mp3");
            loadAny("audio/storysfx/IUNOFIGHT2.mp3");
            loadAny("audio/storysfx/IUNOHIT.mp3");
            loadAny("audio/storysfx/IUNOINTRO.mp3");
            loadAny("audio/storysfx/IUNOLIBERSFX.mp3");
            loadAny("audio/storysfx/IUNOSAVE.mp3");
            loadAny("audio/storysfx/ROVERASK.mp3");
            loadAny("audio/storysfx/ROVERFIGHT.mp3");
            loadAny("audio/storysfx/ROVERFIGHT2.mp3");
            loadAny("audio/storysfx/ROVERGO.mp3");
            loadAny("audio/storysfx/ROVERHIT.mp3");
            loadAny("audio/storysfx/ROVERHM.mp3");
            loadAny("audio/storysfx/ROVERIDLE1.mp3");
            loadAny("audio/storysfx/ROVERLIBER.mp3");
            loadAny("audio/storysfx/ROVERLIBERSFX.mp3");
            // Try OGG first (better support)
            loadAny("audio/endlessost/endlessost1.ogg");
            loadAny("audio/endlessost/endlessost2.ogg");
            loadAny("audio/endlessost/endlessost3.ogg");
            loadAny("audio/endlessost/endlessost4.ogg");
            loadAny("audio/endlessost/endlessost5.ogg");
            // Load game over OST
            loadAny("audio/gameoverost.ogg");

            // Preload all audios to loadedSounds map
            // Create a snapshot of keys to avoid ConcurrentModificationException
            ArrayList<String> audioFiles = new ArrayList<>(loadedFiles.keySet());
            for (String audioFile : audioFiles) {
                if (audioFile.endsWith(".mp3") || audioFile.endsWith(".ogg")) {
                    try {
                        com.badlogic.gdx.audio.Sound sound = Gdx.audio.newSound(loadedFiles.get(audioFile));
                        loadedSounds.put(audioFile, sound);
                    } catch (Exception e) {
                        log.error("Failed to load sound: {}", audioFile, e);
                    }
                }
            }
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
