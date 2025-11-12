package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.managers.Audio;
import org.vibecoders.moongazer.ui.UITextButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

public class MainMenu extends Scene {
    private VideoPlayer videoPlayer;
    private FileHandle videoFileHandle;
    private Texture titleTexture;
    private UITextButton[] buttons;
    private HashMap<Integer, Long> currentKeyDown = new HashMap<>();
    private float titleY, titleX, titleWidth, titleHeight;
    private boolean videoPrepared = false;
    private int currentChoice = -1;

    public MainMenu(Game game) {
        super(game);
        initVideo();
        initUI();
    }

    private void initVideo() {
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        videoFileHandle = Assets.getAsset("videos/main_menu_background.webm", FileHandle.class);
        try {
            videoPlayer.load(videoFileHandle);
        } catch (FileNotFoundException e) {
            log.error("Failed to load video", e);
        }
    }

    private void initUI() {
        titleTexture = Assets.getAsset("textures/main_menu/title.png", Texture.class);
        float targetTitleWidth = 500f;
        float scale = targetTitleWidth / titleTexture.getWidth();
        titleWidth = titleTexture.getWidth() * scale;
        titleHeight = titleTexture.getHeight() * scale;
        titleX = (WINDOW_WIDTH - titleWidth) / 2f;
        titleY = WINDOW_HEIGHT / 2f - titleHeight / 8f;
        var font = Assets.getFont("ui", 24);
        UITextButton playButton = new UITextButton("Play", font);
        UITextButton loadButton = new UITextButton("Load", font);
        UITextButton leaderboardButton = new UITextButton("Leaderboard", font);
        UITextButton settingsButton = new UITextButton("Settings", font);
        UITextButton exitButton = new UITextButton("Exit", font);
        buttons = new UITextButton[] { playButton, loadButton, leaderboardButton, settingsButton, exitButton };
        int buttonWidth = 300;
        int buttonHeight = 80;
        playButton.setSize(buttonWidth, buttonHeight);
        loadButton.setSize(buttonWidth, buttonHeight);
        leaderboardButton.setSize(buttonWidth, buttonHeight);
        settingsButton.setSize(buttonWidth, buttonHeight);
        exitButton.setSize(buttonWidth, buttonHeight);
        int centerX = WINDOW_WIDTH / 2 - buttonWidth / 2;
        int startY = WINDOW_HEIGHT / 2 - buttonHeight / 2;
        int spacing = 65;
        playButton.setPosition(centerX, startY);
        loadButton.setPosition(centerX, startY - spacing);
        leaderboardButton.setPosition(centerX, startY - spacing * 2);
        settingsButton.setPosition(centerX, startY - spacing * 3);
        exitButton.setPosition(centerX, startY - spacing * 4);
        playButton.onClick(() -> {
            log.debug("Play clicked");
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.selectionScene, State.SELECTION, 500);
            }
        });
        loadButton.onClick(() -> {
            log.debug("Load clicked");
            if (game.transition == null) {
                // Reset LoadScene to LOAD mode
                if (game.loadScene instanceof org.vibecoders.moongazer.scenes.LoadScene) {
                    ((org.vibecoders.moongazer.scenes.LoadScene) game.loadScene).setMode(
                        org.vibecoders.moongazer.scenes.LoadScene.Mode.LOAD
                    );
                }
                game.transition = new Transition(game, this, game.loadScene, State.LOAD_GAME, 350);
            }
        });
        leaderboardButton.onClick(() -> {
            log.debug("Leaderboard clicked");
            if (game.transition == null) {
                // Recreate leaderboard to refresh scores
                game.recreateScene(game.leaderboardScene, () -> new Leaderboard(game), scene -> game.leaderboardScene = scene);
                game.transition = new Transition(game, this, game.leaderboardScene, State.LEADERBOARD, 350);
            }
        });
        settingsButton.onClick(() -> {
            log.debug("Settings clicked");
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.settingsScene, State.SETTINGS, 350);
            }
        });
        exitButton.onClick(() -> {
            log.debug("Exit clicked");
            Gdx.app.exit();
        });

        root.addActor(playButton.getActor());
        root.addActor(loadButton.getActor());
        root.addActor(leaderboardButton.getActor());
        root.addActor(settingsButton.getActor());
        root.addActor(exitButton.getActor());

        // Keyboard navigation handling
        initKeyboardHandling();
        game.stage.addActor(root);
    }

    private void initKeyboardHandling() {
        for (int i = 0; i < buttons.length; i++) {
            final int index = i;
            buttons[i].onHoverEnter(() -> {
                log.trace("Button hover enter: {}", index);
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

        root.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                sKeyDown(event, keycode);
                currentKeyDown.put(keycode, TimeUtils.millis());
                return true;
            }
        });

        root.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                currentKeyDown.remove(keycode);
                return true;
            }
        });
    }

    private void startVideoOnce() {
        if (videoPlayer == null || videoPrepared)
            return;
        if (videoFileHandle == null || !videoFileHandle.exists())
            return;
        if (game.transition == null && game.state != State.MAIN_MENU)
            return;
        videoPlayer.setLooping(true);
        videoPlayer.play();
        Audio.menuMusicPlay();
        videoPrepared = true;
    }

    /**
     * The actual key down handler. ((s)cene key down)
     * 
     * @param event   not used for now, can be null
     * @param keycode the keycode of the pressed key
     */
    public void sKeyDown(InputEvent event, int keycode) {
        log.trace("Key pressed: {}", keycode);
        switch (keycode) {
            case Input.Keys.UP:
                currentChoice = (currentChoice - 1 + buttons.length) % buttons.length;
                break;
            case Input.Keys.DOWN:
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
            log.trace("Current choice: {}", currentChoice);
            for (int i = 0; i < buttons.length; i++) {
                if (i == currentChoice) {
                    buttons[i].hoverEnter();
                } else {
                    buttons[i].hoverExit();
                }
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        // SDL way of handling key input XD
        for (Map.Entry<Integer, Long> entry : currentKeyDown.entrySet()) {
            Integer keyCode = entry.getKey();
            Long timeStamp = entry.getValue();
            if (TimeUtils.millis() - timeStamp > 150) {
                sKeyDown(null, keyCode);
                currentKeyDown.put(keyCode, TimeUtils.millis());
            }
        }
        startVideoOnce();
        videoPlayer.update();
        Texture videoTexture = videoPlayer.getTexture();
        
        // Apply parallax effect to background video
        int mouseX = Gdx.input.getX();
        int mouseY = WINDOW_HEIGHT - Gdx.input.getY();
        float offsetX = ((mouseX - WINDOW_WIDTH / 2f) / WINDOW_WIDTH) * PARALLAX_STRENGTH;
        float offsetY = ((mouseY - WINDOW_HEIGHT / 2f) / WINDOW_HEIGHT) * PARALLAX_STRENGTH;
        
        // Zoom background by 1.05x and center it
        float zoomScale = 1.05f;
        float bgWidth = WINDOW_WIDTH * zoomScale;
        float bgHeight = WINDOW_HEIGHT * zoomScale;
        float bgX = (WINDOW_WIDTH - bgWidth) / 2f + offsetX;
        float bgY = (WINDOW_HEIGHT - bgHeight) / 2f + offsetY;
        
        batch.draw(videoTexture, bgX, bgY, bgWidth, bgHeight);
        batch.draw(titleTexture, titleX, titleY, titleWidth, titleHeight);
    }

    @Override
    public void dispose() {
        super.dispose();
        videoPlayer.dispose();
    }
}