package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.lodder.subtools.sublibrary.util.JComponentExtension;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;

import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JComponentExtension.class })
public class PanelCheckBox extends JPanel {
    private static final long serialVersionUID = 1L;
    private final JCheckBox chk;
    private final JPanel panel;

    public PanelCheckBox(JCheckBox chk) {
        this(chk, "wrap");
    }

    public PanelCheckBox(JCheckBox chk, Object constraints) {
        this(new MigLayout("insets 0, gap 0 0 0 0, novisualpadding", "[grow, nogrid]"), chk, constraints);
    }

    public PanelCheckBox(LayoutManager layout, JCheckBox chk, Object constraints) {
        super(layout);
        this.panel = new JPanel(new MigLayout("insets 0, novisualpadding", "[grow, nogrid]"))
                .addComponent("dock west, gap 10 10 0 0", new JSeparator(SwingConstants.VERTICAL));
        super.add(chk, constraints);
        super.add(panel, constraints);
        this.chk = chk;
        chk.addCheckedChangeListener(this::setEnabledChildren).setSelected(true);
    }

    @Override
    public Component add(Component comp) {
        return panel.add(comp);
    }

    @Override
    public void add(Component comp, Object constraints) {
        panel.add(comp, constraints);
    }

    public <T extends Container, S extends Container> PanelCheckBox addComponent(T component, S child, Object constraints) {
        return this.addComponent(child, constraints);
    }

    public <T extends Container, S extends Container> PanelCheckBox addComponent(T component, Object constraints, S child) {
        return this.addComponent(constraints, child);
    }

    private void setEnabledChildren(boolean enabled) {
        panel.setRecursive(c -> c.setEnabled(enabled), c -> !(c instanceof PanelCheckBox));
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled != this.isEnabled()) {
            super.setEnabled(enabled);
            chk.setEnabled(enabled);
            if (isSelected()) {
                setEnabledChildren(enabled);
            }
        }
    }

    public boolean isSelected() {
        return chk.isSelected();
    }
}
