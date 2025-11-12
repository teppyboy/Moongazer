package org.vibecoders.moongazer.ui.story;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.vibecoders.moongazer.managers.Assets;
import org.vibecoders.moongazer.ui.UIScrollbar;
import org.vibecoders.moongazer.ui.UITextButton;
import java.util.List;

import static org.vibecoders.moongazer.Constants.WINDOW_HEIGHT;

public class StageSelectionPanel {
    private Table panel;
    private ScrollPane scrollPane;
    UIScrollbar customScrollbar;
    private OnStageSelectedListener listener;
    private static final BitmapFont font = Assets.getFont("ui", 24);

    public interface OnStageSelectedListener {
        void onStageSelected(StageData stageData);
    }

    public StageSelectionPanel(Table root, List<StageData> stages, OnStageSelectedListener listener) {
        this.listener = listener;
        createPanel(stages);
        root.addActor(panel);
    }

    private void createPanel(List<StageData> stages) {
        panel = new Table();
        panel.setSize(300, 550);
        panel.setPosition(10, (WINDOW_HEIGHT - 550) / 2f);

        Table contentTable = new Table();
        contentTable.top();

        for (StageData stage : stages) {
            UITextButton button = new UITextButton(stage.getName(), font);
            button.onClick(() -> {
                if (listener != null) {
                    listener.onStageSelected(stage);
                }
            });
            contentTable.add(button.getActor()).width(240).height(65).spaceBottom(4);
            contentTable.row();
        }

        scrollPane = new ScrollPane(contentTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setScrollBarPositions(false, false);
        scrollPane.setSmoothScrolling(true);
        scrollPane.setOverscroll(false, false);

        customScrollbar = new UIScrollbar(scrollPane, 5, 530);

        panel.add(scrollPane).width(240).height(530).padLeft(10).padTop(10).padBottom(10);
        panel.add(customScrollbar.getActor()).width(40).height(530).padTop(10).padBottom(10).padRight(10);
    }
    public void update(float delta) {
        if (customScrollbar != null) {
            customScrollbar.update(delta);
        }
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}
