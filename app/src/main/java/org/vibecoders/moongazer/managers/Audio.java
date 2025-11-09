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
    private static Music stage1Music = null;
    private static Music stage2Music = null;
    private static Music stage3Music = null;
    private static Music stage4Music = null;
    private static Music stage5Music = null;
    private static Sound powerupReceive = null;
    private static Sound paddleHit = null;
    private static Sound brickHit = null;
    private static Sound ballLoss = null;


    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        menuMusic = Gdx.audio.newMusic(Assets.getAsset("audio/I Once Praised The Day.mp3", FileHandle.class));
        stage1Music = Gdx.audio.newMusic(Assets.getAsset("audio/stage1.mp3", FileHandle.class));
        stage2Music = Gdx.audio.newMusic(Assets.getAsset("audio/stage2.ogg", FileHandle.class));
        stage3Music = Gdx.audio.newMusic(Assets.getAsset("audio/stage3.ogg", FileHandle.class));
        stage4Music = Gdx.audio.newMusic(Assets.getAsset("audio/stage4.ogg", FileHandle.class));
        stage5Music = Gdx.audio.newMusic(Assets.getAsset("audio/stage5.ogg", FileHandle.class));
        selectSfx = Gdx.audio.newSound(Assets.getAsset("audio/sfx/select.mp3", FileHandle.class));
        returnSfx = Gdx.audio.newSound(Assets.getAsset("audio/sfx/return.mp3", FileHandle.class));
        confirmSfx = Gdx.audio.newSound(Assets.getAsset("audio/sfx/confirm.mp3", FileHandle.class));
        quitGameSfx = Gdx.audio.newSound(Assets.getAsset("audio/sfx/quit.mp3", FileHandle.class));
        powerupReceive = Gdx.audio.newSound(Assets.getAsset("audio/sfx/PowerUpReceived.mp3", FileHandle.class));
        paddleHit = Gdx.audio.newSound(Assets.getAsset("audio/sfx/PaddleHit.mp3", FileHandle.class));
        brickHit = Gdx.audio.newSound(Assets.getAsset("audio/sfx/BlockDamage.mp3", FileHandle.class));
        ballLoss = Gdx.audio.newSound(Assets.getAsset("audio/sfx/BallLoss.mp3", FileHandle.class));
        log.info("Audio manager initialized");
    }

    public static void menuMusicSetVolume() {
        menuMusic.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
    }

    public static void musicSetVolume() {
        menuMusicSetVolume();
        if (stage1Music != null && stage1Music.isPlaying()) {
            stage1Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        }
        if (stage2Music != null && stage2Music.isPlaying()) {
            stage2Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        }
        if (stage3Music != null && stage3Music.isPlaying()) {
            stage3Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        }
        if (stage4Music != null && stage4Music.isPlaying()) {
            stage4Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        }
        if (stage5Music != null && stage5Music.isPlaying()) {
            stage5Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        }
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

    public static void stage1MusicPlay() {
        if (stage1Music != null && !stage1Music.isPlaying()) {
            stage1Music.setLooping(true);
            stage1Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            stage1Music.play();
        }
    }

    public static void stage1MusicStop() {
        if (stage1Music != null && stage1Music.isPlaying()) {
            stage1Music.stop();
        }
    }

    public static void stage2MusicPlay() {
        if (stage2Music != null && !stage2Music.isPlaying()) {
            stage2Music.setLooping(true);
            stage2Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            stage2Music.play();
        }
    }

    public static void stage2MusicStop() {
        if (stage2Music != null && stage2Music.isPlaying()) {
            stage2Music.stop();
        }
    }

    public static void stage3MusicPlay() {
        if (stage3Music != null && !stage3Music.isPlaying()) {
            stage3Music.setLooping(true);
            stage3Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            stage3Music.play();
        }
    }

    public static void stage3MusicStop() {
        if (stage3Music != null && stage3Music.isPlaying()) {
            stage3Music.stop();
        }
    }

    public static void stage4MusicPlay() {
        if (stage4Music != null && !stage4Music.isPlaying()) {
            stage4Music.setLooping(true);
            stage4Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            stage4Music.play();
        }
    }

    public static void stage4MusicStop() {
        if (stage4Music != null && stage4Music.isPlaying()) {
            stage4Music.stop();
        }
    }

    public static void stage5MusicPlay() {
        if (stage5Music != null && !stage5Music.isPlaying()) {
            stage5Music.setLooping(true);
            stage5Music.setVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
            stage5Music.play();
        }
    }

    public static void stage5MusicStop() {
        if (stage5Music != null && stage5Music.isPlaying()) {
            stage5Music.stop();
    public static void playSfxPowerupReceive() {
        if (powerupReceive != null) {
            powerupReceive.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void playSfxPaddleHit() {
        if (paddleHit != null) {
            paddleHit.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void playSfxBrickHit() {
        if (brickHit != null) {
            brickHit.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void playSfxBallLoss() {
        if (ballLoss != null) {
            ballLoss.play(Settings.getSfxVolume() * Settings.getMasterVolume());
        }
    }

    public static void dispose() {
        if (menuMusic != null) {
            menuMusic.dispose();
            menuMusic = null;
        }
        if (stage1Music != null) {
            stage1Music.dispose();
            stage1Music = null;
        }
        if (stage2Music != null) {
            stage2Music.dispose();
            stage2Music = null;
        }
        if (stage3Music != null) {
            stage3Music.dispose();
            stage3Music = null;
        }
        if (stage4Music != null) {
            stage4Music.dispose();
            stage4Music = null;
        }
        if (stage5Music != null) {
            stage5Music.dispose();
            stage5Music = null;
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
        if (powerupReceive != null) {
            powerupReceive.dispose();
            powerupReceive = null;
        }
        if (paddleHit != null) {
            paddleHit.dispose();
            paddleHit = null;
        }
        if (brickHit != null) {
            brickHit.dispose();
            brickHit = null;
        }
        if (ballLoss != null) {
            ballLoss.dispose();
            ballLoss = null;
        }
        initialized = false;
        log.info("Audio manager disposed");
    }
}
