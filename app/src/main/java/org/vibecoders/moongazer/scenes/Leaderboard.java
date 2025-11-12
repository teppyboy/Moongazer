package org.vibecoders.moongazer.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.vibecoders.moongazer.Game;
import org.vibecoders.moongazer.SaveGameManager;
import org.vibecoders.moongazer.enums.State;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UICloseButton;
import org.vibecoders.moongazer.ui.UIScrollbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Leaderboard extends Scene {
    private UIScrollbar customScrollbar;
    private ScrollPane scrollPane;
    private static final float KEYBOARD_SCROLL_SPEED = 500f;
    private static final float KEYBOARD_CLICK_SCROLL = 50f;
    private boolean isKeyScrollingUp = false;
    private boolean isKeyScrollingDown = false;
    private float keyScrollAccumulator = 0f;
    private Table scoreList;

    public Leaderboard(Game game) {
        super(game);
        buildUI();
    }

    private void buildUI() {
        root.setFillParent(true);
        BitmapFont font = Assets.getFont("ui", 24);
        BitmapFont smallFont = Assets.getFont("ui", 18);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label.LabelStyle smallLabelStyle = new Label.LabelStyle(smallFont, Color.LIGHT_GRAY);
        Label.LabelStyle goldLabelStyle = new Label.LabelStyle(font, new Color(1f, 0.84f, 0f, 1f));

        TextureRegionDrawable bg = new TextureRegionDrawable(Assets.getWhiteTexture());
        Drawable tintedBg = bg.tint(new Color(0.2f, 0.2f, 0.2f, 0.3f));

        Table mainPanel = new Table();
        mainPanel.setSize(900, 700);
        mainPanel.setPosition((Gdx.graphics.getWidth() - 900) / 2f,
                (Gdx.graphics.getHeight() - 700) / 2f);

        Label title = new Label("Endless Mode Leaderboard", labelStyle);
        mainPanel.add(title).colspan(2).padTop(40).padBottom(20);
        mainPanel.row();

        scoreList = new Table();
        scoreList.top();
        createScoreList(scoreList, tintedBg, font, smallLabelStyle, goldLabelStyle);

        scrollPane = new ScrollPane(scoreList);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollBarPositions(false, false);

        mainPanel.add(scrollPane).width(750).height(500).padLeft(20).padRight(10).padBottom(40);
        customScrollbar = new UIScrollbar(scrollPane, 12, 500);
        mainPanel.add(customScrollbar.getActor()).width(40).height(500).padLeft(0).padRight(20).padBottom(40);
        mainPanel.row();

        UICloseButton backButton = new UICloseButton();
        backButton.setSize(40, 40);
        backButton.setPosition(Gdx.graphics.getWidth() - 80, Gdx.graphics.getHeight() - 80);
        backButton.onClick(() -> {
            if (game.transition == null) {
                game.transition = new Transition(game, this, game.mainMenuScene,
                        State.MAIN_MENU, 350);
            }
        });

        root.addActor(mainPanel);
        root.addActor(backButton.getActor());
        game.stage.addActor(root);

        root.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    backButton.click();
                    return true;
                } else if (keycode == Input.Keys.UP) {
                    if (!isKeyScrollingUp) {
                        isKeyScrollingUp = true;
                        scroll(-KEYBOARD_CLICK_SCROLL);
                    }
                    return true;
                } else if (keycode == Input.Keys.DOWN) {
                    if (!isKeyScrollingDown) {
                        isKeyScrollingDown = true;
                        scroll(KEYBOARD_CLICK_SCROLL);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.UP) {
                    isKeyScrollingUp = false;
                    keyScrollAccumulator = 0f;
                    return true;
                } else if (keycode == Input.Keys.DOWN) {
                    isKeyScrollingDown = false;
                    keyScrollAccumulator = 0f;
                    return true;
                }
                return false;
            }
        });
    }

    private void createScoreList(Table list, Drawable bg, BitmapFont font,
                                 Label.LabelStyle smallStyle, Label.LabelStyle goldStyle) {
        list.clear();

        List<SaveGameManager.ScoreEntry> scores = SaveGameManager.getAllEndlessScores();

        if (scores.isEmpty()) {
            Label.LabelStyle grayStyle = new Label.LabelStyle(font, Color.GRAY);
            Label noScoresLabel = new Label("No scores yet. Play Endless Mode to set a record!", grayStyle);
            list.add(noScoresLabel).pad(20).center();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");

        // Header row
        Table headerRow = new Table();
        headerRow.setBackground(bg);

        Label.LabelStyle headerStyle = new Label.LabelStyle(font, new Color(0.8f, 0.8f, 0.8f, 1f));
        Label rankHeader = new Label("Rank", headerStyle);
        Label scoreHeader = new Label("Score", headerStyle);
        Label waveHeader = new Label("Wave", headerStyle);
        Label dateHeader = new Label("Date", headerStyle);

        headerRow.add(rankHeader).width(80).pad(10).padLeft(90);
        headerRow.add(scoreHeader).width(160).pad(10);
        headerRow.add(waveHeader).width(120).pad(10);
        headerRow.add(dateHeader).width(390).pad(10);

        list.add(headerRow).width(750).padBottom(10);
        list.row();

        // Score entries
        for (int i = 0; i < scores.size(); i++) {
            SaveGameManager.ScoreEntry entry = scores.get(i);
            Table scoreRow = new Table();

            // Alternating background colors
            TextureRegionDrawable rowBg = new TextureRegionDrawable(Assets.getWhiteTexture());
            if (i % 2 == 0) {
                scoreRow.setBackground(rowBg.tint(new Color(0.15f, 0.15f, 0.15f, 0.5f)));
            } else {
                scoreRow.setBackground(rowBg.tint(new Color(0.1f, 0.1f, 0.1f, 0.5f)));
            }

            // Rank label - gold for top 3
            Label.LabelStyle rankStyle;
            if (i == 0) {
                rankStyle = goldStyle;
            } else if (i == 1) {
                rankStyle = new Label.LabelStyle(font, new Color(0.75f, 0.75f, 0.75f, 1f)); // Silver
            } else if (i == 2) {
                rankStyle = new Label.LabelStyle(font, new Color(0.8f, 0.5f, 0.2f, 1f)); // Bronze
            } else {
                rankStyle = smallStyle;
            }

            Label rankLabel = new Label("#" + (i + 1), rankStyle);
            Label scoreLabel = new Label(String.format("%,d", entry.score), smallStyle);
            Label waveLabel = new Label("Wave " + entry.wave, smallStyle);
            Label dateLabel = new Label(dateFormat.format(new Date(entry.timestamp)), smallStyle);

            scoreRow.add(rankLabel).width(100).pad(10).padLeft(-30);
            scoreRow.add(scoreLabel).width(150).pad(10).padLeft(-30);
            scoreRow.add(waveLabel).width(150).pad(10).padLeft(-30);
            scoreRow.add(dateLabel).width(300).pad(10).padLeft(-30);

            list.add(scoreRow).width(700).padBottom(5);
            list.row();
        }
    }

    private void scroll(float amount) {
        float currentScroll = scrollPane.getScrollY();
        float newScroll = Math.max(0, Math.min(scrollPane.getMaxY(), currentScroll + amount));
        scrollPane.setScrollY(newScroll);
    }

    @Override
    public void render(SpriteBatch batch) {
        game.mainMenuScene.render(batch);
        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(Assets.getWhiteTexture(), 0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(Color.WHITE);

        float delta = Gdx.graphics.getDeltaTime();

        if (isKeyScrollingUp) {
            keyScrollAccumulator += KEYBOARD_SCROLL_SPEED * delta;
            if (keyScrollAccumulator >= 1f) {
                scroll(-keyScrollAccumulator);
                keyScrollAccumulator = 0f;
            }
        } else if (isKeyScrollingDown) {
            keyScrollAccumulator += KEYBOARD_SCROLL_SPEED * delta;
            if (keyScrollAccumulator >= 1f) {
                scroll(keyScrollAccumulator);
                keyScrollAccumulator = 0f;
            }
        }
    }
}
