package org.vibecoders.moongazer.managers;

import org.slf4j.Logger;
import org.vibecoders.moongazer.Settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;
import java.util.List;

public class Audio {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Audio.class);
    private static boolean initialized = false;
    private static Music menuMusic = null;
    private static Sound selectSfx = null;
    private static Sound returnSfx = null;
    private static Sound confirmSfx = null;
    private static Sound quitGameSfx = null;

    // Endless mode music playlist
    private static List<Music> endlessMusicPlaylist = null;
    private static int currentEndlessTrackIndex = 0;
    private static boolean endlessMusicActive = false;

    // Game over music
    private static Music gameOverMusic = null;
    private static boolean gameOverMusicActive = false;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        menuMusic = Gdx.audio.newMusic(Assets.getAsset("audio/I Once Praised The Day.mp3", FileHandle.class));
        selectSfx = Gdx.audio.newSound(Assets.getAsset("audio/sfx/select.mp3", FileHandle.class));
        returnSfx = Gdx.audio.newSound(Assets.getAsset("audio/sfx/return.mp3", FileHandle.class));
        confirmSfx = Gdx.audio.newSound(Assets.getAsset("audio/sfx/confirm.mp3", FileHandle.class));
        quitGameSfx = Gdx.audio.newSound(Assets.getAsset("audio/sfx/quit.mp3", FileHandle.class));
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

    /**
     * Initializes and starts the endless mode music playlist
     */
    public static void initEndlessMusic() {
        if (endlessMusicPlaylist != null) {
            log.info("Endless music playlist already initialized");
            return; // Already initialized
        }

        log.info("=== Initializing endless mode music playlist ===");
        endlessMusicPlaylist = new ArrayList<>();

        // Track base names (without extension)
        String[] trackNames = {
            "endlessost1",
            "endlessost2",
            "endlessost3",
            "endlessost4",
            "endlessost5"
        };

        for (String trackName : trackNames) {
            // Try OGG first (better LibGDX support), then MP3
            String[] extensions = {".ogg", ".mp3"};
            boolean loaded = false;

            for (String ext : extensions) {
                try {
                    String fullPath = "audio/endlessost/" + trackName + ext;
                    log.info("Trying to load: {}", fullPath);

                    FileHandle fileHandle = Assets.getAsset(fullPath, FileHandle.class);

                    // Log file info
                    log.info("FileHandle info - exists: {}, length: {} bytes",
                             fileHandle.exists(), fileHandle.length());

                    if (fileHandle.length() == 0) {
                        log.error("File is empty (0 bytes): {}", fullPath);
                        continue;
                    }

                    Music music = Gdx.audio.newMusic(fileHandle);
                    endlessMusicPlaylist.add(music);
                    log.info("✓✓✓ Successfully loaded: {}{}", trackName, ext);
                    loaded = true;
                    break; // Successfully loaded, no need to try other formats

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

        if (endlessMusicPlaylist.isEmpty()) {
            log.error("!!! NO ENDLESS MUSIC TRACKS WERE LOADED !!!");
            log.error("═══════════════════════════════════════════════════════════");
            log.error("SOLUTION: Convert your MP3 files to OGG format");
            log.error("═══════════════════════════════════════════════════════════");
            log.error("Option 1 - Using Audacity (Free GUI tool):");
            log.error("  1. Download from: https://www.audacityteam.org/");
            log.error("  2. Open your MP3 file");
            log.error("  3. File > Export > Export as OGG Vorbis");
            log.error("  4. Save to: app/src/main/resources/audio/endlessost/");
            log.error("");
            log.error("Option 2 - Using FFmpeg (Command line):");
            log.error("  ffmpeg -i endlessost1.mp3 -c:a libvorbis -q:a 5 endlessost1.ogg");
            log.error("═══════════════════════════════════════════════════════════");
        } else {
            log.info("✓✓✓ Endless playlist loaded with {} tracks", endlessMusicPlaylist.size());
        }
    }

    /**
     * Starts playing the endless mode music playlist
     */
    public static void startEndlessMusic() {
        log.info("=== Starting Endless Music ===");

        if (endlessMusicPlaylist == null) {
            log.info("Playlist is null, initializing...");
            initEndlessMusic();
        }

        if (endlessMusicPlaylist == null || endlessMusicPlaylist.isEmpty()) {
            log.error("Cannot start endless music - playlist is empty or null!");
            return;
        }

        // Stop menu music
        log.info("Stopping menu music...");
        menuMusicStop();

        currentEndlessTrackIndex = 0;
        endlessMusicActive = true;

        log.info("Playing first track from playlist of {} tracks", endlessMusicPlaylist.size());
        playCurrentEndlessTrack();
        log.info("Endless mode music started successfully!");
    }

    /**
     * Plays the current track in the endless playlist
     */
    private static void playCurrentEndlessTrack() {
        if (endlessMusicPlaylist == null || endlessMusicPlaylist.isEmpty() || !endlessMusicActive) {
            log.warn("Cannot play track - playlist null/empty or not active");
            return;
        }

        log.info("Playing endless track {} of {}", currentEndlessTrackIndex + 1, endlessMusicPlaylist.size());

        Music currentTrack = endlessMusicPlaylist.get(currentEndlessTrackIndex);
        float volume = Settings.getMusicVolume() * Settings.getMasterVolume();
        currentTrack.setVolume(volume);
        currentTrack.setLooping(false);

        log.info("Track volume set to: {}", volume);

        // Set completion listener to play next track
        currentTrack.setOnCompletionListener(music -> {
            if (endlessMusicActive) {
                currentEndlessTrackIndex = (currentEndlessTrackIndex + 1) % endlessMusicPlaylist.size();
                log.info("Track completed, switching to track {}", currentEndlessTrackIndex + 1);
                playCurrentEndlessTrack();
            }
        });

        currentTrack.play();
        log.info("✓ Now playing endless track {}", currentEndlessTrackIndex + 1);
    }

    /**
     * Updates the volume of endless music based on settings
     */
    public static void updateEndlessMusicVolume() {
        if (endlessMusicPlaylist != null && endlessMusicActive) {
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

        // Stop all tracks
        for (Music music : endlessMusicPlaylist) {
            if (music.isPlaying()) {
                music.stop();
            }
            music.dispose();
        }

        endlessMusicPlaylist.clear();
        endlessMusicPlaylist = null;
        currentEndlessTrackIndex = 0;
        log.info("Endless mode music stopped and disposed");
    }

    /**
     * Updates music volume for both menu and endless music
     */
    public static void updateAllMusicVolume() {
        menuMusicSetVolume();
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
        gameOverMusic.dispose();
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

    public static void dispose() {
        // Dispose endless music
        stopEndlessMusic();

        // Dispose game over music
        stopGameOverMusic();

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
