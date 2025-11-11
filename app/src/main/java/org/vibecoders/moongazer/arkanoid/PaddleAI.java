package org.vibecoders.moongazer.arkanoid;

import java.util.List;

/**
 * Interface for AI that controls a paddle automatically.
 * All methods are called from the main game thread and do not need to be thread-safe.
 */
public interface PaddleAI {
    /**
     * Update the AI logic and control the paddle.
     * Called once per game frame after physics updates but before rendering.
     *
     * @param paddle The paddle to control (current state, may be null)
     * @param balls List of balls in play (read-only, not null)
     * @param powerUps List of active powerups (read-only, not null)
     */
    void update(Paddle paddle, List<Ball> balls, List<PowerUp> powerUps);

    /**
     * Enable or disable the AI.
     *
     * @param enabled true to enable AI, false to disable
     */
    void setEnabled(boolean enabled);

    /**
     * Check if the AI is currently enabled.
     *
     * @return true if AI is enabled, false otherwise
     */
    boolean isEnabled();
}