package org.lodder.subtools.multisubdownloader.gui.jcomponent.component;

import java.awt.Component;
import java.awt.event.MouseListener;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ComponentExtension {

    public <T extends Component> T withMouseListener(T component, MouseListener listener) {
        component.addMouseListener(listener);
        return component;
    }
}
