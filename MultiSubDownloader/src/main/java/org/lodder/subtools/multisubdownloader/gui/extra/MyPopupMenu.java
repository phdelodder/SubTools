package org.lodder.subtools.multisubdownloader.gui.extra;

import java.awt.Component;
import java.awt.Point;

import javax.swing.JPopupMenu;

public class MyPopupMenu extends JPopupMenu {

    /**
     *
     */
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
