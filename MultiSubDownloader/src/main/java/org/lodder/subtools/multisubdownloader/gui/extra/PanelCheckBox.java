package org.lodder.subtools.multisubdownloader.gui.extra;

import java.io.Serial;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JComponentExtension.class, JCheckBoxExtension.class })
public class PanelCheckBox extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;
    private final JCheckBox checkbox;

    @Getter
    private final JPanel panel;

    private PanelCheckBox(JCheckBox checkbox, boolean panelOnNewLine, LayoutManager panelLayout, boolean addVerticalSeparator, int leftGap) {
        super(new MigLayout("insets 0, novisualpadding, fillx"));
        this.checkbox = checkbox;
        this.panel = new JPanel(panelLayout) {
            private static final long serialVersionUID = 1L;

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

    public static BuilderPanelNewLineIntf checkbox(JCheckBox checkbox) {
        return new Builder(checkbox);
    }

    public interface BuilderPanelNewLineIntf {
        BuilderSeparatorIntf panelOnNewLine();

        BuilderOtherIntf panelOnSameLine();
    }

    public interface BuilderSeparatorIntf extends BuilderOtherIntf {
        BuilderOtherIntf addVerticalSeparator();
    }

    public interface BuilderOtherIntf {
        BuilderOtherIntf leftGap(int leftGap);

        BuilderOtherIntf panelLayout(LayoutManager panelLayout);

        JPanel addTo(JComponent component);

        JPanel addTo(JComponent component, Object constraints);

        PanelCheckBox build();
    }

    @RequiredArgsConstructor
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder implements
            BuilderPanelNewLineIntf,
            BuilderSeparatorIntf,
            BuilderOtherIntf {
        private final JCheckBox checkbox;
        private boolean panelOnNewLine;
        private LayoutManager panelLayout = new MigLayout("insets 0, novisualpadding, fillx");
        private boolean addVerticalSeparator;
        private int leftGap = 20;

        @Override
        public Builder panelOnNewLine() {
            return panelOnNewLine(true);
        }

        @Override
        public Builder panelOnSameLine() {
            return panelOnNewLine(false);
        }

        @Override
        public Builder addVerticalSeparator() {
            return addVerticalSeparator(true);
        }

        @Override
        public JPanel addTo(JComponent component) {
            return addTo(component, "");
        }

        @Override
        public JPanel addTo(JComponent component, Object constraints) {
            PanelCheckBox panelCheckBox = build();
            component.add(panelCheckBox, constraints);
            return panelCheckBox.getPanel();
        }

        @Override
        public PanelCheckBox build() {
            return new PanelCheckBox(checkbox, panelOnNewLine, panelLayout, addVerticalSeparator, leftGap);
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
}
