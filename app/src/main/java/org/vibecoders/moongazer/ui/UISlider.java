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

    public void setValue(float value) {
        slider.setValue(value);
    }

    public float getValue() {
        return slider.getValue();
    }

    public void setSize(float width, float height) {
        slider.setSize(width, height);
    }

    public void setPosition(float x, float y) {
        slider.setPosition(x, y);
    }

    public void addListener(EventListener listener) {
        slider.addListener(listener);
    }

    public void onChanged(Runnable action) {
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
    }
}
