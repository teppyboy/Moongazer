package org.vibecoders.moongazer.ui.dialogue;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CharacterActor extends Image {
    /**
     * Constructs a character actor with the specified texture region.
     * Sets the origin to the bottom center for proper scaling and rotation.
     *
     * @param baseReg the texture region for the character
     */
    public CharacterActor(TextureRegion baseReg) {
        super(baseReg);
        float ox = getWidth() * 0.5f;
        float oy = 0f;
        setOrigin(ox, oy);
    }
}
