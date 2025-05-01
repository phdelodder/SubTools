package extensions.javax.swing.JSlider;

import javax.swing.*;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class JSliderExt {

    public static @Self JSlider minimum(@This JSlider slider, int minimum) {
        slider.setMinimum(minimum);
        return slider;
    }

    public static @Self JSlider maximum(@This JSlider slider, int maximum) {
        slider.setMaximum(maximum);
        return slider;
    }
}
