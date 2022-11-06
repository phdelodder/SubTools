package org.lodder.subtools.sublibrary.util;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Component;
import java.awt.Container;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JComponentExtension {

    public JCheckBox addCheckedChangeListener(JCheckBox checkBox, Consumer<Boolean> listener) {
        checkBox.addItemListener(
                e -> {
                    JCheckBox chk = (JCheckBox) e.getSource();
                    if (chk.isEnabled()) {
                        listener.accept(chk.isSelected());
                    }
                });
        return checkBox;
    }

    public <T extends Container, S extends Container> T addComponent(T component, S child) {
        component.add(child);
        return component;
    }

    public <T extends Container, S extends Container> T addComponent(T component, S child, Object constraints) {
        component.add(child, constraints);
        return component;
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

    public <T extends JTextField> T columns(T component, int columns) {
        component.setColumns(columns);
        return component;
    }

    public <T extends Component> T enabled(T component, boolean enabled) {
        component.setEnabled(enabled);
        return component;
    }

    public JScrollPane scrollPane(JScrollPane scrollPane, Component view) {
        scrollPane.setViewportView(view);
        return scrollPane;
    }

}
