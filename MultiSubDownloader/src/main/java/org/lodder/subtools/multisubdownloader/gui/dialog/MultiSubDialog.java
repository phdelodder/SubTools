package org.lodder.subtools.multisubdownloader.gui.dialog;

import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.Frame;
import java.awt.Rectangle;

public class MultiSubDialog extends JDialog {

    private static final long serialVersionUID = -2357021997104425566L;

    public MultiSubDialog(JFrame frame, String title, boolean modal) {
        super(frame);
        setTitle(title);
        setModal(modal);
    }

    public MultiSubDialog(String title, boolean modal) {
        super();
        setTitle(title);
        setModal(modal);
    }

    protected void setDialogLocation(Frame f) {
        Rectangle r = f.getBounds();
        int x = r.x + (r.width - getSize().width) / 2;
        int y = r.y + (r.height - getSize().height) / 2;
        setLocation(x, y);
    }

}
