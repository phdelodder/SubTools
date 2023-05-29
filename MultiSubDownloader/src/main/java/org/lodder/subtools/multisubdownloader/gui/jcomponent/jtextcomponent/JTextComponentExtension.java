package org.lodder.subtools.multisubdownloader.gui.jcomponent.jtextcomponent;

import javax.swing.text.JTextComponent;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JTextComponentExtension {

    public <T extends JTextComponent> T editable(T textComponent, boolean editable) {
        textComponent.setEditable(editable);
        return textComponent;
    }
}
