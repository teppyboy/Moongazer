package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

/**
 * Main menu scene with fade-in effects and menu options.
 */
public class MainMenu extends Scene {
    // Menu options
    private Label logoLabel;
    private Label newGameLabel;
    private Label loadGameLabel;
    private Label settingsLabel;
    private Label quitGameLabel;

    // Background and logo textures
    private Texture backgroundTexture;
    private Texture logoTexture;
    private Texture titleTexture;

    // Timing and effects
    private float elapsedTime = 0f;
    private static final float BACKGROUND_FADE_START = 2.0f; // Background visible for 2 seconds
    private static final float BACKGROUND_FADE_DURATION = 1.0f; // 1 second fade
    private static final float MENU_FADE_START = 2.5f; // Menu starts fading in after 2.5 seconds
    private static final float MENU_FADE_DURATION = 1.5f; // 1.5 seconds to fully appear

    // Colors
    private final Color backgroundAlpha = new Color(1, 1, 1, 0); // Start with transparent background
    private final Color menuAlpha = new Color(Color.BLACK);

    // Selected menu index
    private int selectedIndex = 0;
    private static final int MENU_COUNT = 4;

    public MainMenu() {
        super();
        loadBackgroundTexture();
        loadLogoTexture();
        loadTitleTexture();
        initializeLabels();
        setupMenuPositions();
    }

    private void loadBackgroundTexture() {
        try {
            log.info("Attempting to load background texture: main menu/background.png");
            backgroundTexture = Assets.getAsset("main menu/background.png", Texture.class);
            log.info("Successfully loaded background texture: {}x{}", backgroundTexture.getWidth(), backgroundTexture.getHeight());
        } catch (Exception e) {
            log.error("Could not load background texture, using fallback", e);
            backgroundTexture = TEXTURE_WHITE;
        }
    }

    private void loadLogoTexture() {
        try {
            log.info("Attempting to load logo texture: icons/logo.png");
            logoTexture = Assets.getAsset("icons/logo.png", Texture.class);
            log.info("Successfully loaded logo texture: {}x{}", logoTexture.getWidth(), logoTexture.getHeight());
        } catch (Exception e) {
            log.error("Could not load logo texture, using fallback", e);
            logoTexture = TEXTURE_WHITE;
        }
    }

    public void loadTitleTexture() {
        try {
            log.info("Attempting to load title texture: main menu/title.png");
            titleTexture = Assets.getAsset("main menu/title.png", Texture.class);
            log.info("Successfully loaded title texture: {}x{}", titleTexture.getWidth(), titleTexture.getHeight());
        } catch (Exception e) {
            log.error("Could not load title texture, using fallback", e);
            titleTexture = TEXTURE_WHITE;
        }
    }

    private void initializeLabels() {
        var logoFont = Assets.getFont("ui", 48);
        var menuFont = Assets.getFont("ui", 24);

        logoLabel = new Label("Moongazer", new LabelStyle(logoFont, Color.BLACK));
        newGameLabel = new Label("New Game", new LabelStyle(menuFont, Color.BLACK));
        loadGameLabel = new Label("Load Game", new LabelStyle(menuFont, Color.BLACK));
        settingsLabel = new Label("Settings", new LabelStyle(menuFont, Color.BLACK));
        quitGameLabel = new Label("Quit Game", new LabelStyle(menuFont, Color.BLACK));

        // Initially transparent
        menuAlpha.a = 0f;
    }

    private void setupMenuPositions() {
        float centerX = WINDOW_WIDTH / 2f;
        float startY = WINDOW_HEIGHT * 0.6f;
        float menuSpacing = 60f;

        // Logo at top
        logoLabel.setPosition(centerX - logoLabel.getWidth() / 2f, WINDOW_HEIGHT * 0.8f);

        // Menu options centered
        newGameLabel.setPosition(centerX - newGameLabel.getWidth() / 2f, startY);
        loadGameLabel.setPosition(centerX - loadGameLabel.getWidth() / 2f, startY - menuSpacing);
        settingsLabel.setPosition(centerX - settingsLabel.getWidth() / 2f, startY - menuSpacing * 2);
        quitGameLabel.setPosition(centerX - quitGameLabel.getWidth() / 2f, startY - menuSpacing * 3);
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;

        // Handle background fade in from start
        if (elapsedTime < BACKGROUND_FADE_DURATION) {
            // Fade in background from transparent to opaque (0 to 1 second)
            backgroundAlpha.a = elapsedTime / BACKGROUND_FADE_DURATION;
        } else if (elapsedTime < BACKGROUND_FADE_START) {
            // Keep background fully visible (1 to 2 seconds)
            backgroundAlpha.a = 1f;
        } else if (elapsedTime < BACKGROUND_FADE_START + BACKGROUND_FADE_DURATION) {
            // Fade background to a brighter level for menu (2 to 3 seconds)
            float fadeProgress = (elapsedTime - BACKGROUND_FADE_START) / BACKGROUND_FADE_DURATION;
            backgroundAlpha.a = 1f - (fadeProgress * 0.3f); // Fade from 1.0 to 0.7 instead of 0.5
        } else {
            // Background dimmed to 70% opacity to serve as backdrop for menu (brighter than before)
            backgroundAlpha.a = 0.7f;
        }

        // Handle menu fade in
        if (elapsedTime >= MENU_FADE_START && elapsedTime < MENU_FADE_START + MENU_FADE_DURATION) {
            float fadeProgress = (elapsedTime - MENU_FADE_START) / MENU_FADE_DURATION;
            menuAlpha.a = fadeProgress; // Fade in menu
        } else if (elapsedTime >= MENU_FADE_START + MENU_FADE_DURATION) {
            menuAlpha.a = 1f; // Fully visible
        }

        // Handle input (only when menu is visible)
        if (menuAlpha.a > 0.5f) {
            handleInput();
        }
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            // Simple mouse/touch selection for now
            float mouseY = Gdx.input.getY();
            float screenMouseY = WINDOW_HEIGHT - mouseY; // Flip Y coordinate

            if (isLabelClicked(newGameLabel, screenMouseY)) {
                selectedIndex = 0;
                handleMenuSelection();
            } else if (isLabelClicked(loadGameLabel, screenMouseY)) {
                selectedIndex = 1;
                handleMenuSelection();
            } else if (isLabelClicked(settingsLabel, screenMouseY)) {
                selectedIndex = 2;
                handleMenuSelection();
            } else if (isLabelClicked(quitGameLabel, screenMouseY)) {
                selectedIndex = 3;
                handleMenuSelection();
            }
        }
    }

    private boolean isLabelClicked(Label label, float mouseY) {
        return mouseY >= label.getY() && mouseY <= label.getY() + label.getHeight();
    }

    private void handleMenuSelection() {
        switch (selectedIndex) {
            case 0: // New Game
                log.info("New Game selected");
                // TODO: Implement new game logic
                break;
            case 1: // Load Game
                log.info("Load Game selected");
                // TODO: Implement load game logic
                break;
            case 2: // Settings
                log.info("Settings selected");
                // TODO: Implement settings menu
                break;
            case 3: // Quit Game
                log.info("Quit Game selected");
                Gdx.app.exit();
                break;
        }
    }

    /**
     * Renders the main menu scene with fade effects.
     * @param batch The SpriteBatch to draw with.
     */
    @Override
    public void render(SpriteBatch batch) {
        // Update timing
        update(Gdx.graphics.getDeltaTime());

        // Draw background image directly without black background underneath
        if (backgroundTexture != null && backgroundTexture != TEXTURE_WHITE) {
            // Draw actual background image with current alpha
            batch.setColor(backgroundAlpha.r, backgroundAlpha.g, backgroundAlpha.b, backgroundAlpha.a);
            batch.draw(backgroundTexture, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        } else {
            // Fallback: draw a brighter gradient background
            batch.setColor(0.2f, 0.2f, 0.3f, backgroundAlpha.a);
            batch.draw(TEXTURE_WHITE, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }

        // Reset color after background
        batch.setColor(Color.WHITE);

        // Always draw menu elements after fade start time, regardless of background
        if (elapsedTime >= MENU_FADE_START) {
            float currentMenuAlpha = menuAlpha.a;

            // Force menu to be visible if we've passed the fade start time
            if (currentMenuAlpha <= 0 && elapsedTime >= MENU_FADE_START + MENU_FADE_DURATION) {
                currentMenuAlpha = 1.0f;
                log.warn("Forcing menu visibility due to timing issue");
            }

            if (currentMenuAlpha > 0) {
                // Draw logo texture with fade
                if (logoTexture != null) {
                    batch.setColor(1f, 1f, 1f, currentMenuAlpha);

                    // Keep logo square and use original aspect ratio
                    float targetLogoSize = 300f; // Target size for logo
                    float originalLogoWidth = logoTexture.getWidth();
                    float originalLogoHeight = logoTexture.getHeight();

                    // Calculate scale to fit within target size while maintaining aspect ratio
                    float logoScale = targetLogoSize / Math.max(originalLogoWidth, originalLogoHeight);

                    float logoWidth = originalLogoWidth * logoScale;
                    float logoHeight = originalLogoHeight * logoScale;

                    float logoX = (WINDOW_WIDTH - logoWidth) / 2f;
                    float logoY = WINDOW_HEIGHT * 0.6f;
                    batch.draw(logoTexture, logoX, logoY, logoWidth, logoHeight);
                }

                // Draw title texture below logo
                if (titleTexture != null && titleTexture != TEXTURE_WHITE) {
                    batch.setColor(1f, 1f, 1f, currentMenuAlpha);

                    // Make title similar size to logo (300px width)
                    float targetTitleWidth = 300f; // Same as logo width
                    float originalWidth = titleTexture.getWidth();
                    float originalHeight = titleTexture.getHeight();
                    
                    // Calculate scale based on target width while maintaining aspect ratio
                    float scale = targetTitleWidth / originalWidth;

                    float titleWidth = originalWidth * scale;
                    float titleHeight = originalHeight * scale;

                    float titleX = (WINDOW_WIDTH - titleWidth) / 2f;
                    float titleY = WINDOW_HEIGHT * 0.58f; // Adjusted to maintain spacing with lowered logo

                    batch.draw(titleTexture, titleX, titleY, titleWidth, titleHeight);
                } else {
                    if (currentMenuAlpha >= 0.99f) {
                        log.warn("Title texture is null or fallback - titleTexture: {}", titleTexture);
                    }
                }

                // Reset color after textures
                batch.setColor(Color.WHITE);

                // Draw menu labels with proper color handling
                drawMenuLabelsWithAlpha(batch, currentMenuAlpha);
            }
        }
    }

    private void drawMenuLabelsWithAlpha(SpriteBatch batch, float alpha) {
        // Store original colors
        Color originalNewGameColor = new Color(newGameLabel.getColor());
        Color originalLoadGameColor = new Color(loadGameLabel.getColor());
        Color originalSettingsColor = new Color(settingsLabel.getColor());
        Color originalQuitGameColor = new Color(quitGameLabel.getColor());

        try {
            // Set alpha for menu labels
            newGameLabel.setColor(originalNewGameColor.r, originalNewGameColor.g, originalNewGameColor.b, alpha);
            loadGameLabel.setColor(originalLoadGameColor.r, originalLoadGameColor.g, originalLoadGameColor.b, alpha);
            settingsLabel.setColor(originalSettingsColor.r, originalSettingsColor.g, originalSettingsColor.b, alpha);
            quitGameLabel.setColor(originalQuitGameColor.r, originalQuitGameColor.g, originalQuitGameColor.b, alpha);

            // Highlight selected option
            highlightSelectedOptionWithAlpha(alpha);

            // Draw menu labels
            newGameLabel.draw(batch, 1.0f);
            loadGameLabel.draw(batch, 1.0f);
            settingsLabel.draw(batch, 1.0f);
            quitGameLabel.draw(batch, 1.0f);

        } finally {
            // Always restore original colors
            newGameLabel.setColor(originalNewGameColor);
            loadGameLabel.setColor(originalLoadGameColor);
            settingsLabel.setColor(originalSettingsColor);
            quitGameLabel.setColor(originalQuitGameColor);
        }
    }

    private void drawMenuLabels(SpriteBatch batch) {
        drawMenuLabelsWithAlpha(batch, menuAlpha.a);
    }

    private void highlightSelectedOptionWithAlpha(float alpha) {
        // Reset all to normal color
        Color normalColor = new Color(Color.BLACK);
        normalColor.a = alpha;

        // Highlight color
        Color highlightColor = new Color(Color.BLUE);
        highlightColor.a = alpha;

        switch (selectedIndex) {
            case 0:
                newGameLabel.setColor(highlightColor);
                break;
            case 1:
                loadGameLabel.setColor(highlightColor);
                break;
            case 2:
                settingsLabel.setColor(highlightColor);
                break;
            case 3:
                quitGameLabel.setColor(highlightColor);
                break;
        }
    }
}
