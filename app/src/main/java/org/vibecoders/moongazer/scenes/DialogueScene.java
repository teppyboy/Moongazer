package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.vn.CharacterActor;
import org.vibecoders.moongazer.vn.ChoiceBox;
import org.vibecoders.moongazer.vn.DialogueBoxTransparent;

public class DialogueScene extends Scene {
    private Stage stage;
    private final CharacterActor character;
    private final DialogueBoxTransparent dialogue;
    private ChoiceBox choice;
    private int step = 0;
    private float alpha = 1f;
    private boolean isActive = true;
    private boolean inTransition = false;

    public DialogueScene(Game game) {
        super(game);
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Texture bgTexture = Assets.getAsset("textures/main_menu/background.png", Texture.class);
        Image background = new Image(bgTexture);
        background.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        background.setColor(0.5f, 0.5f, 0.5f, 1f);
        stage.addActor(background);

        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(0, 0, 0, 0.4f);
        overlayPixmap.fill();
        Texture overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();
        Image overlay = new Image(overlayTexture);
        overlay.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        stage.addActor(overlay);

        TextureRegion charBase = loadTexture("textures/vn_scene/char_base.png");
        character = new CharacterActor(charBase);
        float charX = (WINDOW_WIDTH - character.getWidth()) / 2f;
        float charY = (WINDOW_HEIGHT - character.getHeight()) / 2f + 100;
        character.setPosition(charX, charY);
        stage.addActor(character);

        TextureRegion dialogBg = createDialogBackground();
        TextureRegion separator = loadTexture("textures/vn_scene/separator.png");

        var font = Assets.getFont("ui", 20);
        dialogue = new DialogueBoxTransparent(font, dialogBg, separator, WINDOW_WIDTH - 100);
        dialogue.setPosition(50, 20);
        stage.addActor(dialogue);

        showStep(0);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent e, int keycode) {
                if (choice == null) {
                    nextOrSkip();
                }
                return true;
            }

            @Override
            public boolean touchDown(InputEvent e, float x, float y, int pointer, int button) {
                if (choice == null) {
                    nextOrSkip();
                    return true;
                }
                return false;
            }
        });
    }

    private TextureRegion loadTexture(String path) {
        try {
            Texture tex = Assets.getAsset(path, Texture.class);
            return new TextureRegion(tex);
        } catch (Exception e) {
            log.warn("Failed to load texture: {}, creating placeholder", path);
            Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.GRAY);
            pixmap.fill();
            Texture tex = new Texture(pixmap);
            pixmap.dispose();
            return new TextureRegion(tex);
        }
    }

    private TextureRegion createDialogBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(tex);
    }

    private void nextOrSkip() {
        if (!dialogue.isDone()) {
            dialogue.skip();
            return;
        }
        showStep(++step);
    }

    private void showStep(int s) {
        if (choice != null) {
            choice.remove();
            choice = null;
        }

        switch (s) {
            case 0:
                dialogue.setDialogue("Iuno", "Hmph. Apologies, but I need my rest...");
                break;
            case 1:
                var font = Assets.getFont("ui", 18);
                choice = new ChoiceBox(font, new String[]{"New game", "Back to Main Menu"}, idx -> {
                    if (idx == 0) {
                        dialogue.setDialogue("Iuno", "Toi yeu tunxd...");
                        step = 2;
                    } else {
                        game.returnToMainMenu();
                    }
                });
                choice.setPosition(WINDOW_WIDTH - 260, WINDOW_HEIGHT / 2);
                stage.addActor(choice);
                break;
            case 2:
                game.returnToMainMenu();
                break;
        }
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        if (stage != null && stage.getRoot() != null) {
            stage.getRoot().setColor(1, 1, 1, alpha);
        }
    }

    public void enterTransition() {
        inTransition = true;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (inTransition || !isActive || stage == null) {
            return;
        }
        try {
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        } catch (Exception e) {
            isActive = false;
            log.warn("Error rendering DialogueScene, marking as inactive", e);
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
    }
}
