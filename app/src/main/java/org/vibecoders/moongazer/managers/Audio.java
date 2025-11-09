package org.vibecoders.moongazer.managers;

import org.slf4j.Logger;
import org.vibecoders.moongazer.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class Audio {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Audio.class);
    private static boolean initialized = false;
    private static final Map<String, Music> musicTracks = new HashMap<>();
    private static final Map<String, Sound> soundEffects = new HashMap<>();


    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        
        // Initialize music tracks
        musicTracks.put("menu", Gdx.audio.newMusic(Assets.getAsset("audio/I Once Praised The Day.mp3", FileHandle.class)));
        musicTracks.put("stage1", Gdx.audio.newMusic(Assets.getAsset("audio/stage1.mp3", FileHandle.class)));
        musicTracks.put("stage2", Gdx.audio.newMusic(Assets.getAsset("audio/stage2.ogg", FileHandle.class)));
        musicTracks.put("stage3", Gdx.audio.newMusic(Assets.getAsset("audio/stage3.ogg", FileHandle.class)));
        musicTracks.put("stage4", Gdx.audio.newMusic(Assets.getAsset("audio/stage4.ogg", FileHandle.class)));
        musicTracks.put("stage5", Gdx.audio.newMusic(Assets.getAsset("audio/stage5.ogg", FileHandle.class)));
        
        // Initialize sound effects
        soundEffects.put("select", Gdx.audio.newSound(Assets.getAsset("audio/sfx/select.mp3", FileHandle.class)));
        soundEffects.put("return", Gdx.audio.newSound(Assets.getAsset("audio/sfx/return.mp3", FileHandle.class)));
        soundEffects.put("confirm", Gdx.audio.newSound(Assets.getAsset("audio/sfx/confirm.mp3", FileHandle.class)));
        soundEffects.put("quit", Gdx.audio.newSound(Assets.getAsset("audio/sfx/quit.mp3", FileHandle.class)));
        soundEffects.put("powerupReceive", Gdx.audio.newSound(Assets.getAsset("audio/sfx/PowerUpReceived.mp3", FileHandle.class)));
        soundEffects.put("paddleHit", Gdx.audio.newSound(Assets.getAsset("audio/sfx/PaddleHit.mp3", FileHandle.class)));
        soundEffects.put("brickHit", Gdx.audio.newSound(Assets.getAsset("audio/sfx/BlockDamage.mp3", FileHandle.class)));
        soundEffects.put("ballLoss", Gdx.audio.newSound(Assets.getAsset("audio/sfx/BallLoss.mp3", FileHandle.class)));
        
        log.info("Audio manager initialized");
    }

    // Generic music playback methods
    private static void playMusic(String trackName) {
        Music music = musicTracks.get(trackName);
        if (music != null && !music.isPlaying()) {
            music.setLooping(true);
            music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            music.play();
        }
    }

    private static void stopMusic(String trackName) {
        Music music = musicTracks.get(trackName);
        if (music != null && music.isPlaying()) {
            music.stop();
        }
    }

    private static void playSfx(String sfxName) {
        Sound sfx = soundEffects.get(sfxName);
        if (sfx != null) {
            sfx.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    // Public API methods for backward compatibility
    public static void menuMusicPlay() {
        playMusic("menu");
    }

    public static void menuMusicStop() {
        stopMusic("menu");
    }

    public static void menuMusicSetVolume() {
        Music music = musicTracks.get("menu");
        if (music != null) {
            music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        }
    }

    public static void musicSetVolume() {
        menuMusicSetVolume();
        for (Music music : musicTracks.values()) {
            if (music != null && music.isPlaying()) {
                music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            }
        }
    }

    // Stage music methods
    public static void stage1MusicPlay() {
        playMusic("stage1");
    }

    public static void stage1MusicStop() {
        stopMusic("stage1");
    }

    public static void stage2MusicPlay() {
        playMusic("stage2");
    }

    public static void stage2MusicStop() {
        stopMusic("stage2");
    }

    public static void stage3MusicPlay() {
        playMusic("stage3");
    }

    public static void stage3MusicStop() {
        stopMusic("stage3");
    }

    public static void stage4MusicPlay() {
        playMusic("stage4");
    }

    public static void stage4MusicStop() {
        stopMusic("stage4");
    }

    public static void stage5MusicPlay() {
        playMusic("stage5");
    }

    public static void stage5MusicStop() {
        stopMusic("stage5");
    }

    // Sound effects methods
    public static void playSfxSelect() {
        playSfx("select");
    }

    public static void playSfxReturn() {
        playSfx("return");
    }

    public static void playSfxConfirm() {
        playSfx("confirm");
    }

    public static void playSfxQuitGame() {
        playSfx("quit");
    }

    public static void playSfxPowerupReceive() {
        playSfx("powerupReceive");
    }

    public static void playSfxPaddleHit() {
        playSfx("paddleHit");
    }

    public static void playSfxBrickHit() {
        playSfx("brickHit");
    }

    public static void playSfxBallLoss() {
        playSfx("ballLoss");
    }

    public static void dispose() {
        for (Music music : musicTracks.values()) {
            if (music != null) {
                music.dispose();
            }
        }
        musicTracks.clear();
        
        for (Sound sound : soundEffects.values()) {
            if (sound != null) {
                sound.dispose();
            }
        }
        soundEffects.clear();
        
        initialized = false;
        log.info("Audio manager disposed");
    }
}
