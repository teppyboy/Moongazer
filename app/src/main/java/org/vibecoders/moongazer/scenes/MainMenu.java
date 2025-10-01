package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import java.io.FileNotFoundException;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UITextButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

public class MainMenu extends Scene {
    private VideoPlayer videoPlayer;
    private FileHandle videoFileHandle;
    private Texture titleTexture;
    private UITextButton[] buttons;
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
        // Title
        titleTexture = Assets.getAsset("textures/main_menu/title.png", Texture.class);
        float targetTitleWidth = 500f;
        float scale = targetTitleWidth / titleTexture.getWidth();
        titleWidth = titleTexture.getWidth() * scale;
        titleHeight = titleTexture.getHeight() * scale;
        titleX = (WINDOW_WIDTH - titleWidth) / 2f;
        titleY = WINDOW_HEIGHT / 2f - titleHeight / 8f;

        // Buttons
        var font = Assets.getFont("ui", 24);
        UITextButton playButton = new UITextButton("Play", font);
        UITextButton loadButton = new UITextButton("Load", font);
        UITextButton settingsButton = new UITextButton("Settings", font);
        UITextButton exitButton = new UITextButton("Exit", font);
        buttons = new UITextButton[] { playButton, loadButton, settingsButton, exitButton };

        int buttonWidth = 300;
        int buttonHeight = 80;

        playButton.setSize(buttonWidth, buttonHeight);
        loadButton.setSize(buttonWidth, buttonHeight);
        settingsButton.setSize(buttonWidth, buttonHeight);
        exitButton.setSize(buttonWidth, buttonHeight);

        int centerX = WINDOW_WIDTH / 2 - buttonWidth / 2;
        int startY = WINDOW_HEIGHT / 2 - buttonHeight / 2;
        int spacing = 65;

        playButton.setSize(buttonWidth, buttonHeight);
        playButton.setPosition(centerX, startY);
        loadButton.setSize(buttonWidth, buttonHeight);
        loadButton.setPosition(centerX, startY - spacing);
        settingsButton.setSize(buttonWidth, buttonHeight);
        settingsButton.setPosition(centerX, startY - spacing * 2);
        exitButton.setSize(buttonWidth, buttonHeight);
        exitButton.setPosition(centerX, startY - spacing * 3);

        // Mouse click handlers
        playButton.onClick(() -> log.debug("Play clicked"));
        loadButton.onClick(() -> log.debug("Load clicked"));
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
                log.trace("Key pressed: {}", keycode);
                switch (keycode) {
                    case Input.Keys.UP:
                        currentChoice = (currentChoice - 1 + 4) % 4;
                        break;
                    case Input.Keys.DOWN:
                        currentChoice = (currentChoice + 1) % 4;
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
        videoPrepared = true;
    }

    @Override
    public void render(SpriteBatch batch) {
        startVideoOnce();
        videoPlayer.update();
        Texture videoTexture = videoPlayer.getTexture();
        batch.draw(videoTexture, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        batch.draw(titleTexture, titleX, titleY, titleWidth, titleHeight);
    }

    @Override
    public void dispose() {
        super.dispose();
        videoPlayer.dispose();
    }
}