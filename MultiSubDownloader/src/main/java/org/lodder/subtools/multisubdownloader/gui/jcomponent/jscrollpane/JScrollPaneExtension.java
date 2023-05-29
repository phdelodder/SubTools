package org.lodder.subtools.multisubdownloader.gui.jcomponent.jscrollpane;

import javax.swing.JScrollPane;

import java.awt.Component;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JScrollPaneExtension {

    public JScrollPane withViewportView(JScrollPane scrollPane, Component view) {
        scrollPane.setViewportView(view);
        return scrollPane;
    }
}
