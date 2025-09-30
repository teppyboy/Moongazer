package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UITextButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;

public class MainMenu extends Scene {
    private final Game game;
    private VideoPlayer videoPlayer;
    private FileHandle videoFileHandle;
    private Texture titleTexture;
    private float titleY, titleX, titleWidth, titleHeight;
    private boolean videoPrepared = false;
    private boolean firstFrameLogged = false;
    private long videoStartTime;

    private static final String WEBM_PATH = "videos/main_menu_background.webm";
    private static final String OGV_PATH  = "videos/main_menu_background.ogv";

    public MainMenu(Game game) {
        super(game);
        this.game = game;
        initVideo();
        initUI();
    }

    private void initVideo() {
        try {
            videoPlayer = VideoPlayerCreator.createVideoPlayer();
            FileHandle webm = Gdx.files.internal(WEBM_PATH);
            FileHandle ogv  = Gdx.files.internal(OGV_PATH);
            if (webm.exists()) {
                videoFileHandle = webm;
                log.info("Using menu background (WebM): {}", webm.path());
            } else if (ogv.exists()) {
                videoFileHandle = ogv;
                log.info("Using menu background (OGV): {}", ogv.path());
            } else {
                log.warn("No background video found (expected {} or {}).", WEBM_PATH, OGV_PATH);
            }
        } catch (Exception e) {
            log.error("Cannot create VideoPlayer", e);
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
        UITextButton settingsButton = new UITextButton("Settings", font);
        UITextButton exitButton = new UITextButton("Exit", font);

        int buttonWidth = 300, buttonHeight = 80;
        int centerX = WINDOW_WIDTH / 2 - buttonWidth / 2;
        int startY = WINDOW_HEIGHT / 2 - buttonHeight / 2;
        int spacing = 65;

        playButton.setSize(buttonWidth, buttonHeight); playButton.setPosition(centerX, startY);
        loadButton.setSize(buttonWidth, buttonHeight); loadButton.setPosition(centerX, startY - spacing);
        settingsButton.setSize(buttonWidth, buttonHeight); settingsButton.setPosition(centerX, startY - spacing * 2);
        exitButton.setSize(buttonWidth, buttonHeight); exitButton.setPosition(centerX, startY - spacing * 3);

        exitButton.onClick(() -> log.info("Exit clicked"));

        root.addActor(playButton.getActor());
        root.addActor(loadButton.getActor());
        root.addActor(settingsButton.getActor());
        root.addActor(exitButton.getActor());
        game.stage.addActor(root);
    }

    private void ensureVideoStarted() {
        if (videoPlayer == null || videoPrepared) return;
        if (videoFileHandle == null || !videoFileHandle.exists()) return;
        if (game.transition == null && game.state != State.MAIN_MENU) return;
        startVideo();
    }

    private void startVideo() {
        try {
            videoPlayer.setLooping(true);
            videoPlayer.play(videoFileHandle);
            videoPrepared = true;
            videoStartTime = TimeUtils.millis();
        } catch (Exception e) {
            log.error("Failed to play video: {}", videoFileHandle.path(), e);
        }
    }

    public void forceStartVideo() { // used by Transition for early warm-up
        if (!videoPrepared && videoFileHandle != null && videoFileHandle.exists()) startVideo();
    }

    @Override
    public void render(SpriteBatch batch) {
        ensureVideoStarted();
        if (videoPlayer != null && videoPrepared) {
            videoPlayer.update();
            Texture videoTexture = videoPlayer.getTexture();
            if (videoTexture != null) {
                if (!firstFrameLogged) {
                    firstFrameLogged = true;
                    log.info("Menu video first frame in {} ms", TimeUtils.timeSinceMillis(videoStartTime));
                }
                batch.draw(videoTexture, 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            } else {
                batch.draw(Assets.getBlackTexture(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
            }
        } else {
            batch.draw(Assets.getBlackTexture(), 0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }
        batch.draw(titleTexture, titleX, titleY, titleWidth, titleHeight);
    }

    public void updateVideo() {
        if (videoPlayer != null && videoPrepared) videoPlayer.update();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (videoPlayer != null) videoPlayer.dispose();
    }
}