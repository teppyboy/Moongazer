package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import java.io.FileNotFoundException;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UITextButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

public class MainMenu extends Scene {
    private final Game game;
    private VideoPlayer videoPlayer;
    private FileHandle videoFileHandle;
    private Texture titleTexture;
    private float titleY, titleX, titleWidth, titleHeight;
    private boolean videoPrepared = false;

    public MainMenu(Game game) {
        super(game);
        this.game = game;
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

        game.stage.addActor(root);
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