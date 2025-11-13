package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

/**
 * Game Win Menu for Story Mode
 * Displays score, max combo, and stars earned based on remaining lives
 */
public class GameWinMenu {
    private static final Logger log = LoggerFactory.getLogger(GameWinMenu.class);
    private enum FadeState { HIDDEN, FADING_IN, VISIBLE }
    private static final float FADE_DURATION = 0.4f;
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 80;
    private static final int BUTTON_SPACING = 65;
    private boolean isVisible = false;
    private UITextButton[] buttons;
    private Table menuTable;
    private Stage menuStage;
    private BitmapFont titleFont;
    private BitmapFont scoreFont;
    private BitmapFont buttonFont;
    private Texture darkOverlay;
    private Texture starTexture;
    private int currentChoice = -1;
    private HashMap<Integer, Long> currentKeyDown = new HashMap<>();
    private float fadeAlpha = 0f;
    private FadeState fadeState = FadeState.HIDDEN;
    private GlyphLayout glyphLayout;

    // Game stats
    private int totalScore = 0;
    private int maxCombo = 0;
    private int starsEarned = 0;
    private int remainingLives = 0;

    // Callbacks
    private Runnable onContinue;
    private Runnable onMainMenu;
    private Runnable onQuit;

    private static class ButtonConfig {
        final String label;
        final Runnable action;
        ButtonConfig(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }
    }

    /**
     * Constructs a new game win menu with all UI components.
     */
    public GameWinMenu() {
        initUI();
    }

    /**
     * Initializes the UI components including buttons, fonts, and stage.
     */
    private void initUI() {
        // Create dark overlay
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.85f);
        pixmap.fill();
        darkOverlay = new Texture(pixmap);
        pixmap.dispose();

        // Load fonts
        titleFont = Assets.getFont("ui", 48);
        scoreFont = Assets.getFont("ui", 32);
        buttonFont = Assets.getFont("ui", 24);
        glyphLayout = new GlyphLayout();

        // Load star texture
        starTexture = Assets.getAsset("textures/ui/UI_Icon_Tower_Star.png", Texture.class);

        // Setup stage
        menuStage = new Stage();
        menuTable = new Table();
        menuTable.setFillParent(true);
        menuStage.addActor(menuTable);

        ButtonConfig[] configs = createButtonConfigs();
        buttons = new UITextButton[configs.length];

        int centerX = WINDOW_WIDTH / 2 - BUTTON_WIDTH / 2;
        int startY = WINDOW_HEIGHT / 2 - 200;

        for (int i = 0; i < configs.length; i++) {
            buttons[i] = buildButton(configs[i], i, centerX, startY);
        }

        initKeyboardHandling();
    }

    /**
     * Creates button configurations for the game win menu.
     *
     * @return array of button configurations
     */
    private ButtonConfig[] createButtonConfigs() {
        return new ButtonConfig[]{
                new ButtonConfig("Continue", () -> {
                    log.debug("Continue clicked");
                    Audio.playSfxConfirm();
                    if (onContinue != null) {
                        hide();
                        onContinue.run();
                    }
                }),
                new ButtonConfig("Main Menu", () -> {
                    log.debug("Main Menu clicked");
                    Audio.playSfxConfirm();
                    if (onMainMenu != null) {
                        hide();
                        onMainMenu.run();
                    }
                }),
                new ButtonConfig("Quit Game", () -> {
                    log.debug("Quit Game clicked");
                    Audio.playSfxQuitGame();
                    if (onQuit != null) {
                        onQuit.run();
                    } else {
                        Gdx.app.exit();
                    }
                })
        };
    }

    /**
     * Builds a single button with the specified configuration.
     *
     * @param config the button configuration
     * @param index the button index
     * @param centerX the x-coordinate for button center
     * @param startY the starting y-coordinate
     * @return the created button
     */
    private UITextButton buildButton(ButtonConfig config, int index, int centerX, int startY) {
        UITextButton button = new UITextButton(config.label, buttonFont);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setPosition(centerX, startY - BUTTON_SPACING * index);
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
     * Show the game win menu with stats.
     *
     * @param finalScore Total score achieved
     * @param maxCombo Maximum combo achieved
     * @param remainingLives Lives remaining (used to calculate stars)
     */
    public void show(int finalScore, int maxCombo, int remainingLives) {
        if (!isVisible) {
            this.totalScore = finalScore;
            this.maxCombo = maxCombo;
            this.remainingLives = remainingLives;
            this.starsEarned = calculateStars(remainingLives);

            isVisible = true;
            currentChoice = 0;
            buttons[currentChoice].hoverEnter();
            fadeAlpha = 0f;
            fadeState = FadeState.FADING_IN;
            Gdx.input.setInputProcessor(menuStage);
            menuStage.setKeyboardFocus(menuTable);

            log.info("Level Complete! Score: {}, Max Combo: {}, Lives: {}, Stars: {}",
                     totalScore, maxCombo, remainingLives, starsEarned);
        }
    }

    /**
     * Calculate stars based on remaining lives.
     * 3 lives = 3 stars, 2 lives = 2 stars, 1 life = 1 star, 0 lives = 0 stars
     *
     * @param lives the number of remaining lives
     * @return the number of stars earned (0-3)
     */
    private int calculateStars(int lives) {
        return Math.max(0, Math.min(3, lives));
    }

    /**
     * Hides the game win menu.
     */
    private void hide() {
        if (isVisible) {
            isVisible = false;
            currentChoice = -1;
            currentKeyDown.clear();
            fadeState = FadeState.HIDDEN;
            fadeAlpha = 0f;
            log.info("Game Win menu hidden");
        }
    }

    /**
     * Checks if the game win menu is currently visible.
     *
     * @return true if visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Renders the game win menu with stats, stars, and buttons.
     *
     * @param batch the sprite batch for rendering
     * @param gameSnapshot the snapshot texture of the game state
     */
    public void render(SpriteBatch batch, Texture gameSnapshot) {
        if (!isVisible) {
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        updateFade(delta);

        // Handle key repeat
        for (Map.Entry<Integer, Long> entry : new HashMap<>(currentKeyDown).entrySet()) {
            Integer keyCode = entry.getKey();
            Long timeStamp = entry.getValue();
            if (TimeUtils.millis() - timeStamp > 100) {
                handleKeyDown(keyCode);
                currentKeyDown.put(keyCode, TimeUtils.millis());
            }
        }

        // Draw game snapshot
        if (gameSnapshot != null) {
            batch.draw(gameSnapshot, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, 0, 1, 1);
        }

        if (fadeAlpha <= 0f) {
            return;
        }

        // Draw dark overlay
        batch.setColor(1f, 1f, 1f, fadeAlpha);
        batch.draw(darkOverlay, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.setColor(Color.WHITE);

        // Draw "STAGE COMPLETE" title
        String titleText = "STAGE COMPLETE";
        glyphLayout.setText(titleFont, titleText);
        float titleX = (WINDOW_WIDTH - glyphLayout.width) / 2f;
        float titleY = WINDOW_HEIGHT / 2f + 320;

        Color originalTitleColor = titleFont.getColor().cpy();
        titleFont.setColor(0.3f, 1f, 0.3f, fadeAlpha); // Green tint for victory
        titleFont.draw(batch, titleText, titleX, titleY);
        titleFont.setColor(originalTitleColor);

        // Draw stars
        drawStars(batch);

        // Draw score and combo
        String totalScoreText = "Score: " + totalScore;
        String comboText = "Max Combo: " + maxCombo;

        glyphLayout.setText(scoreFont, totalScoreText);
        float totalScoreX = (WINDOW_WIDTH - glyphLayout.width) / 2f;
        float totalScoreY = WINDOW_HEIGHT / 2f + 150;

        glyphLayout.setText(scoreFont, comboText);
        float comboX = (WINDOW_WIDTH - glyphLayout.width) / 2f;
        float comboY = WINDOW_HEIGHT / 2f + 100;

        Color originalScoreColor = scoreFont.getColor().cpy();
        scoreFont.setColor(1f, 1f, 1f, fadeAlpha);
        scoreFont.draw(batch, totalScoreText, totalScoreX, totalScoreY);
        scoreFont.draw(batch, comboText, comboX, comboY);
        scoreFont.setColor(originalScoreColor);

        // Draw buttons
        batch.end();
        if (menuStage != null && menuStage.getRoot() != null) {
            menuStage.getRoot().getColor().a = fadeAlpha;
        }
        menuStage.act(delta);
        menuStage.draw();
        batch.begin();
    }

    /**
     * Draws stars based on starsEarned with golden color for earned stars.
     *
     * @param batch the sprite batch for rendering
     */
    private void drawStars(SpriteBatch batch) {
        if (starTexture == null) {
            return;
        }

        float starSize = 64;
        float starSpacing = 80;
        float totalStarsWidth = 3 * starSize + 2 * starSpacing;
        float startX = (WINDOW_WIDTH - totalStarsWidth) / 2f;
        float starY = WINDOW_HEIGHT / 2f + 210;

        Color originalColor = batch.getColor().cpy();

        for (int i = 0; i < 3; i++) {
            float starX = startX + i * (starSize + starSpacing);

            if (i < starsEarned) {
                // Draw filled star with golden color
                batch.setColor(1f, 0.84f, 0f, fadeAlpha);
            } else {
                // Draw empty/grayed out star
                batch.setColor(0.3f, 0.3f, 0.3f, fadeAlpha * 0.5f);
            }

            batch.draw(starTexture, starX, starY, starSize, starSize);
        }

        batch.setColor(originalColor);
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
                    fadeState = FadeState.VISIBLE;
                }
                break;
            case VISIBLE:
                fadeAlpha = 1f;
                break;
            case HIDDEN:
                fadeAlpha = 0f;
                break;
        }
    }

    /**
     * Sets the callback to be executed when continue is selected.
     *
     * @param callback the callback runnable
     */
    public void setOnContinue(Runnable callback) {
        this.onContinue = callback;
    }

    /**
     * Sets the callback to be executed when main menu is selected.
     *
     * @param callback the callback runnable
     */
    public void setOnMainMenu(Runnable callback) {
        this.onMainMenu = callback;
    }

    /**
     * Sets the callback to be executed when quit is selected.
     *
     * @param callback the callback runnable
     */
    public void setOnQuit(Runnable callback) {
        this.onQuit = callback;
    }

    /**
     * Disposes of all resources used by the game win menu.
     */
    public void dispose() {
        if (darkOverlay != null) {
            darkOverlay.dispose();
        }
        if (menuStage != null) {
            menuStage.dispose();
        }
    }
}

