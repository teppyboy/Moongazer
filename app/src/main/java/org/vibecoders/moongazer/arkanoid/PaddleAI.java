package org.vibecoders.moongazer.arkanoid;

import java.util.List;

/**
 * Interface for AI that controls a paddle automatically
 */
public interface PaddleAI {
    /**
     * Update the AI logic and control the paddle
     *
     * @param paddle The paddle to control
     * @param balls List of balls in play
     * @param powerUps List of active powerups
     */
    void update(Paddle paddle, List<Ball> balls, List<PowerUp> powerUps);

    /**
     * Enable or disable the AI
     *
     * @param enabled true to enable AI, false to disable
     */
    void setEnabled(boolean enabled);

    /**
     * Check if the AI is currently enabled
     *
     * @return true if AI is enabled, false otherwise
     */
    boolean isEnabled();
}

