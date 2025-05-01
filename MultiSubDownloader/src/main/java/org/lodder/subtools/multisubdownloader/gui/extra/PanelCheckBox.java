package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.io.Serial;

import manifold.ext.props.rt.api.val;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

public class PanelCheckBox extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;
    private final JCheckBox checkbox;

    @val JPanel panel;

    public PanelCheckBox(JCheckBox checkbox,
        boolean panelOnNewLine,
        LayoutManager panelLayout=new MigLayout("insets 0, novisualpadding, fillx"),
        boolean addVerticalSeparator=false,
        int leftGap=20) {

        super(new MigLayout("insets 0, novisualpadding, fillx"));
        this.checkbox = checkbox;
        this.panel = new JPanel(panelLayout) {
            @Serial private static final long serialVersionUID = 1L;

            @Override
            protected void addImpl(Component comp, Object constraints, int index) {
                super.addImpl(comp, constraints, index);
                setEnabledChildren(comp, isSelected());
                PanelCheckBox.addContainerListener(comp, checkbox);
            }
        };
        if (addVerticalSeparator) {
            this.panel.addComponent("dock west, gap 10 10 0 0", new JSeparator(SwingConstants.VERTICAL));
        }
        super.addImpl(checkbox, panelOnNewLine ? "span" : "", -1);
        super.addImpl(panel, "span, growx, " + (addVerticalSeparator ? "" : "gapx " + leftGap), -1);
        checkbox.addCheckedChangeListener(selected -> setEnabledChildren(panel, selected));
        this.setRecursive(this::addContainerListener);
        setEnabledChildren(panel, isSelected());
    }

    private void addContainerListener(Component component) {
        addContainerListener(component, checkbox);
    }

    private static void addContainerListener(Component component, JCheckBox checkbox) {
        if (component instanceof Container container) {
            container.addContainerListener(new ContainerListener() {

                @Override
                public void componentRemoved(ContainerEvent e) {
                }

                @Override
                public void componentAdded(ContainerEvent e) {
                    Component component = e.getChild();
                    component.setEnabled(checkbox.isSelected());
                    if (component instanceof Container container) {
                        addContainerListener(container, checkbox);
                    }
                    component.setRecursive(c -> addContainerListener(c, checkbox));
                }
            });
        }
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        panel.add(comp, constraints, index);
        setEnabledChildren(comp, isSelected());
        comp.setRecursive(this::addContainerListener);
    }

    private void setEnabledChildren(Component component, boolean enabled) {
        component.setRecursive(c -> c.setEnabled(enabled));
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != this.isEnabled()) {
            super.setEnabled(enabled);
            checkbox.setEnabled(enabled);
            if (isSelected()) {
                setEnabledChildren(panel, enabled);
            }
        }
    }

    public boolean isSelected() {
        return checkbox.isSelected();
    }

    public JPanel addToPanel(Container parent, @Nullable Object constraints=null) {
        parent.addComponent(this, constraints);
        return panel;
    }
}
