package org.vibecoders.moongazer.ui.dialogue;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ChoiceBox extends Group {
    public interface Listener {
        /**
         * Called when a choice is selected.
         *
         * @param idx the index of the selected choice
         */
        void onChoice(int idx);
    }

    /**
     * Constructs a choice box with multiple option buttons.
     *
     * @param font the font to use for button text
     * @param options the array of option texts
     * @param listener the listener to be notified when a choice is selected
     */
    public ChoiceBox(BitmapFont font, String[] options, Listener listener) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        VerticalGroup col = new VerticalGroup().space(8);

        for (int i = 0; i < options.length; i++) {
            final int idx = i;
            TextButton b = new TextButton(options[i], style);
            b.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    listener.onChoice(idx);
                }
            });
            col.addActor(b);
        }

        addActor(col);
        col.pack();
    }
}
