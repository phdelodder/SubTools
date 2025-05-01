package extensions.javax.swing.JTextField;

import javax.swing.*;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class JTextFieldExt {

    public static @Self JTextField columns(@This JTextField textField, int columns) {
        textField.setColumns(columns);
        return textField;
    }
}
