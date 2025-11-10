package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ActivePowerUpEffect {
    private PowerUp powerUp;
    private long startTime;
    private int duration;
    private String effectType;

    public ActivePowerUpEffect(PowerUp powerUp) {
        this.powerUp = powerUp;
        this.startTime = System.currentTimeMillis();
        this.duration = powerUp.getDuration();
        this.effectType = powerUp.getName();
    }

    public boolean hasExpired() {
        if (duration < 0) return false;
        return System.currentTimeMillis() - startTime >= duration;
    }

    public void removeEffect(Arkanoid arkanoid) {
        powerUp.removeEffect(arkanoid);
    }

    public void refreshDuration() {
        this.startTime = System.currentTimeMillis();
    }

    public PowerUp getPowerUp() {
        return powerUp;
    }

    public String getEffectType() {
        return effectType;
    }

    public float getRemainingTime() {
        if (duration < 0) return -1;
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.max(0, (duration - elapsed) / 1000f);
    }
}