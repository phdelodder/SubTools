package org.lodder.subtools.multisubdownloader.gui.jcomponent.jslider;

import javax.swing.JSlider;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JSliderExtension {

    public <T extends JSlider> T withMinimum(T slider, int minimum) {
        slider.setMinimum(minimum);
        return slider;
    }

    public <T extends JSlider> T withMaximum(T slider, int maximum) {
        slider.setMaximum(maximum);
        return slider;
    }
}
