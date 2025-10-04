package org.vibecoders.moongazer.ui.novel;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class ChoiceBox extends Group {
    public interface Listener {
        void onChoice(int idx);
    }

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
