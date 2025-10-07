package org.vibecoders.moongazer.managers;

import org.slf4j.Logger;
import org.vibecoders.moongazer.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

public class Audio {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Audio.class);
    private static boolean initialized = false;
    private static Music menuMusic = null;
    private static Sound selectSfx = null;
    private static Sound returnSfx = null;
    private static Sound confirmSfx = null;
    private static Sound quitGameSfx = null;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        menuMusic = Gdx.audio.newMusic(Assets.getAsset("audio/I Once Praised the Day.mp3", FileHandle.class));
        selectSfx = Gdx.audio.newSound(Assets.getAsset("audio/selectsfx.mp3", FileHandle.class));
        returnSfx = Gdx.audio.newSound(Assets.getAsset("audio/returnsfx.mp3", FileHandle.class));
        confirmSfx = Gdx.audio.newSound(Assets.getAsset("audio/confirmsfx.mp3", FileHandle.class));
        quitGameSfx = Gdx.audio.newSound(Assets.getAsset("audio/quitgamesfx.mp3", FileHandle.class));
        log.info("Audio manager initialized");
    }

    public static void menuMusicSetVolume() {
        menuMusic.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
    }

    public static void musicSetVolume() {
        menuMusicSetVolume();
    }

    public static void menuMusicPlay() {
        if (!menuMusic.isPlaying()) {
            menuMusic.setLooping(true);
            menuMusic.play();
        }
    }

    public static void menuMusicStop() {
        if (menuMusic.isPlaying()) {
            menuMusic.stop();
        }
    }

    public static void playSfxSelect() {
        if (selectSfx != null) {
            selectSfx.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void playSfxReturn() {
        if (returnSfx != null) {
            returnSfx.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void playSfxConfirm() {
        if (confirmSfx != null) {
            confirmSfx.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void playSfxQuitGame() {
        if (quitGameSfx != null) {
            quitGameSfx.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void dispose() {
        if (menuMusic != null) {
            menuMusic.dispose();
            menuMusic = null;
        }
        if (selectSfx != null) {
            selectSfx.dispose();
            selectSfx = null;
        }
        if (returnSfx != null) {
            returnSfx.dispose();
            returnSfx = null;
        }
        if (confirmSfx != null) {
            confirmSfx.dispose();
            confirmSfx = null;
        }
        if (quitGameSfx != null) {
            quitGameSfx.dispose();
            quitGameSfx = null;
        }
        initialized = false;
        log.info("Audio manager disposed");
    }
}
