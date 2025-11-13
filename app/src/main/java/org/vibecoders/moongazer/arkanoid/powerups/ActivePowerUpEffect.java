package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ActivePowerUpEffect {
    private PowerUp powerUp;
    private long startTime;
    private int duration;
    private String effectType;

    /**
     * Constructs a new ActivePowerUpEffect.
     * @param powerUp The power-up to track
     */
    public ActivePowerUpEffect(PowerUp powerUp) {
        this.powerUp = powerUp;
        this.startTime = System.currentTimeMillis();
        this.duration = powerUp.getDuration();
        this.effectType = powerUp.getName();
    }

    /**
     * Checks if the power-up effect has expired.
     * @return true if expired, false otherwise
     */
    public boolean hasExpired() {
        if (duration < 0) return false;
        return System.currentTimeMillis() - startTime >= duration;
    }

    /**
     * Removes the power-up effect from the game.
     * @param arkanoid The game instance
     */
    public void removeEffect(Arkanoid arkanoid) {
        powerUp.removeEffect(arkanoid);
    }

    /**
     * Refreshes the duration of the power-up effect.
     */
    public void refreshDuration() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Gets the power-up object.
     * @return PowerUp object
     */
    public PowerUp getPowerUp() {
        return powerUp;
    }

    /**
     * Gets the type of effect.
     * @return Effect type name
     */
    public String getEffectType() {
        return effectType;
    }

    /**
     * Gets the remaining time for this effect.
     * @return Remaining time in seconds, or -1 if permanent
     */
    public float getRemainingTime() {
        if (duration < 0) return -1;
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.max(0, (duration - elapsed) / 1000f);
    }
}