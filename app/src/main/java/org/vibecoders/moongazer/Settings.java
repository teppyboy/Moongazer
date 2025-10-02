package org.vibecoders.moongazer;

import com.badlogic.gdx.Input;

import java.util.HashMap;

import org.slf4j.Logger;

public class Settings {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Settings.class);
    private static float masterVolume = 1.0f;
    private static float musicVolume = 1.0f;
    private static float sfxVolume = 1.0f;
    public static HashMap<String, Integer> keybinds = new HashMap<>() {
        {
            put("p1_left", Input.Keys.LEFT);
            put("p1_right", Input.Keys.RIGHT);
            put("p2_left", Input.Keys.A);
            put("p2_right", Input.Keys.D);
        }
    };

    public static int getKeybind(String action) {
        return keybinds.getOrDefault(action, Input.Keys.UNKNOWN);
    }

    public static float getMasterVolume() {
        return masterVolume;
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static float getSfxVolume() {
        return sfxVolume;
    }

    public static void setKeybind(String action, int keycode) {
        keybinds.put(action, keycode);
    }

    public static void setMasterVolume(float volume) {
        masterVolume = Math.max(0, Math.min(1, volume));
    }

    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0, Math.min(1, volume));
    }

    public static void setSfxVolume(float volume) {
        sfxVolume = Math.max(0, Math.min(1, volume));
    }

    public static void saveSettings() {
        log.debug("Saving settings: Master Volume = {}, Music Volume = {}, SFX Volume = {}", masterVolume, musicVolume, sfxVolume);
        for (java.util.Map.Entry<String, Integer> entry : keybinds.entrySet()) {
            log.debug("Keybind: {} = {}", entry.getKey(), Input.Keys.toString(entry.getValue()));
        }
    }

    public static void loadSettings() {
        
    }
}
