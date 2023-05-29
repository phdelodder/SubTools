package org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import java.awt.Component;
import java.awt.Container;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JComponentExtension {

    public <T extends JComponent> T withEnabled(T component, boolean enabled) {
        component.setEnabled(enabled);
        return component;
    }

    public <T extends JComponent> T withEnabled(T component) {
        return withEnabled(component, true);
    }

    public <T extends JComponent> T withDisabled(T component) {
        return withEnabled(component, false);
    }

    public <T extends Container, S extends Container> T addComponent(T component, S child) {
        component.add(child);
        return component;
    }

    public <T extends Container, S extends Container> T addComponent(T component, S child, Object constraints) {
        component.add(child, constraints);
        return component;
    }

    public <T extends Container, S extends Container> S addTo(S child, T parent) {
        parent.add(child);
        return child;
    }

    public <T extends Container, S extends Container> S addTo(S child, T parent, Object constraints) {
        parent.add(child, constraints);
        return child;
    }


    public <T extends Container, S extends Container> T addComponent(T component, Object constraints, S child) {
        component.add(child, constraints);
        return component;
    }


    public void setEnabledRecursive(Component component, boolean enabled) {
        setRecursive(component, c -> c.setEnabled(enabled));
    }

    public void setRecursive(Component component, Consumer<Component> consumer) {
        setRecursive(component, consumer, c -> true);
    }

    public void setRecursive(Component component, Consumer<Component> consumer, Predicate<Container> condition) {
        if (component != null) {
            consumer.accept(component);
            if (component instanceof Container container && condition.test(container)) {
                Arrays.stream(container.getComponents()).forEach(child -> setRecursive(child, consumer, condition));
            }
        }
    }

    public <T extends Component> T enabledRecursive(T component, boolean enabled) {
        setEnabledRecursive(component, enabled);
        return component;
    }

    public JScrollPane scrollPane(JScrollPane scrollPane, Component view) {
        scrollPane.setViewportView(view);
        return scrollPane;
    }
}
