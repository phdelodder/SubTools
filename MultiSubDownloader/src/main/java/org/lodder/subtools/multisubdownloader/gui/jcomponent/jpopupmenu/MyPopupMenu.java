package org.lodder.subtools.multisubdownloader.gui.jcomponent.jpopupmenu;

import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.Point;
import java.io.Serial;

public class MyPopupMenu extends JPopupMenu {

    @Serial
    private static final long serialVersionUID = 1084650376633196066L;
    private Point clickLocation;

    @Override
    public void show(Component invoker, int x, int y) {
        super.show(invoker, x, y);
        setClickLocation(new Point(x, y));
    }

    public void setClickLocation(Point clickLocation) {
        this.clickLocation = clickLocation;
    }

    public Point getClickLocation() {
        return clickLocation;
    }

}
