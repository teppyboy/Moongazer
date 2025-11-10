package org.vibecoders.moongazer;

/**
 * Client configuration constants and default values
 * used throughout the Moongazer client application.
 */
public class Constants {

    /**
     * Window configuration.
     */
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final String WINDOW_TITLE = "Moongazer";

    /**
     * Arkanoid game configuration.
     */
    public static final float BRICK_WIDTH = 60f;
    public static final float BRICK_HEIGHT = 60f;
    public static final float BRICK_PADDING = 2f;
    public static final float COLLISION_COOLDOWN_TIME = 0.05f;
    public static final float PADDLE_WIDTH = 150f;
    public static final float PADDLE_HEIGHT = 50f;
    public static final float SIDE_PANEL_WIDTH = 250f;
    public static final float GAMEPLAY_AREA_WIDTH = WINDOW_WIDTH - (SIDE_PANEL_WIDTH * 2);
    public static final float HEART_ICON_SIZE = 50f;

    /**
     * Main menu configuration.
     */
    public static final float PARALLAX_STRENGTH = 20f;

    /**
     * Story mode configuration.
     */
    public static final float MAP_WIDTH = 550f;
    public static final float MAP_HEIGHT = 400f;
}
