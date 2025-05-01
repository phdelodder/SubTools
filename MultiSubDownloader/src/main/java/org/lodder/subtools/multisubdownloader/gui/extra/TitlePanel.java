package org.lodder.subtools.multisubdownloader.gui.extra;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

import manifold.ext.props.rt.api.val;
import net.miginfocom.swing.MigLayout;
import org.jspecify.annotations.Nullable;

public class TitlePanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;
    @val JPanel panel;

    public TitlePanel(String title,
        boolean useGrid=false,
        boolean fillContents=true,
        BoxModelProperties margin=new BoxModelProperties(),
        BoxModelProperties padding=new BoxModelProperties(),
        LayoutManager panelLayout=null,
        String panelColumnConstraints="") {

        super(new MigLayout("fillx, nogrid, " + margin.getInsets()));
        super.add(new JLabel(title));
        super.add(new JSeparator(), "growx, gapy 6, wrap");

        LayoutManager panelLayoutNew = panelLayout != null ? panelLayout : new MigLayout(
            (fillContents ? "fill," : "") + (useGrid ? "" : "nogrid,") + padding.getInsets(),
            panelColumnConstraints);
        super.add(this.panel = new JPanel(panelLayoutNew), "growx, span");
    }

    public JPanel addToPanel(Container parent, @Nullable Object constraints=null) {
        parent.addComponent(this, constraints);
        return panel;
    }
}
