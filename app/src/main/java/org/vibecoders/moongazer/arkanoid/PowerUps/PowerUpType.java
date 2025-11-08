package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public interface PowerUpType {
    void applyEffect(Arkanoid arkanoid);
    void removeEffect(Arkanoid arkanoid);
    int getDuration();
    String getName();
}
