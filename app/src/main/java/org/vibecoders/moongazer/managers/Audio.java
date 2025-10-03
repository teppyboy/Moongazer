package org.vibecoders.moongazer.managers;

import org.slf4j.Logger;
import org.vibecoders.moongazer.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

public class Audio {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Audio.class);
    private static boolean initialized = false;
    private static Music menuMusic = null;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        menuMusic = Gdx.audio.newMusic(Assets.getAsset("audio/I Once Praised the Day.mp3", FileHandle.class));
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

    public static void dispose() {
        if (menuMusic != null) {
            menuMusic.dispose();
            menuMusic = null;
        }
        initialized = false;
        log.info("Audio manager disposed");
    }
}
