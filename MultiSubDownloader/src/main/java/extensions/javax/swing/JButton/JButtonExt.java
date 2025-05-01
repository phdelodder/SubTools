package extensions.javax.swing.JButton;

import javax.swing.*;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class JButtonExt {

    public static @Self JButton defaultButtonFor(@This JButton abstractButton, JRootPane rootPane) {
        rootPane.setDefaultButton(abstractButton);
        return abstractButton;
    }
}
