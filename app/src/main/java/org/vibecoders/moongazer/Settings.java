package org.vibecoders.moongazer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import java.util.HashMap;
import org.slf4j.Logger;

public class Settings {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Settings.class);
    private static final String SETTINGS_FILE = "settings.json";
    private static float masterVolume = 1.0f;
    private static float musicVolume = 1.0f;
    private static float sfxVolume = 1.0f;
    public static HashMap<String, Integer> keybinds = new HashMap<>() {{
        put("p1_left", Input.Keys.LEFT);
        put("p1_right", Input.Keys.RIGHT);
    }};

    public static class SettingsData {
        public float masterVolume;
        public float musicVolume;
        public float sfxVolume;
        public HashMap<String, Integer> keybinds;
    }

    public static int getKeybind(String action) { return keybinds.getOrDefault(action, Input.Keys.UNKNOWN); }
    public static float getMasterVolume() { return masterVolume; }
    public static float getMusicVolume() { return musicVolume; }
    public static float getSfxVolume() { return sfxVolume; }
    public static void setKeybind(String action, int keycode) { keybinds.put(action, keycode); }
    public static void setMasterVolume(float volume) { masterVolume = Math.max(0, Math.min(1, volume)); }
    public static void setMusicVolume(float volume) { musicVolume = Math.max(0, Math.min(1, volume)); }
    public static void setSfxVolume(float volume) { sfxVolume = Math.max(0, Math.min(1, volume)); }

    /**
     * Saves the current settings to a JSON file.
     */
    public static void saveSettings() {
        try {
            SettingsData data = new SettingsData();
            data.masterVolume = masterVolume;
            data.musicVolume = musicVolume;
            data.sfxVolume = sfxVolume;
            data.keybinds = new HashMap<>(keybinds);
            Json json = new Json();
            json.setOutputType(JsonWriter.OutputType.json);
            json.setUsePrototypes(false);
            FileHandle file = Gdx.files.local(SETTINGS_FILE);
            String jsonString = json.prettyPrint(data);
            file.writeString(jsonString, false);
            log.info("Settings saved to {}", SETTINGS_FILE);
            log.debug("Master Volume = {}, Music Volume = {}, SFX Volume = {}", masterVolume, musicVolume, sfxVolume);
        } catch (Exception e) {
            log.error("Failed to save settings", e);
        }
    }

    /**
     * Loads settings from a JSON file. If the file does not exist or is invalid, defaults are used.
     */
    public static void loadSettings() {
        try {
            FileHandle file = Gdx.files.local(SETTINGS_FILE);
            if (!file.exists()) {
                log.info("Settings file not found, using defaults");
                return;
            }
            Json json = new Json();
            String jsonString = file.readString();
            SettingsData data = json.fromJson(SettingsData.class, jsonString);
            if (data != null) {
                masterVolume = data.masterVolume;
                musicVolume = data.musicVolume;
                sfxVolume = data.sfxVolume;
                if (data.keybinds != null && !data.keybinds.isEmpty()) {
                    keybinds.clear();
                    keybinds.putAll(data.keybinds);
                }
                log.info("Settings loaded from {}", SETTINGS_FILE);
                log.debug("Master Volume = {}, Music Volume = {}, SFX Volume = {}", masterVolume, musicVolume, sfxVolume);
            }
        } catch (Exception e) {
            log.error("Failed to load settings, using defaults", e);
        }
    }
}
