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

    /**
     * Constructs a stage selection panel with scrollable list of stages.
     *
     * @param root the root table to add this panel to
     * @param stages the list of stage data to display
     * @param listener the listener to be notified when a stage is selected
     */
    public StageSelectionPanel(Table root, List<StageData> stages, OnStageSelectedListener listener) {
        this.listener = listener;
        createPanel(stages);
        root.addActor(panel);
    }

    /**
     * Creates the panel UI with stage buttons and custom scrollbar.
     *
     * @param stages the list of stage data to create buttons for
     */
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

    /**
     * Updates the scrollbar animation. Should be called every frame.
     *
     * @param delta the time delta since last frame
     */
    public void update(float delta) {
        if (customScrollbar != null) {
            customScrollbar.update(delta);
        }
    }

    /**
     * Gets the scroll pane containing the stage buttons.
     *
     * @return the scroll pane
     */
    public ScrollPane getScrollPane() {
        return scrollPane;
    }
}
