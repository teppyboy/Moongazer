package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;
import java.util.HashMap;
import java.util.Map;
import static org.vibecoders.moongazer.Constants.*;

public class PauseMenu {
    private static final Logger log = LoggerFactory.getLogger(PauseMenu.class);
    private enum FadeState { HIDDEN, FADING_IN, VISIBLE, FADING_OUT }
    private static final float FADE_DURATION = 0.25f;
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 80;
    private static final int BUTTON_SPACING = 65;
    private boolean isPaused = false;
    private UITextButton[] buttons;
    private Table menuTable;
    private Stage menuStage;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private Texture blurOverlay;
    private int currentChoice = -1;
    private HashMap<Integer, Long> currentKeyDown = new HashMap<>();
    private PauseMenuSettings settingsOverlay;
    private boolean pendingResume = false;
    private float fadeAlpha = 0f;
    private FadeState fadeState = FadeState.HIDDEN;
    private Runnable onResume;
    private Runnable onRestart;
    private Runnable onMainMenu;
    private Runnable onQuit;
    private Runnable onSaveGame;
    private boolean isStoryMode = false;

    private static class ButtonConfig {
        final String label;
        final Runnable action;
        ButtonConfig(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }

    /**
     * Constructs a new pause menu with all UI components.
     */
    public PauseMenu() {
        initUI();
        initSettingsOverlay();
    }

    /**
     * Sets whether the game is in story mode.
     * Rebuilds the menu to show/hide story-specific options like "Save Game".
     *
     * @param isStoryMode true if in story mode, false otherwise
     */
    public void setStoryMode(boolean isStoryMode) {
        if (this.isStoryMode != isStoryMode) {
            this.isStoryMode = isStoryMode;
            rebuildMenu();
        }
    }

    /**
     * Initializes the settings overlay component.
     */
    private void initSettingsOverlay() {
        settingsOverlay = new PauseMenuSettings();
        settingsOverlay.setOnClose(() -> {
            Gdx.input.setInputProcessor(menuStage);
            menuStage.setKeyboardFocus(menuTable);
        });
    }

    /**
     * Initializes the UI components including buttons and stage.
     */
    private void initUI() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        blurOverlay = new Texture(pixmap);
        pixmap.dispose();
        titleFont = Assets.getFont("ui", 40);
        buttonFont = Assets.getFont("ui", 24);
        menuStage = new Stage();
        menuTable = new Table();
        menuTable.setFillParent(true);
        menuStage.addActor(menuTable);
        ButtonConfig[] configs = createButtonConfigs();
        buttons = new UITextButton[configs.length];
        int centerX = WINDOW_WIDTH / 2 - BUTTON_WIDTH / 2;
        int startY = WINDOW_HEIGHT / 2 - BUTTON_HEIGHT / 2;
        if (isStoryMode) {
            startY += 40;
        }
        int currentY = startY;
        for (int i = 0; i < configs.length; i++) {
            buttons[i] = buildButton(configs[i], centerX, currentY);
            currentY -= BUTTON_SPACING;
        }
        initKeyboardHandling();
    }

    /**
     * Creates button configurations based on current game mode.
     *
     * @return array of button configurations
     */
    private ButtonConfig[] createButtonConfigs() {
        java.util.List<ButtonConfig> configs = new java.util.ArrayList<>();

        configs.add(new ButtonConfig("Resume", () -> {
            log.debug("Resume clicked");
            Audio.playSfxConfirm();
            resume();
        }));

        configs.add(new ButtonConfig("Restart", () -> {
            log.debug("Restart clicked");
            Audio.playSfxConfirm();
            if (onRestart != null) {
                resume();
                onRestart.run();
            }
        }));
        if (isStoryMode) {
            configs.add(new ButtonConfig("Save Game", () -> {
                log.debug("Save Game clicked");
                Audio.playSfxConfirm();
                if (onSaveGame != null) {
                    onSaveGame.run();
                }
            }));
        }

        configs.add(new ButtonConfig("Settings", () -> {
            log.debug("Settings clicked from pause menu");
            Audio.playSfxSelect();
            settingsOverlay.open();
        }));

        configs.add(new ButtonConfig("Main Menu", () -> {
            log.debug("Main menu clicked");
            Audio.playSfxConfirm();
            if (onMainMenu != null) {
                resume();
                onMainMenu.run();
            }
        }));

        configs.add(new ButtonConfig("Quit Game", () -> {
            log.debug("Quit clicked");
            Audio.playSfxQuitGame();
            if (onQuit != null) {
                onQuit.run();
            } else {
                Gdx.app.exit();
            }
        }));

        return configs.toArray(new ButtonConfig[0]);
    }

    /**
     * Rebuilds the menu with updated button configurations.
     * Called when switching between story and endless modes.
     */
    private void rebuildMenu() {
        if (buttons != null) {
            for (UITextButton button : buttons) {
                button.getActor().remove();
            }
        }
        menuTable.clear();
        ButtonConfig[] configs = createButtonConfigs();
        buttons = new UITextButton[configs.length];
        int centerX = WINDOW_WIDTH / 2 - BUTTON_WIDTH / 2;
        int startY = WINDOW_HEIGHT / 2 - BUTTON_HEIGHT / 2;
        if (isStoryMode) {
            startY += 40;
        }
        int currentY = startY;
        for (int i = 0; i < configs.length; i++) {
            buttons[i] = buildButton(configs[i], centerX, currentY);
            currentY -= BUTTON_SPACING;
        }
        initKeyboardHandling();
    }

    /**
     * Builds a single button with the specified configuration.
     *
     * @param config the button configuration
     * @param centerX the x-coordinate for button center
     * @param yPosition the y-coordinate for button position
     * @return the created button
     */
    private UITextButton buildButton(ButtonConfig config, int centerX, int yPosition) {
        UITextButton button = new UITextButton(config.label, buttonFont);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setPosition(centerX, yPosition);
        button.onClick(config.action);
        menuTable.addActor(button.getActor());
        return button;
    }

    /**
     * Initializes keyboard navigation and input handling for the menu.
     */
    private void initKeyboardHandling() {
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].onHoverEnter(() -> {
                if (currentChoice != index) {
                    if (currentChoice != -1) {
                        buttons[currentChoice].hoverExit();
                    }
                    currentChoice = index;
                }
            });
            buttons[i].onHoverExit(() -> {
                if (currentChoice == index) {
                    currentChoice = -1;
                }
            });
        }
        menuStage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                handleKeyDown(keycode);
                currentKeyDown.put(keycode, TimeUtils.millis());
                return true;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                currentKeyDown.remove(keycode);
                return true;
            }
        });
        menuStage.setKeyboardFocus(menuTable);
    }

    /**
     * Handles keyboard input for menu navigation and selection.
     *
     * @param keycode the key code of the pressed key
     */
    private void handleKeyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                Audio.playSfxSelect();
                currentChoice = (currentChoice - 1 + buttons.length) % buttons.length;
                break;
            case Input.Keys.DOWN:
                Audio.playSfxSelect();
                currentChoice = (currentChoice + 1) % buttons.length;
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.ENTER:
                if (currentChoice != -1) {
                    buttons[currentChoice].click();
                }
                break;
            case Input.Keys.ESCAPE:
                Audio.playSfxReturn();
                resume();
                break;
            default:
                break;
        }
        if (currentChoice != -1) {
            for (int i = 0; i < buttons.length; i++) {
                if (i == currentChoice) {
                    buttons[i].hoverEnter();
                } else {
                    buttons[i].hoverExit();
                }
            }
        }
    }

    /**
     * Displays the pause menu with fade-in animation.
     */
    public void pause() {
        if (!isPaused) {
            isPaused = true;
            currentChoice = -1;
            fadeAlpha = 0f;
            fadeState = FadeState.FADING_IN;
            Gdx.input.setInputProcessor(menuStage);
            menuStage.setKeyboardFocus(menuTable);
            log.info("Game paused");
        } else if (fadeState == FadeState.FADING_OUT) {
            fadeState = FadeState.FADING_IN;
        }
    }

    /**
     * Initiates the resume process with fade-out animation.
     */
    public void resume() {
        if (isPaused && fadeState != FadeState.FADING_OUT) {
            pendingResume = true;
        }
    }

    /**
     * Immediately closes the pause menu without animation.
     * Restores the input processor via the onResume callback.
     */
    public void forceClose() {
        if (isPaused) {
            isPaused = false;
            fadeState = FadeState.HIDDEN;
            fadeAlpha = 0f;
            currentChoice = -1;
            currentKeyDown.clear();
            pendingResume = false;
            if (menuStage != null && menuStage.getRoot() != null) {
                menuStage.getRoot().getColor().a = 1f;
            }
            // Trigger the onResume callback to restore input processor
            if (onResume != null) {
                onResume.run();
            }
            log.info("Pause menu force closed");
        }
    }

    /**
     * Processes the resume operation and starts fade-out animation.
     */
    private void processResume() {
        if (!isPaused || fadeState == FadeState.FADING_OUT) {
            return;
        }
        currentChoice = -1;
        currentKeyDown.clear();
        fadeState = FadeState.FADING_OUT;
        log.info("Game resuming (fading out)");
    }

    /**
     * Checks if the game is currently paused.
     *
     * @return true if paused, false otherwise
     */
    public boolean isPaused() {
        return isPaused;
    }

    /**
     * Renders the pause menu with the game snapshot background.
     *
     * @param batch the sprite batch for rendering
     * @param gameSnapshot the snapshot texture of the game state
     */
    public void render(SpriteBatch batch, Texture gameSnapshot) {
        if (!isPaused)
            return;
        float delta = Gdx.graphics.getDeltaTime();
        if (pendingResume) {
            pendingResume = false;
            processResume();
        }
        updateFade(delta);
        if (!isPaused)
            return;
        if (!settingsOverlay.isOpen()) {
            for (Map.Entry<Integer, Long> entry : currentKeyDown.entrySet()) {
                Integer keyCode = entry.getKey();
                Long timeStamp = entry.getValue();
                if (TimeUtils.millis() - timeStamp > 150) {
                    handleKeyDown(keyCode);
                    currentKeyDown.put(keyCode, TimeUtils.millis());
                }
            }
        }
        if (gameSnapshot != null) {
            batch.draw(gameSnapshot, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, 0, 1, 1);
        }
        if (fadeAlpha <= 0f) {
            return;
        }
        batch.setColor(1f, 1f, 1f, fadeAlpha);
        batch.draw(blurOverlay, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.setColor(Color.WHITE);
        if (settingsOverlay.isOpen()) {
            settingsOverlay.render(batch, fadeAlpha);
            return;
        }
        String pausedText = "PAUSED";
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        layout.setText(titleFont, pausedText);
        float titleX = (WINDOW_WIDTH - layout.width) / 2f;
        float titleY = WINDOW_HEIGHT / 2f + 200;
        Color originalColor = titleFont.getColor().cpy();
        titleFont.setColor(1f, 1f, 1f, fadeAlpha);
        titleFont.draw(batch, pausedText, titleX, titleY);
        titleFont.setColor(originalColor);
        batch.end();
        if (menuStage != null && menuStage.getRoot() != null) {
            menuStage.getRoot().getColor().a = fadeAlpha;
        }
        menuStage.act(delta);
        menuStage.draw();
        batch.begin();
    }

    /**
     * Updates the fade animation state.
     *
     * @param delta the time delta since last frame
     */
    private void updateFade(float delta) {
        switch (fadeState) {
            case FADING_IN:
                fadeAlpha = Math.min(1f, fadeAlpha + delta / FADE_DURATION);
                if (fadeAlpha >= 1f) {
                    fadeAlpha = 1f;
                    fadeState = FadeState.VISIBLE;
                }
                break;
            case FADING_OUT:
                fadeAlpha = Math.max(0f, fadeAlpha - delta / FADE_DURATION);
                if (fadeAlpha <= 0f) {
                    completeFadeOut();
                }
                break;
            case VISIBLE:
                fadeAlpha = 1f;
                break;
            case HIDDEN:
            default:
                fadeAlpha = 0f;
                break;
        }
    }

    /**
     * Completes the fade-out animation and fully resumes the game.
     */
    private void completeFadeOut() {
        fadeAlpha = 0f;
        fadeState = FadeState.HIDDEN;
        isPaused = false;
        currentKeyDown.clear();
        if (menuStage != null && menuStage.getRoot() != null) {
            menuStage.getRoot().getColor().a = 1f;
        }
        if (onResume != null) {
            onResume.run();
        }
        log.info("Game resumed");
    }

    /**
     * Sets the callback to be executed when resuming the game.
     *
     * @param onResume the callback runnable
     */
    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    /**
     * Sets the callback to be executed when restarting the game.
     *
     * @param onRestart the callback runnable
     */
    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    /**
     * Sets the callback to be executed when returning to main menu.
     *
     * @param onMainMenu the callback runnable
     */
    public void setOnMainMenu(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu;
    }

    /**
     * Sets the callback to be executed when quitting the game.
     *
     * @param onQuit the callback runnable
     */
    public void setOnQuit(Runnable onQuit) {
        this.onQuit = onQuit;
    }

    /**
     * Sets the callback to be executed when saving the game.
     *
     * @param onSaveGame the callback runnable
     */
    public void setOnSaveGame(Runnable onSaveGame) {
        this.onSaveGame = onSaveGame;
    }

    /**
     * Disposes of all resources used by the pause menu.
     */
    public void dispose() {
        if (blurOverlay != null) {
            blurOverlay.dispose();
        }
        if (menuStage != null) {
            menuStage.dispose();
        }
        if (settingsOverlay != null) {
            settingsOverlay.dispose();
        }
    }
}
