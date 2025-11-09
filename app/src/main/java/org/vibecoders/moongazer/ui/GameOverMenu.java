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

public class GameOverMenu {
    private static final Logger log = LoggerFactory.getLogger(GameOverMenu.class);
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
    private int currentChoice = -1;
    private HashMap<Integer, Long> currentKeyDown = new HashMap<>();
    private float fadeAlpha = 0f;
    private FadeState fadeState = FadeState.HIDDEN;
    private GlyphLayout glyphLayout;
    private int totalScore = 0;
    private int bestScore = 0;
    private Runnable onPlayAgain;
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

    public GameOverMenu() {
        initUI();
    }

    private void initUI() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.85f);
        pixmap.fill();
        darkOverlay = new Texture(pixmap);
        pixmap.dispose();
        titleFont = Assets.getFont("ui", 48);
        scoreFont = Assets.getFont("ui", 32);
        buttonFont = Assets.getFont("ui", 24);
        glyphLayout = new GlyphLayout();
        menuStage = new Stage();
        menuTable = new Table();
        menuTable.setFillParent(true);
        menuStage.addActor(menuTable);

        ButtonConfig[] configs = createButtonConfigs();
        buttons = new UITextButton[configs.length];

        int centerX = WINDOW_WIDTH / 2 - BUTTON_WIDTH / 2;
        int startY = WINDOW_HEIGHT / 2 - 150;

        for (int i = 0; i < configs.length; i++) {
            buttons[i] = buildButton(configs[i], i, centerX, startY);
        }

        initKeyboardHandling();
    }

    private ButtonConfig[] createButtonConfigs() {
        return new ButtonConfig[]{
                new ButtonConfig("Play Again", () -> {
                    log.debug("Play Again clicked");
                    Audio.playSfxConfirm();
                    if (onPlayAgain != null) {
                        hide();
                        onPlayAgain.run();
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

    private UITextButton buildButton(ButtonConfig config, int index, int centerX, int startY) {
        UITextButton button = new UITextButton(config.label, buttonFont);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setPosition(centerX, startY - BUTTON_SPACING * index);
        button.onClick(config.action);
        menuTable.addActor(button.getActor());
        return button;
    }

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
     * Show the game over menu with the given score
     */
    public void show(int finalScore) {
        if (!isVisible) {
            this.totalScore = finalScore;
            // TODO: Load best score from save file
            if (finalScore > bestScore) {
                bestScore = finalScore;
                // TODO: Save new best score
                log.info("New best score: {}", bestScore);
            }

            isVisible = true;
            currentChoice = 0; // Default to "Play Again"
            buttons[currentChoice].hoverEnter();
            fadeAlpha = 0f;
            fadeState = FadeState.FADING_IN;
            Gdx.input.setInputProcessor(menuStage);
            menuStage.setKeyboardFocus(menuTable);
            log.info("Game Over! Final Score: {}, Best Score: {}", totalScore, bestScore);
        }
    }

    /**
     * Hide the game over menu
     */
    private void hide() {
        if (isVisible) {
            isVisible = false;
            currentChoice = -1;
            currentKeyDown.clear();
            fadeState = FadeState.HIDDEN;
            fadeAlpha = 0f;
            log.info("Game Over menu hidden");
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void render(SpriteBatch batch, Texture gameSnapshot) {
        if (!isVisible) {
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();
        updateFade(delta);

        for (Map.Entry<Integer, Long> entry : new HashMap<>(currentKeyDown).entrySet()) {
            Integer keyCode = entry.getKey();
            Long timeStamp = entry.getValue();
            if (TimeUtils.millis() - timeStamp > 100) {
                handleKeyDown(keyCode);
                currentKeyDown.put(keyCode, TimeUtils.millis());
            }
        }

        if (gameSnapshot != null) {
            batch.draw(gameSnapshot, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, 0, 1, 1);
        }

        if (fadeAlpha <= 0f) {
            return;
        }

        batch.setColor(1f, 1f, 1f, fadeAlpha);
        batch.draw(darkOverlay, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.setColor(Color.WHITE);

        String gameOverText = "GAME OVER";
        glyphLayout.setText(titleFont, gameOverText);
        float titleX = (WINDOW_WIDTH - glyphLayout.width) / 2f;
        float titleY = WINDOW_HEIGHT / 2f + 300;

        Color originalTitleColor = titleFont.getColor().cpy();
        titleFont.setColor(1f, 0.3f, 0.3f, fadeAlpha); // Red tint for game over
        titleFont.draw(batch, gameOverText, titleX, titleY);
        titleFont.setColor(originalTitleColor);

        // Draw scores
        String totalScoreText = "Total Score: " + totalScore;
        String bestScoreText = "Best Score: " + bestScore;

        glyphLayout.setText(scoreFont, totalScoreText);
        float totalScoreX = (WINDOW_WIDTH - glyphLayout.width) / 2f;
        float totalScoreY = WINDOW_HEIGHT / 2f + 220;

        glyphLayout.setText(scoreFont, bestScoreText);
        float bestScoreX = (WINDOW_WIDTH - glyphLayout.width) / 2f;
        float bestScoreY = WINDOW_HEIGHT / 2f + 170;

        Color originalScoreColor = scoreFont.getColor().cpy();
        scoreFont.setColor(1f, 1f, 1f, fadeAlpha);
        scoreFont.draw(batch, totalScoreText, totalScoreX, totalScoreY);
        scoreFont.draw(batch, bestScoreText, bestScoreX, bestScoreY);
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

    // Setters for callbacks
    public void setOnPlayAgain(Runnable callback) {
        this.onPlayAgain = callback;
    }

    public void setOnMainMenu(Runnable callback) {
        this.onMainMenu = callback;
    }

    public void setOnQuit(Runnable callback) {
        this.onQuit = callback;
    }

    public void dispose() {
        if (darkOverlay != null) {
            darkOverlay.dispose();
        }
        if (menuStage != null) {
            menuStage.dispose();
        }
    }
}

