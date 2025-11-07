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

/**
 * Reusable pause menu that can be used in any game scene.
 * Features blur effect and freezes the game state.
 */
public class PauseMenu {
    private static final Logger log = LoggerFactory.getLogger(PauseMenu.class);

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

    private Runnable onResume;
    private Runnable onRestart;
    private Runnable onMainMenu;
    private Runnable onQuit;

    public PauseMenu() {
        initUI();
        initSettingsOverlay();
    }

    private void initSettingsOverlay() {
        settingsOverlay = new PauseMenuSettings();
        settingsOverlay.setOnClose(() -> {
            Gdx.input.setInputProcessor(menuStage);
            menuStage.setKeyboardFocus(menuTable);
        });
    }

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

        UITextButton resumeButton = new UITextButton("Back to Game", buttonFont);
        UITextButton restartButton = new UITextButton("Restart", buttonFont);
        UITextButton settingsButton = new UITextButton("Settings", buttonFont);
        UITextButton mainMenuButton = new UITextButton("Return to Main Menu", buttonFont);
        UITextButton quitButton = new UITextButton("Quit Game", buttonFont);

        buttons = new UITextButton[] { resumeButton, restartButton, settingsButton, mainMenuButton, quitButton };

        int buttonWidth = 350;
        int buttonHeight = 70;

        for (UITextButton button : buttons) {
            button.setSize(buttonWidth, buttonHeight);
        }

        int centerX = WINDOW_WIDTH / 2 - buttonWidth / 2;
        int startY = WINDOW_HEIGHT / 2 + 100;
        int spacing = 60;

        resumeButton.setPosition(centerX, startY);
        restartButton.setPosition(centerX, startY - spacing);
        settingsButton.setPosition(centerX, startY - spacing * 2);
        mainMenuButton.setPosition(centerX, startY - spacing * 3);
        quitButton.setPosition(centerX, startY - spacing * 4);

        resumeButton.onClick(() -> {
            log.debug("Resume clicked");
            Audio.playSfxConfirm();
            resume();
        });

        restartButton.onClick(() -> {
            log.debug("Restart clicked");
            Audio.playSfxConfirm();
            if (onRestart != null) {
                resume();
                onRestart.run();
            }
        });

        settingsButton.onClick(() -> {
            log.debug("Settings clicked from pause menu");
            Audio.playSfxSelect();
            openSettings();
        });

        mainMenuButton.onClick(() -> {
            log.debug("Main menu clicked");
            Audio.playSfxConfirm();
            if (onMainMenu != null) {
                resume();
                onMainMenu.run();
            }
        });

        quitButton.onClick(() -> {
            log.debug("Quit clicked");
            Audio.playSfxQuitGame();
            if (onQuit != null) {
                onQuit.run();
            } else {
                Gdx.app.exit();
            }
        });

        menuTable.addActor(resumeButton.getActor());
        menuTable.addActor(restartButton.getActor());
        menuTable.addActor(settingsButton.getActor());
        menuTable.addActor(mainMenuButton.getActor());
        menuTable.addActor(quitButton.getActor());

        initKeyboardHandling();
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

        // Set keyboard focus to menuTable so it receives input events
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

    public void pause() {
        if (!isPaused) {
            isPaused = true;
            currentChoice = -1;
            Gdx.input.setInputProcessor(menuStage);
            menuStage.setKeyboardFocus(menuTable);
            log.info("Game paused");
        }
    }

    public void resume() {
        if (isPaused) {
            pendingResume = true;
        }
    }

    private void processResume() {
        isPaused = false;
        currentChoice = -1;
        currentKeyDown.clear();
        if (onResume != null) {
            onResume.run();
        }
        log.info("Game resumed");
    }

    private void openSettings() {
        settingsOverlay.open();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void render(SpriteBatch batch, Texture gameSnapshot) {
        if (!isPaused) return;

        // Process pending resume at the start of render, before any input processing
        if (pendingResume) {
            pendingResume = false;
            processResume();
            return; // Exit immediately after resume
        }

        if (!settingsOverlay.isOpen()) {
            for (Map.Entry<Integer, Long> entry : currentKeyDown.entrySet()) {
                Integer keyCode = entry.getKey();
                Long timeStamp = entry.getValue();
                if (TimeUtils.millis() - timeStamp > 100) {
                    handleKeyDown(keyCode);
                    currentKeyDown.put(keyCode, TimeUtils.millis());
                }
            }
        }

        if (gameSnapshot != null) {
            batch.draw(gameSnapshot, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, 0, 1, 1);
        }

        batch.setColor(1, 1, 1, 1);
        batch.draw(blurOverlay, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

        if (settingsOverlay.isOpen()) {
            settingsOverlay.render(batch);
            return;
        }

        String pausedText = "PAUSED";
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        layout.setText(titleFont, pausedText);
        float titleX = (WINDOW_WIDTH - layout.width) / 2f;
        float titleY = WINDOW_HEIGHT / 2f + 200;

        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, pausedText, titleX, titleY);

        batch.end();

        menuStage.act(Gdx.graphics.getDeltaTime());
        menuStage.draw();

        batch.begin();
    }

    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    public void setOnMainMenu(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu;
    }

    public void setOnQuit(Runnable onQuit) {
        this.onQuit = onQuit;
    }

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
