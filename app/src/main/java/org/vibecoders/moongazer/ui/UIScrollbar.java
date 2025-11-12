package org.vibecoders.moongazer.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vibecoders.moongazer.managers.Assets;

/**
 * Custom scrollbar with arrow buttons for constant scrolling
 */
public class UIScrollbar {
    private static final Logger log = LoggerFactory.getLogger(UIScrollbar.class);
    private static final float SCROLL_SPEED = 500f;
    private static final float CLICK_SCROLL = 50f;

    private final Table scrollbarContainer;
    private final ScrollPane scrollPane;
    private final Image scrollIndicator;
    private final Image scrollTrack;
    private final Table scrollArea;
    // private final float scrollbarHeight;
    private final float trackHeight;

    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;
    private boolean isScrollingFromTrack = false;
    private boolean isScrollingTrackUp = false;
    private float scrollAccumulator = 0f;
    private float trackClickTargetY = 0;

    public UIScrollbar(ScrollPane scrollPane, float width, float height) {
        this.scrollPane = scrollPane;
        // this.scrollbarHeight = height;

        Texture arrowTexture = Assets.getAsset("textures/ui/UI_Scrollbar_Arrow.png", Texture.class);
        Texture knobTexture = Assets.getAsset("textures/ui/ScrollVerticalKnob.png", Texture.class);
        Texture whiteTexture = Assets.getWhiteTexture();

        int arrowHeight = arrowTexture.getHeight() / 2;
        TextureRegion upArrowRegion = new TextureRegion(arrowTexture, 0, 0, arrowTexture.getWidth(), arrowHeight);
        TextureRegionDrawable arrowUpDrawable = new TextureRegionDrawable(upArrowRegion);

        TextureRegion downArrowRegion = new TextureRegion(arrowTexture, 0, arrowHeight, arrowTexture.getWidth(), arrowHeight);
        TextureRegionDrawable arrowDownDrawable = new TextureRegionDrawable(downArrowRegion);

        scrollbarContainer = new Table();
        scrollbarContainer.top();

        ImageButton.ImageButtonStyle upStyle = new ImageButton.ImageButtonStyle();
        upStyle.imageUp = arrowUpDrawable;
        upStyle.imageOver = arrowUpDrawable.tint(new Color(1.2f, 1.2f, 1.2f, 1f)); // Sáng lên khi hover
        upStyle.imageDown = arrowUpDrawable.tint(new Color(0.8f, 0.8f, 0.8f, 1f)); // Tối đi khi bấm
        ImageButton upButton = new ImageButton(upStyle);
        upButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isScrollingUp = true;
                scrollUp(CLICK_SCROLL);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isScrollingUp = false;
            }
        });

        // Scroll area
        float buttonHeight = 40f;
        trackHeight = height - (buttonHeight * 2);

        scrollArea = new Table();
        Stack scrollStack = new Stack();

        // Gray background track
        scrollTrack = new Image(whiteTexture);
        scrollTrack.setColor(new Color(0.3f, 0.3f, 0.3f, 0.8f));

        // Green scroll indicator
        scrollIndicator = new Image(new TextureRegionDrawable(new TextureRegion(knobTexture)));
//        scrollIndicator.setColor(new Color(0.2f, 0.8f, 0.3f, 1f));

        // Add to stack
        Table trackTable = new Table();
        trackTable.add(scrollTrack).width(width * 0.65f).expand().fill();

        Table indicatorTable = new Table();
        indicatorTable.top();
        indicatorTable.add(scrollIndicator).width(width).height(calculateIndicatorHeight());

        scrollStack.add(trackTable);
        scrollStack.add(indicatorTable);

        scrollArea.add(scrollStack).width(width).height(trackHeight);

        // Add drag listener to indicator area
        scrollArea.addListener(new InputListener() {
            private boolean isDraggingIndicator = false;
            private float dragStartY;
            private float dragStartScrollY;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                float indicatorHeight = calculateIndicatorHeight();
                float indicatorPosition = calculateIndicatorPosition();

                // Check if clicking on green indicator
                if (y >= (trackHeight - indicatorPosition - indicatorHeight) &&
                    y <= (trackHeight - indicatorPosition)) {
                    // Clicking on indicator - start drag
                    isDraggingIndicator = true;
                    dragStartY = y;
                    dragStartScrollY = scrollPane.getScrollY();
                    log.debug("Start dragging indicator at y={}", y);
                    return true;
                } else {
                    // Clicking on gray track
                    trackClickTargetY = y;
                    float indicatorMiddle = trackHeight - indicatorPosition - (indicatorHeight / 2f);
                    if (y > indicatorMiddle) {
                        isScrollingTrackUp = true;
                        scrollUp(CLICK_SCROLL);
                    } else {
                        isScrollingTrackUp = false;
                        scrollDown(CLICK_SCROLL);
                    }
                    isScrollingFromTrack = true;
                    log.debug("Start scrolling from track click at y={}, direction up: {}", y, isScrollingTrackUp);
                    return true;
                }
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                if (isDraggingIndicator) {
                    float deltaY = dragStartY - y;
                    float maxScroll = scrollPane.getMaxY();
                    float indicatorHeight = calculateIndicatorHeight();
                    float availableTrackHeight = trackHeight - indicatorHeight;

                    if (availableTrackHeight > 0) {
                        float scrollRatio = maxScroll / availableTrackHeight;
                        float newScroll = dragStartScrollY + (deltaY * scrollRatio);
                        newScroll = Math.max(0, Math.min(maxScroll, newScroll));
                        scrollPane.setScrollY(newScroll);
                    }
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isDraggingIndicator) {
                    log.debug("Stop dragging indicator");
                    isDraggingIndicator = false;
                }
                if (isScrollingFromTrack) {
                    log.debug("Stop scrolling from track");
                    isScrollingFromTrack = false;
                }
            }
        });

        // Down button with hold support
        ImageButton.ImageButtonStyle downStyle = new ImageButton.ImageButtonStyle();
        downStyle.imageUp = arrowDownDrawable;
        downStyle.imageOver = arrowDownDrawable.tint(new Color(2f, 2f, 2f, 1f)); //hovering
        downStyle.imageDown = arrowDownDrawable.tint(new Color(0.7f, 0.7f, 0.7f, 1f)); //clicking
        ImageButton downButton = new ImageButton(downStyle);
        downButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                isScrollingDown = true;
                scrollDown(CLICK_SCROLL);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                isScrollingDown = false;
            }
        });

        scrollbarContainer.add(upButton).width(40f).height(buttonHeight);
        scrollbarContainer.row();
        scrollbarContainer.add(scrollArea).width(width).height(trackHeight);
        scrollbarContainer.row();
        scrollbarContainer.add(downButton).width(40f).height(buttonHeight);

        scrollbarContainer.setSize(40f, height);

        log.debug("UIScrollbar created with size: {}x{}", width, height);
    }

    /**
     * Calculate indicator height based on content ratio
     */
    private float calculateIndicatorHeight() {
        float maxScroll = scrollPane.getMaxY();
        if (maxScroll <= 0) {
            return trackHeight; // No scrolling needed, fill entire track
        }

        // Calculate visible ratio
        float scrollHeight = scrollPane.getHeight();
        float contentHeight = scrollHeight + maxScroll;
        float visibleRatio = scrollHeight / contentHeight;

        // Indicator height proportional to visible content
        float indicatorHeight = trackHeight * visibleRatio;

        // Minimum height to ensure it's always grabbable
        float minHeight = 30f;
        return Math.max(indicatorHeight, minHeight);
    }

    /**
     * Calculate indicator position from top
     */
    private float calculateIndicatorPosition() {
        float maxScroll = scrollPane.getMaxY();
        if (maxScroll <= 0) {
            return 0;
        }

        float currentScroll = scrollPane.getScrollY();
        float scrollRatio = currentScroll / maxScroll;

        float indicatorHeight = calculateIndicatorHeight();
        float availableTrackHeight = trackHeight - indicatorHeight;

        return scrollRatio * availableTrackHeight;
    }

    /**
     * Update method - call this in Scene's act() or render() to handle continuous scrolling
     */
    public void update(float delta) {
        // Update indicator position and size
        float indicatorHeight = calculateIndicatorHeight();
        float indicatorPosition = calculateIndicatorPosition();

        // Update indicator visual
        scrollIndicator.setHeight(indicatorHeight);
        scrollIndicator.setY(trackHeight - indicatorPosition - indicatorHeight);

        // Check if track scrolling should stop (reached target)
        if (isScrollingFromTrack && hasReachedTarget()) {
            isScrollingFromTrack = false;
            scrollAccumulator = 0f;
            log.debug("Reached target position, stopping track scroll");
        }

        // Handle continuous scrolling when holding buttons or clicking track
        if (isScrollingUp) {
            scrollAccumulator += SCROLL_SPEED * delta;
            if (scrollAccumulator >= 1f) {
                float scrollAmount = (int) scrollAccumulator;
                scrollUp(scrollAmount);
                scrollAccumulator -= scrollAmount;
            }
        } else if (isScrollingDown) {
            scrollAccumulator += SCROLL_SPEED * delta;
            if (scrollAccumulator >= 1f) {
                float scrollAmount = (int) scrollAccumulator;
                scrollDown(scrollAmount);
                scrollAccumulator -= scrollAmount;
            }
        } else if (isScrollingFromTrack) {
            // Continuous scroll when holding on track (until reaching target)
            scrollAccumulator += SCROLL_SPEED * delta;
            if (scrollAccumulator >= 1f) {
                float scrollAmount = (int) scrollAccumulator;
                if (isScrollingTrackUp) {
                    scrollUp(scrollAmount);
                } else {
                    scrollDown(scrollAmount);
                }
                scrollAccumulator -= scrollAmount;
            }
        } else {
            scrollAccumulator = 0f;
        }
    }

    /**
     * Check if indicator center has reached the target position
     */
    private boolean hasReachedTarget() {
        float indicatorHeight = calculateIndicatorHeight();
        float indicatorPosition = calculateIndicatorPosition();
        float indicatorMiddle = trackHeight - indicatorPosition - (indicatorHeight / 2f);

        if (isScrollingTrackUp) {
            return indicatorMiddle >= trackClickTargetY;
        } else {
            return indicatorMiddle <= trackClickTargetY;
        }
    }

    private void scrollUp(float amount) {
        float currentScroll = scrollPane.getScrollY();
        float newScroll = Math.max(0, currentScroll - amount);
        scrollPane.setScrollY(newScroll);
    }

    private void scrollDown(float amount) {
        float currentScroll = scrollPane.getScrollY();
        float maxScroll = scrollPane.getMaxY();
        float newScroll = Math.min(maxScroll, currentScroll + amount);
        scrollPane.setScrollY(newScroll);
    }

    public Table getActor() {
        return scrollbarContainer;
    }
}
