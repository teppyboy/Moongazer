package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class PremiumBallType implements PowerUpType{

    @Override
    public void applyEffect(Arkanoid arkanoid) {
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public String getName() {
        return "";
    }
}
