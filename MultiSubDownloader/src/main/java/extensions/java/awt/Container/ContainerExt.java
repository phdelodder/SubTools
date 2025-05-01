package extensions.java.awt.Container;

import java.awt.*;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class ContainerExt {

    public static @Self Container addComponent(@This Container component, Component child) {
        component.add(child);
        return component;
    }

    public static @Self Container addComponent(@This Container component, Component child, Object constraints) {
        component.add(child, constraints);
        return component;
    }

    public static @Self Container addComponent(@This Container component, Object constraints, Component child) {
        component.add(child, constraints);
        return component;
    }

    public static @Self Container layout(@This Container container, LayoutManager mgr) {
        container.setLayout(mgr);
        return container;
    }
}
