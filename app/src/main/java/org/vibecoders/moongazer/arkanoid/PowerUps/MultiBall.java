package org.vibecoders.moongazer.arkanoid.powerups;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class MultiBall extends PowerUp {

    public MultiBall(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/arkanoid/perk2.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
    }

    @Override
    public void removeEffect(Arkanoid arkanoid) {
    }

    @Override
    public int getDuration() {
        return -1;
    }

    @Override
    public String getName() {
        return "Multi Ball";
    }
}
