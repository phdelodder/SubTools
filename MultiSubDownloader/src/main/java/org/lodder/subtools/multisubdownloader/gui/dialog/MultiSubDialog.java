package org.lodder.subtools.multisubdownloader.gui.dialog;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

import org.jspecify.annotations.Nullable;

public class MultiSubDialog extends JDialog {

    @Serial
    private static final long serialVersionUID = -2357021997104425566L;

    public MultiSubDialog(@Nullable JFrame frame=null, String title, boolean modal) {
        super(frame);
        setTitle(title);
        setModal(modal);
    }

    protected void setDialogLocation(Frame f) {
        Rectangle r = f.getBounds();
        int x = r.x + (r.width - size.width) / 2;
        int y = r.y + (r.height - size.height) / 2;
        setLocation(x, y);
    }

}
