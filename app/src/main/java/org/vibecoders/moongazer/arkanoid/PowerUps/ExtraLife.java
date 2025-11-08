package org.vibecoders.moongazer.arkanoid.PowerUps;

import org.vibecoders.moongazer.arkanoid.PowerUp;
import org.vibecoders.moongazer.scenes.arkanoid.Arkanoid;

public class ExtraLife extends PowerUp {
    public ExtraLife(float x, float y, float width, float height) {
        super(x, y, width, height);
        loadTexture("textures/ui/hearth.png");
    }

    @Override
    public void applyEffect(Arkanoid arkanoid) {
        if (arkanoid.lives < 3) {
            arkanoid.lives += 1;
        }
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
        return "Add live";
    }
}
