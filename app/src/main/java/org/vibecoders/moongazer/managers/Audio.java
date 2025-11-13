package org.vibecoders.moongazer.managers;

import org.slf4j.Logger;
import org.vibecoders.moongazer.Settings;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Audio {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Audio.class);
    private static boolean initialized = false;
    private static final Map<String, Music> musicTracks = new HashMap<>();
    private static final Map<String, Sound> soundEffects = new HashMap<>();
    private static List<Music> endlessMusicPlaylist = null;
    private static int currentEndlessTrackIndex = 0;
    private static boolean endlessMusicActive = false;
    private static Music gameOverMusic = null;
    private static boolean gameOverMusicActive = false;

    /**
     * Initializes the audio manager by loading music tracks and sound effects.
     * This method should be called once at the start of the application.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;
        musicTracks.put("menu", Gdx.audio.newMusic(Assets.getAsset("audio/I Once Praised The Day.mp3", FileHandle.class)));
        musicTracks.put("stage1", Gdx.audio.newMusic(Assets.getAsset("audio/stage1.mp3", FileHandle.class)));
        musicTracks.put("stage2", Gdx.audio.newMusic(Assets.getAsset("audio/stage2.ogg", FileHandle.class)));
        musicTracks.put("stage3", Gdx.audio.newMusic(Assets.getAsset("audio/stage3.ogg", FileHandle.class)));
        musicTracks.put("stage4", Gdx.audio.newMusic(Assets.getAsset("audio/stage4.ogg", FileHandle.class)));
        musicTracks.put("stage5", Gdx.audio.newMusic(Assets.getAsset("audio/stage5.ogg", FileHandle.class)));
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

    /**
     * Plays the specified music track if it is not already playing.
     *
     * @param trackName The name of the music track to play.
     */
    private static void playMusic(String trackName) {
        Music music = musicTracks.get(trackName);
        if (music != null && !music.isPlaying()) {
            music.setLooping(true);
            music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            music.play();
        }
    }

    /**
     * Stops the specified music track if it is currently playing.
     *
     * @param trackName The name of the music track to stop.
     */
    private static void stopMusic(String trackName) {
        Music music = musicTracks.get(trackName);
        if (music != null && music.isPlaying()) {
            music.stop();
        }
    }

    /**
     * Plays the specified sound effect.
     *
     * @param sfxName The name of the sound effect to play.
     */
    private static void playSfx(String sfxName) {
        Sound sfx = soundEffects.get(sfxName);
        if (sfx != null) {
            sfx.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void menuMusicPlay() { playMusic("menu"); }
    public static void menuMusicStop() { stopMusic("menu"); }
    public static void stage1MusicPlay() { playMusic("stage1"); }
    public static void stage1MusicStop() { stopMusic("stage1"); }
    public static void stage2MusicPlay() { playMusic("stage2"); }
    public static void stage2MusicStop() { stopMusic("stage2"); }
    public static void stage3MusicPlay() { playMusic("stage3"); }
    public static void stage3MusicStop() { stopMusic("stage3"); }
    public static void stage4MusicPlay() { playMusic("stage4"); }
    public static void stage4MusicStop() { stopMusic("stage4"); }
    public static void stage5MusicPlay() { playMusic("stage5"); }
    public static void stage5MusicStop() { stopMusic("stage5"); }
    public static void playSfxSelect() { playSfx("select"); }
    public static void playSfxReturn() { playSfx("return"); }
    public static void playSfxConfirm() { playSfx("confirm"); }
    public static void playSfxQuitGame() { playSfx("quit"); }
    public static void playSfxPowerupReceive() { playSfx("powerupReceive"); }
    public static void playSfxPaddleHit() { playSfx("paddleHit"); }
    public static void playSfxBrickHit() { playSfx("brickHit"); }
    public static void playSfxBallLoss() { playSfx("ballLoss"); }

    /**
     * Sets the volume for all currently playing music tracks based on settings.
     */
    public static void musicSetVolume() {
        for (Music music : musicTracks.values()) {
            if (music != null && music.isPlaying()) {
                music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            }
        }
    }

    /** Initializes the endless mode music playlist by loading tracks from assets */
    public static void initEndlessMusic() {
        if (endlessMusicPlaylist != null) {
            log.info("Endless music playlist already initialized");
            return;
        }
        log.info("=== Initializing endless mode music playlist ===");
        endlessMusicPlaylist = new ArrayList<>();
        String[] trackNames = {"endlessost1", "endlessost2", "endlessost3", "endlessost4", "endlessost5"};
        for (String trackName : trackNames) {
            String[] extensions = {".ogg", ".mp3"};
            boolean loaded = false;
            for (String ext : extensions) {
                try {
                    String fullPath = "audio/endlessost/" + trackName + ext;
                    log.info("Trying to load: {}", fullPath);
                    FileHandle fileHandle = Assets.getAsset(fullPath, FileHandle.class);
                    log.info("FileHandle info - exists: {}, length: {} bytes", fileHandle.exists(), fileHandle.length());
                    if (fileHandle.length() == 0) {
                        log.error("File is empty (0 bytes): {}", fullPath);
                        continue;
                    }
                    Music music = Gdx.audio.newMusic(fileHandle);
                    endlessMusicPlaylist.add(music);
                    log.info("✓✓✓ Successfully loaded: {}{}", trackName, ext);
                    loaded = true;
                    break;
                } catch (Exception e) {
                    log.debug("Failed to load {}{}: {}", trackName, ext, e.getMessage());
                }
            }
            if (!loaded) {
                log.error("✗✗✗ Could not load {} in any supported format (.ogg or .mp3)", trackName);
                log.error("Please convert this file to OGG format using Audacity or ffmpeg:");
                log.error("  ffmpeg -i {}.mp3 -c:a libvorbis -q:a 5 {}.ogg", trackName, trackName);
            }
        }

        if (!endlessMusicPlaylist.isEmpty()) {
            log.info("Endless playlist loaded with {} tracks", endlessMusicPlaylist.size());
        }
    }

    /**
     * Starts playing endless mode music in a looped playlist
     */
    public static void startEndlessMusic() {
        if (endlessMusicPlaylist == null) initEndlessMusic();
        if (endlessMusicPlaylist == null || endlessMusicPlaylist.isEmpty()) {
            log.error("Cannot start endless music - playlist is empty or null!");
            return;
        }
        menuMusicStop();
        currentEndlessTrackIndex = 0;
        endlessMusicActive = true;
        playCurrentEndlessTrack();
        log.info("Endless mode music started");
    }

    /**
     * Plays the current track in the endless music playlist and sets up the completion listener
     */
    private static void playCurrentEndlessTrack() {
        if (endlessMusicPlaylist == null || endlessMusicPlaylist.isEmpty() || !endlessMusicActive) return;
        Music currentTrack = endlessMusicPlaylist.get(currentEndlessTrackIndex);
        currentTrack.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        currentTrack.setLooping(false);
        currentTrack.setOnCompletionListener(music -> {
            if (endlessMusicActive) {
                currentEndlessTrackIndex = (currentEndlessTrackIndex + 1) % endlessMusicPlaylist.size();
                playCurrentEndlessTrack();
            }
        });
        currentTrack.play();
    }

    /**
     * Updates the volume of the currently playing endless mode music track based on settings
     */
    public static void updateEndlessMusicVolume() {
        if (endlessMusicPlaylist != null && endlessMusicActive && 
            currentEndlessTrackIndex >= 0 && currentEndlessTrackIndex < endlessMusicPlaylist.size()) {
            Music currentTrack = endlessMusicPlaylist.get(currentEndlessTrackIndex);
            if (currentTrack.isPlaying()) {
                currentTrack.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            }
        }
    }

    /**
     * Stops and disposes endless mode music
     */
    public static void stopEndlessMusic() {
        if (endlessMusicPlaylist == null) {
            return;
        }
        endlessMusicActive = false;
        for (Music music : endlessMusicPlaylist) {
            if (music.isPlaying()) music.stop();
        }
        endlessMusicPlaylist.clear();
        endlessMusicPlaylist = null;
        currentEndlessTrackIndex = 0;
        log.info("Endless mode music stopped and disposed");
    }

    /**
     * Updates the volume of all music tracks based on settings
     */
    public static void updateAllMusicVolume() {
        musicSetVolume();
        updateEndlessMusicVolume();
        updateGameOverMusicVolume();
    }

    /**
     * Starts playing the game over music (looping)
     */
    public static void startGameOverMusic() {
        log.info("=== Starting Game Over Music ===");

        // Stop endless music if playing
        if (endlessMusicActive) {
            log.info("Stopping endless music for game over");
            // Don't dispose, just stop
            endlessMusicActive = false;
            if (endlessMusicPlaylist != null) {
                for (Music music : endlessMusicPlaylist) {
                    if (music.isPlaying()) {
                        music.stop();
                    }
                }
            }
        }

        // Stop menu music if playing
        menuMusicStop();

        // Load and play game over music
        try {
            if (gameOverMusic == null) {
                FileHandle fileHandle = Assets.getAsset("audio/gameoverost.ogg", FileHandle.class);
                gameOverMusic = Gdx.audio.newMusic(fileHandle);
                log.info("Game over music loaded");
            }

            gameOverMusic.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            gameOverMusic.setLooping(true); // Loop game over music
            gameOverMusic.play();
            gameOverMusicActive = true;
            log.info("Game over music started (looping)");
        } catch (Exception e) {
            log.error("Failed to load game over music", e);
        }
    }

    /**
     * Stops and disposes game over music
     */
    public static void stopGameOverMusic() {
        if (gameOverMusic == null) {
            return;
        }

        gameOverMusicActive = false;

        if (gameOverMusic.isPlaying()) {
            gameOverMusic.stop();
        }
        gameOverMusic = null;
        log.info("Game over music stopped and disposed");
    }

    /**
     * Updates the volume of game over music based on settings
     */
    public static void updateGameOverMusicVolume() {
        if (gameOverMusic != null && gameOverMusicActive && gameOverMusic.isPlaying()) {
            gameOverMusic.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        }
    }

    /**
     * Disposes all audio resources managed by the audio manager.
     * This method should be called when the application is closing.
     */
    public static void dispose() {
        stopEndlessMusic();
        stopGameOverMusic();
        musicTracks.clear();
        soundEffects.clear();
        initialized = false;
        log.info("Audio manager disposed");
    }
}
