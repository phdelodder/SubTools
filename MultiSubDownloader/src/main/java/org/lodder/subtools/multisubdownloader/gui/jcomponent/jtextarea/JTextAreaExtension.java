package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextarea;

import javax.swing.JTextArea;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JTextAreaExtension {

    public <T extends JTextArea> T autoscrolls(T textArea, boolean autoscrolls) {
        textArea.setAutoscrolls(autoscrolls);
        return textArea;
    }
}
