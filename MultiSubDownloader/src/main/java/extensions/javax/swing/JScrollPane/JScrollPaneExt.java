package extensions.javax.swing.JScrollPane;

import javax.swing.*;
import java.awt.*;

import lombok.experimental.UtilityClass;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.Self;
import manifold.ext.rt.api.This;

@UtilityClass
@Extension
public class JScrollPaneExt {

    public static @Self JScrollPane viewportView(@This JScrollPane scrollPane, Component view) {
        scrollPane.setViewportView(view);
        return scrollPane;
    }

}
