package org.vibecoders.moongazer.scenes;

import static org.vibecoders.moongazer.Constants.*;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.novel.CharacterActor;
import org.vibecoders.moongazer.ui.novel.ChoiceBox;
import org.vibecoders.moongazer.ui.novel.DialogueBoxTransparent;

public class DialogueScene extends Scene {
    private final CharacterActor character;
    private final DialogueBoxTransparent dialogue;
    private ChoiceBox choice;
    private int step = 0;

    public DialogueScene(Game game) {
        super(game);
        Texture bgTexture = Assets.getAsset("textures/main_menu/background.png", Texture.class);
        Image background = new Image(bgTexture);
        background.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        background.setColor(0.5f, 0.5f, 0.5f, 1f);
        root.addActor(background);

        Pixmap overlayPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        overlayPixmap.setColor(0, 0, 0, 0.4f);
        overlayPixmap.fill();
        Texture overlayTexture = new Texture(overlayPixmap);
        overlayPixmap.dispose();
        Image overlay = new Image(overlayTexture);
        overlay.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.addActor(overlay);

        TextureRegion charBase = new TextureRegion(Assets.getAsset("textures/vn_scene/char_base.png", Texture.class));
        character = new CharacterActor(charBase);
        float charX = (WINDOW_WIDTH - character.getWidth()) / 2f;
        float charY = (WINDOW_HEIGHT - character.getHeight()) / 2f + 100;
        character.setPosition(charX, charY);
        root.addActor(character);

        TextureRegion dialogBg = createDialogBackground();
        TextureRegion separator = new TextureRegion(Assets.getAsset("textures/vn_scene/separator.png", Texture.class));

        var font = Assets.getFont("ui", 20);
        dialogue = new DialogueBoxTransparent(font, dialogBg, separator, WINDOW_WIDTH - 100);
        dialogue.setPosition(50, 20);
        root.addActor(dialogue);

        showStep(0);

        root.addListener(new InputListener() {
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
        game.stage.addActor(root);
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
                choice = new ChoiceBox(font, new String[] { "New game", "Back to Main Menu" }, idx -> {
                    if (idx == 0) {
                        dialogue.setDialogue("Owari Da", "Toi yeu tretrauit...");  // :thumbsup:
                        step = 2;
                    } else {
                        game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 500);
                    }
                });
                choice.setPosition(WINDOW_WIDTH - 260, WINDOW_HEIGHT / 2);
                root.addActor(choice);
                break;
            case 2:
                game.transition = new Transition(game, this, game.mainMenuScene, State.MAIN_MENU, 500);
                break;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        return;
    }
}
