package org.vibecoders.moongazer.ui;

import org.vibecoders.moongazer.managers.Assets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Slider.SliderStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UISlider {
    public Slider slider;

    /**
     * Constructs a new slider with predefined textures for background and knob.
     * The slider ranges from 0 to 1 with 0.01 step size and is initially set to 1.
     */
    public UISlider() {
        Texture sliderBgTexture = Assets.getAsset("textures/ui/UI_SliderBg2.png", Texture.class);
        Texture sliderKnobTexture = Assets.getAsset("textures/ui/UI_SliderKnob.png", Texture.class);
        Texture sliderKnobOverTexture = Assets.getAsset("textures/ui/UI_SliderBg.png", Texture.class);

        SliderStyle sliderStyle = new SliderStyle();
        sliderStyle.background = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));
        sliderStyle.knob = new TextureRegionDrawable(new TextureRegion(sliderKnobTexture));
        sliderStyle.knobBefore = new TextureRegionDrawable(new TextureRegion(sliderKnobOverTexture));
        sliderStyle.knobAfter = new TextureRegionDrawable(new TextureRegion(sliderBgTexture));

        slider = new Slider(0f, 1f, 0.01f, false, sliderStyle);
        slider.setValue(1f);
        slider.setProgrammaticChangeEvents(true);
    }

    /**
     * Sets the current value of the slider.
     *
     * @param value the value to set (between 0 and 1)
     */
    public void setValue(float value) {
        slider.setValue(value);
    }

    /**
     * Gets the current value of the slider.
     *
     * @return the current value (between 0 and 1)
     */
    public float getValue() {
        return slider.getValue();
    }

    /**
     * Sets the size of the slider.
     *
     * @param width the width of the slider
     * @param height the height of the slider
     */
    public void setSize(float width, float height) {
        slider.setSize(width, height);
    }

    /**
     * Sets the position of the slider.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     */
    public void setPosition(float x, float y) {
        slider.setPosition(x, y);
    }

    /**
     * Adds an event listener to the slider.
     *
     * @param listener the event listener to add
     */
    public void addListener(EventListener listener) {
        slider.addListener(listener);
    }

    /**
     * Sets the action to be executed when the slider value changes.
     *
     * @param action the runnable action to execute on value change
     */
    public void onChanged(Runnable action) {
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
    }
}
