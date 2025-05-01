package extensions.javax.swing.JComponent;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

import extensions.java.awt.Component.ComponentExt;
import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;
import org.jspecify.annotations.Nullable;

@UtilityClass
@Extension
public class JComponentExt {

    public static @Self JComponent enabled(@This JComponent component, boolean enabled) {
        component.setEnabled(enabled);
        return component;
    }

    public static @Self JComponent enabled(@This JComponent component) {
        return component.enabled(true);
    }

    public static @Self JComponent disabled(@This JComponent component) {
        return component.enabled(false);
    }

    public static @Self JComponent toolTipText(@This JComponent component, String text) {
        component.setToolTipText(text);
        return component;
    }

    public static @Self JComponent hidden(@This JComponent component) {
        return component.visible(false);
    }

    public static @Self JComponent visible(@This JComponent component) {
        return component.visible(true);
    }

    public static @Self JComponent visible(@This JComponent component, boolean visible) {
        component.setVisible(visible);
        return component;
    }

    public static @Self JComponent background(@This JComponent component, @Nullable Color background) {
        component.background = background;
        return component;
    }

    public static void setEnabledRecursive(@This JComponent component, boolean enabled) {
        ComponentExt.setRecursive(component, c -> c.setEnabled(enabled));
    }

    public static @Self JComponent enabledRecursive(@This JComponent component, boolean enabled) {
        setEnabledRecursive(component, enabled);
        return component;
    }

    public static @Self JComponent withToolTipText(@This JComponent component, String text) {
        component.setToolTipText(text);
        return component;
    }

    public static @Self JComponent border(@This JComponent component, Border border) {
        component.setBorder(border);
        return component;
    }
}
