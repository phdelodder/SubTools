package extensions.java.awt.Component;

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.function.Consumer;
import java.util.function.Predicate;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;
import org.jspecify.annotations.Nullable;

@UtilityClass
@Extension
public class ComponentExt {

    public static void setRecursive(@This Component component, Consumer<Component> consumer) {
        setRecursive(component, consumer, _ -> true);
    }

    public static void setRecursive(@This Component component, Consumer<Component> consumer,
            Predicate<Container> condition) {
        if (component != null) {
            consumer.accept(component);
            if (component instanceof Container container && condition.test(container)) {
                container.getComponents().forEach(child -> setRecursive(child, consumer, condition));
            }
        }
    }

    public static @Self Component mouseListener(@This Component component, @Nullable MouseListener listener) {
        component.addMouseListener(listener);
        return component;
    }

    public static @Self Component addTo(@This Component child, Container parent) {
        parent.addComponent(child);
        return child;
    }

    public static @Self Component addTo(@This Component child, Container parent, Object constraints) {
        parent.addComponent(child, constraints);
        return child;
    }

    public static @Self Component withSize(@This Component component, Dimension size) {
        component.size = size;
        component.minimumSize = size;
        component.maximumSize = size;
        component.preferredSize = size;
        return component;
    }

    public static @Self Component minSize(@This Component component, Dimension minSize) {
        component.minimumSize = minSize;
        return component;
    }

    public static @Self Component minSize(@This Component component, int minWidth, int minHeight) {
        return component.minSize(new Dimension(minWidth, minHeight));
    }

    public static @Self Component maxSize(@This Component component, Dimension maximumSize) {
        component.maximumSize = maximumSize;
        return component;
    }

    public static @Self Component maxSize(@This Component component, int maxWidth, int maxHeight) {
        return component.maxSize(new Dimension(maxWidth, maxHeight));
    }

    public static @Self Component font(@This Component component, Font font) {
        component.setFont(font);
        return component;
    }

    public static @Self Component focusable(@This Component component, boolean focusable) {
        component.setFocusable(focusable);
        return component;
    }
}
