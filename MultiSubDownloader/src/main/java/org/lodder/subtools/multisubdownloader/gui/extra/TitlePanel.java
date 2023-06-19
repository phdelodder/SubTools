package org.lodder.subtools.multisubdownloader.gui.extra;

import java.io.Serial;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcheckbox.JCheckBoxExtension;
import org.lodder.subtools.multisubdownloader.gui.jcomponent.jcomponent.JComponentExtension;

import java.awt.Container;
import java.awt.LayoutManager;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import net.miginfocom.swing.MigLayout;

@ExtensionMethod({ JComponentExtension.class, JCheckBoxExtension.class })
public class TitlePanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;
    @Getter
    private final JPanel panel;

    private TitlePanel(String title, LayoutManager panelLayout, int marginTop, int marginLeft, int marginBottom, int marginRight) {
        super(new MigLayout("fillx, nogrid, insets %s %s %s %s".formatted(getPadding(marginTop),
                getPadding(marginLeft), getPadding(marginBottom), getPadding(marginRight))));
        super.add(new JLabel(title));
        super.add(new JSeparator(), "growx, gapy 6, wrap");
        super.add(this.panel = new JPanel(panelLayout), "growx, span");
    }

    private static String getPadding(int padding) {
        return padding == -1 ? "n" : String.valueOf(padding);
    }

    public static BuilderOtherIntf title(String title) {
        return new Builder(title);
    }

    public interface BuilderOtherIntf {
        BuilderOtherIntf useGrid();

        BuilderOtherIntf fillContents(boolean fillContents);

        BuilderOtherIntf margin(int margin);

        BuilderOtherIntf margin(int top, int left, int bottom, int right);

        BuilderOtherIntf marginTop(int top);

        BuilderOtherIntf marginLeft(int left);

        BuilderOtherIntf marginBottom(int bottom);

        BuilderOtherIntf marginRight(int right);

        BuilderOtherIntf marginSides(int marginSide);

        BuilderOtherIntf panelLayout(LayoutManager panelLayout);

        BuilderOtherIntf panelColumnConstraints(String panelColumnConstraints);

        BuilderOtherIntf padding(int padding);

        BuilderOtherIntf padding(int top, int left, int bottom, int right);

        BuilderOtherIntf paddingTop(int top);

        BuilderOtherIntf paddingLeft(int left);

        BuilderOtherIntf paddingBottom(int bottom);

        BuilderOtherIntf paddingRight(int right);

        BuilderOtherIntf paddingSides(int paddingSide);

        JPanel addTo(Container component);

        JPanel addTo(Container component, Object constraints);
    }

    @RequiredArgsConstructor
    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder implements
            BuilderOtherIntf {
        private final String title;
        private boolean useGrid;
        private boolean fillContents = true;
        private int marginTop = -1;
        private int marginLeft = -1;
        private int marginBottom = -1;
        private int marginRight = -1;
        private int paddingTop = -1;
        private int paddingLeft = -1;
        private int paddingBottom = -1;
        private int paddingRight = -1;
        private LayoutManager panelLayout;
        private String panelColumnConstraints = "";

        @Override
        public Builder useGrid() {
            this.useGrid = true;
            return this;
        }

        @Override
        public Builder margin(int margin) {
            return margin(margin, margin, margin, margin);
        }

        @Override
        public Builder margin(int top, int left, int bottom, int right) {
            return marginTop(top).marginLeft(left).marginBottom(bottom).marginRight(right);
        }

        @Override
        public Builder marginSides(int marginSide) {
            return marginLeft(marginSide).marginRight(marginSide);
        }

        @Override
        public Builder padding(int padding) {
            return padding(padding, padding, padding, padding);
        }

        @Override
        public Builder padding(int top, int left, int bottom, int right) {
            return paddingTop(top).paddingLeft(left).paddingBottom(bottom).paddingRight(right);
        }

        @Override
        public Builder paddingSides(int paddingSide) {
            return paddingLeft(paddingSide).paddingRight(paddingSide);
        }

        @Override
        public JPanel addTo(Container component) {
            return addTo(component, "");
        }

        @Override
        public JPanel addTo(Container component, Object constraints) {
            if (panelLayout == null) {
                panelLayout = new MigLayout(
                        (fillContents ? "fill," : "") + (useGrid ? "" : "nogrid,") + "insets %s %s %s %s".formatted(getPadding(paddingTop),
                                getPadding(paddingLeft), getPadding(paddingBottom), getPadding(paddingRight)),
                        panelColumnConstraints);
            }
            TitlePanel titlePanel = new TitlePanel(title, panelLayout, marginTop, marginLeft, marginBottom, marginRight);
            component.add(titlePanel, constraints);
            return titlePanel.getPanel();

        }
    }
}
