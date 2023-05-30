package org.lodder.subtools.multisubdownloader.gui.jcomponent.container;

import java.awt.Container;
import java.awt.LayoutManager;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ContainerExtension {

    public <T extends Container> T layout(T container, LayoutManager mgr) {
        container.setLayout(mgr);
        return container;
    }
}
