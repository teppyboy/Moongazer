package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ActivePowerUpEffect {
    private PowerUpType effect;
    private long startTime;
    private int duration;
    private String effectType;

    public ActivePowerUpEffect(PowerUpType effect) {
        this.effect = effect;
        this.startTime = System.currentTimeMillis();
        this.duration = effect.getDuration();
        this.effectType = effect.getName();
    }

    public boolean hasExpired() {
        if (duration < 0) return false;
        return System.currentTimeMillis() - startTime >= duration;
    }

    public void removeEffect(Arkanoid arkanoid) {
        effect.removeEffect(arkanoid);
    }

    public void refreshDuration() {
        this.startTime = System.currentTimeMillis();
    }

    public PowerUpType getEffect() {
        return effect;
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