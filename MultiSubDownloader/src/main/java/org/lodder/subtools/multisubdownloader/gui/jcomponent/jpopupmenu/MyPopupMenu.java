package org.lodder.subtools.multisubdownloader.gui.jcomponent.jpopupmenu;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

import manifold.ext.props.rt.api.var;

public class MyPopupMenu extends JPopupMenu {

    @Serial
    private static final long serialVersionUID = 1084650376633196066L;
    @var Point clickLocation;

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
        clickLocation = new Point(x, y);
    }

}
