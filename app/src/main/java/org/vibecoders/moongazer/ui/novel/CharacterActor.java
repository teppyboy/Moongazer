package org.vibecoders.moongazer.ui.novel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CharacterActor extends Image {
    public CharacterActor(TextureRegion baseReg) {
        super(baseReg);
        float ox = getWidth() * 0.5f;
        float oy = 0f;
        setOrigin(ox, oy);
    }
}
